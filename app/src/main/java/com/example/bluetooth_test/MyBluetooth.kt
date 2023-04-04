package com.example.bluetooth_test

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*


class MyBluetooth(private val appCompatActivity: AppCompatActivity) {
    private var bluetoothAdapter: BluetoothAdapter
    private lateinit var btSocket : BluetoothSocket

    // Serial port UUID
    // https://stackoverflow.com/questions/4632524/how-to-find-the-uuid-of-serial-port-bluetooth-device
    private val _myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

    // Tag for debug info
    private val _tag = MyBluetooth::class.qualifiedName

    // ActivityResultLauncher for requesting BT permissions with API < 33
    private var requestBluetooth = appCompatActivity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            //granted
        }else{
            //deny
        }
    }

    // ActivityResultLauncher for requesting BT permissions with API >= 33
    private val requestMultiplePermissions =
        appCompatActivity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d(_tag, "${it.key} = ${it.value}")
            }
        }

    init {
        //requestBTPermissions() // Seems not necessary here. Will be done later when needed.
        val bluetoothManager = appCompatActivity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
    }

    private fun requestBTPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT))
        }
        else {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
        }
    }

    private var _isConnected : Boolean = false
    val isConnected : Boolean
        get() {
            _isConnected = this::btSocket.isInitialized && btSocket.isConnected
            return _isConnected
        }

    fun connect(): Boolean {
        // Address discovered with 3rd party Bluetooth Scanner app
        // Probably unique address for each HC-05 device.
        val device = bluetoothAdapter.getRemoteDevice("98:DA:50:01:33:48")
        Log.d(_tag, "Connecting to ... $device")
        Toast.makeText(appCompatActivity.applicationContext, "Connecting...", Toast.LENGTH_SHORT).show()
        if (ActivityCompat.checkSelfPermission(
                appCompatActivity,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // We don't have permissions.
            Toast.makeText(appCompatActivity.applicationContext, "BLUETOOTH_CONNECT permission is not granted! Please allow, and try again.",Toast.LENGTH_LONG).show()
            requestBTPermissions()
            return false
        }
        Toast.makeText(appCompatActivity.applicationContext, "Connecting to ... ${device.name} mac: ${device.uuids[0]} address: ${device.address}", Toast.LENGTH_SHORT).show()
        bluetoothAdapter.cancelDiscovery()

        // Establish connection
        try {
            btSocket = device.createRfcommSocketToServiceRecord(_myUUID)
            /* Here is the part the connection is made, by asking the device to create a RfcommSocket (Unsecure socket I guess), It map a port for us or something like that */
            btSocket.connect()
            Log.d(_tag, "Connection made.")
            Toast.makeText(appCompatActivity.applicationContext, "Connection made.", Toast.LENGTH_SHORT).show()
            return true
        } catch (e: IOException) {
            try {
                btSocket.close()
            } catch (e2: IOException) {
                Log.d(_tag, "Unable to end the connection.\n" + e2.message)
                Toast.makeText(appCompatActivity.applicationContext, "Unable to end the connection.", Toast.LENGTH_SHORT).show()
                return false
            }

            Log.d(_tag, "Socket creation failed.\n" + e.message)
            Toast.makeText(appCompatActivity.applicationContext, "Socket creation failed.", Toast.LENGTH_SHORT).show()
            return false
        }

    }

    fun writeData(data: String): Boolean {
        if (!isConnected)
            return false
        var outStream = btSocket.outputStream
        try {
            outStream = btSocket.outputStream
        } catch (e: IOException) {
            Log.d(_tag, "Error before sending stuff", e)
            return false
        }
        val msgBuffer = data.toByteArray()

        try {
            outStream.write(msgBuffer)
        } catch (e: IOException) {
            Log.d(_tag, "Error while sending stuff", e)
            return false
        }
        return true
    }

    fun readData(): String {
        if (!isConnected)
            return ""
        var inStream = btSocket.inputStream
        try {
            inStream = btSocket.inputStream
        } catch (e: IOException) {
            Log.d(_tag, "Error before receiving stuff", e)
        }

        var s = ""

        try {
            while (inStream.available() > 0) {
                // https://developer.android.com/reference/java/io/InputStream#read()
                s += inStream.read().toChar()
            }
        } catch (e: IOException) {
            Log.d(_tag, "Error while receiving stuff", e)
        } finally {
            Log.i(_tag, "INFO: Read string: $s")
            return s
        }
    }



}