package com.example.bluetooth_test

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*

// Permission code from:
// https://stackoverflow.com/questions/67722950/android-12-new-bluetooth-permissions

class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var btSocket: BluetoothSocket
    // Serial port UUID
    // https://stackoverflow.com/questions/4632524/how-to-find-the-uuid-of-serial-port-bluetooth-device
    private val _myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    private val _maxMessageLength = 50
    private val _tag = MainActivity::class.qualifiedName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(applicationContext, "Starting...", Toast.LENGTH_SHORT).show()

        requestBTPermissions()

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter


        setContentView(R.layout.activity_main)

        val connectButton: Button = findViewById(R.id.btnConnect)
        connectButton.setOnClickListener {
            connect()
        }

        val sendAButton: Button = findViewById(R.id.btnSendA)
        sendAButton.setOnClickListener {
            writeData("a")
        }
        val sendBButton: Button = findViewById(R.id.btnSendB)
        sendBButton.setOnClickListener {
            writeData("b")
        }

        val receiveButton: Button = findViewById(R.id.btnReceive)
        receiveButton.setOnClickListener {
            val s = readData()
            Toast.makeText(applicationContext, "Received text: $s", Toast.LENGTH_LONG).show()
        }
    }

    private fun requestBTPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT))
        }
        else{
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
        }


    }

    // ActivityResultLauncher for requesting BT permissions with API < 33
    private var requestBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            //granted
        }else{
            //deny
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d(_tag, "${it.key} = ${it.value}")
            }
        }

    private fun connect() {
        // Address discovered with 3rd party Bluetooth Scanner app
        val device = bluetoothAdapter.getRemoteDevice("98:DA:50:01:33:48")
        Log.d(_tag, "Connecting to ... $device")
        Toast.makeText(applicationContext, "Connecting...", Toast.LENGTH_SHORT).show()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(applicationContext, "BLUETOOTH_CONNECT permission is not granted!",Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(applicationContext, "Connecting to ... ${device.name} mac: ${device.uuids[0]} address: ${device.address}", Toast.LENGTH_LONG).show()
        bluetoothAdapter.cancelDiscovery()
        try {
            btSocket = device.createRfcommSocketToServiceRecord(_myUUID)
            /* Here is the part the connection is made, by asking the device to create a RfcommSocket (Unsecure socket I guess), It map a port for us or something like that */
            btSocket.connect()
            Log.d(_tag, "Connection made.")
            Toast.makeText(applicationContext, "Connection made.", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            try {
                btSocket.close()
            } catch (e2: IOException) {
                Log.d(_tag, "Unable to end the connection.")
                Toast.makeText(applicationContext, "Unable to end the connection.", Toast.LENGTH_SHORT).show()
            }

            Log.d(_tag, "Socket creation failed.")
            Toast.makeText(applicationContext, "Socket creation failed.", Toast.LENGTH_SHORT).show()
        }

    }

    private fun writeData(data: String) {
        var outStream = btSocket.outputStream
        try {
            outStream = btSocket.outputStream
        } catch (e: IOException) {
            Log.d(_tag, "Error before sending stuff", e)
        }
        val msgBuffer = data.toByteArray()

        try {
            outStream.write(msgBuffer)
        } catch (e: IOException) {
            Log.d(_tag, "Error while sending stuff", e)
        }
    }

    private fun readData(): String {

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