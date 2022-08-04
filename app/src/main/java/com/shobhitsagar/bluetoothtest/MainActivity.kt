package com.shobhitsagar.bluetoothtest

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
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
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.edit_text)
        val button = findViewById<Button>(R.id.button)
        textView = findViewById(R.id.text_view)

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        /*connectToBtDevice()*/
        bluetoothAdapter?.let { ConnectThread(it.getRemoteDevice("94:B5:55:2D:29:32")).run() }

        button.setOnClickListener {
            Log.d(TAG, "onCreate: ${editText.text}")
            sendDataToBT(editText.text.toString())
        }
    }

    @SuppressLint("MissingPermission")
    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(MY_UUID)
        }

        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.let { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
//                manageMyConnectedSocket(socket)

                if (socket.isConnected) {
                    btSocket = socket
                    Toast.makeText(applicationContext, "Socket Created", Toast.LENGTH_SHORT).show()
                }
                else Toast.makeText(applicationContext, "Socket Failed", Toast.LENGTH_SHORT).show()
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }

    /*private inner class AcceptThread : Thread() {

        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord("NAME", MY_UUID)
        }

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    Log.d(TAG, "run: Starting to listen for connection...")
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e(TAG, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    btSocket = it
                    if (btSocket?.isConnected == true)
                        Toast.makeText(applicationContext, "Socket Created", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(applicationContext, "Socket Failed", Toast.LENGTH_SHORT).show()
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }*/


    /*private fun connectToBtDevice() {
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
    }*/

    private fun sendDataToBT(signal: String) {
        val ssidOutputStream: OutputStream = btSocket!!.outputStream
        ssidOutputStream.write(signal.toByteArray())

    }
}