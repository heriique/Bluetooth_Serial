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

class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var btSocket: BluetoothSocket
    // Serial port UUID
    private val myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")


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
                Log.d("test006", "${it.key} = ${it.value}")
            }
        }

    private fun connect() {

        //val device = bluetoothAdapter.getRemoteDevice("98:D3:32:71:17:DE")
        val device = bluetoothAdapter.getRemoteDevice("98:DA:50:01:33:48")
        Log.d("", "Conneeeecting to ... $device")
        Toast.makeText(applicationContext, "Connecting...", Toast.LENGTH_LONG).show()
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
            btSocket = device.createRfcommSocketToServiceRecord(myUUID)
            /* Here is the part the connection is made, by asking the device to create a RfcommSocket (Unsecure socket I guess), It map a port for us or something like that */
            btSocket.connect()
            Log.d("", "Connection made.")
            Toast.makeText(applicationContext, "Connection made.", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            try {
                btSocket.close()
            } catch (e2: IOException) {
                Log.d("", "Unable to end the connection")
                Toast.makeText(applicationContext, "Unable to end the connection", Toast.LENGTH_SHORT).show()
            }

            Log.d("", "Socket creation failed")
            Toast.makeText(applicationContext, "Socket creation failed", Toast.LENGTH_SHORT).show()
        }

        //beginListenForData()
        /* this is a method used to read what the Arduino says for example when you write Serial.print("Hello world.") in your Arduino code */
    }

    private fun writeData(data: String) {
        var outStream = btSocket.outputStream
        try {
            outStream = btSocket.outputStream
        } catch (e: IOException) {
            //Log.d(FragmentActivity.TAG, "Bug BEFORE Sending stuff", e)
        }
        val msgBuffer = data.toByteArray()

        try {
            outStream.write(msgBuffer)
        } catch (e: IOException) {
            //Log.d(FragmentActivity.TAG, "Bug while sending stuff", e)
        }
    }
}

class Dice(private val numSides: Int) {
    fun roll(): Int {
        return (1..numSides).random()
    }
}