package com.example.bluetooth_test


import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


// Permission code from:
// https://stackoverflow.com/questions/67722950/android-12-new-bluetooth-permissions

class MainActivity : AppCompatActivity() {

    private val _tag = MainActivity::class.qualifiedName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(_tag, "Starting...")

        setContentView(R.layout.activity_main)

        var myBluetooth = MyBluetooth(this)

        val sendAButton: Button = findViewById(R.id.btnSendA)
        sendAButton.setOnClickListener { myBluetooth.writeData("a") }

        val sendBButton: Button = findViewById(R.id.btnSendB)
        sendBButton.setOnClickListener { myBluetooth.writeData("b") }

        val receiveButton: Button = findViewById(R.id.btnReceive)
        receiveButton.setOnClickListener {
            val s = myBluetooth.readData()
            Toast.makeText(applicationContext, "Received text: $s", Toast.LENGTH_LONG).show()
        }

        val connectButton: Button = findViewById(R.id.btnConnect)
        connectButton.setOnClickListener {
            val success = myBluetooth.connect()
            sendAButton.isEnabled = success
            sendBButton.isEnabled = success
            receiveButton.isEnabled = success
        }


    }
}