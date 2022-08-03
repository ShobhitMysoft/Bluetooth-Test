package com.shobhitsagar.bluetoothtest

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.io.OutputStream
import java.util.*

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    private var textView: TextView? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var btSocket: BluetoothSocket? = null
    private val mUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.edit_text)
        val button = findViewById<Button>(R.id.button)
        textView = findViewById(R.id.text_view)

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        connectToBtDevice()

        button.setOnClickListener {
            btSocket!!.outputStream.write(editText.text.toString().toByteArray())
        }
    }


    private fun connectToBtDevice() {
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        val remoteDevice = btAdapter.getRemoteDevice("94:B5:55:2D:29:32")

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Permission not granted")
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
        } else return

        btSocket = remoteDevice.createRfcommSocketToServiceRecord(mUUID)

        try {
            Log.i(TAG, "connectToBtDevice: Try ${Calendar.getInstance().time}")
            textView?.text = "Trying to connect to bluetooth..."
            btSocket!!.connect()
            Log.i(TAG, "connectToBtDevice: Try complete ${Calendar.getInstance().time}")
        } catch (e: Exception) {
            Log.e(TAG, "onCreate: Bluetooth Error", e)
        }

        if (btSocket!!.isConnected) {
            textView?.text = "Bluetooth is Connected"
        } else {
            textView?.text = "Bluetooth is not Connected"

        }
    }

    private var requestBluetooth = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            //granted
        }else{
            //deny
        }
    }
}