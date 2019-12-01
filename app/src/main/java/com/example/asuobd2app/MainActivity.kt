package com.example.asuobd2app

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlin.system.exitProcess

/**
 * Tested with Google Pixel 2 with Android 10.0.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Arbitrary integer to request enable BT.
     */
    private final var REQUEST_ENABLE_BT = 1

    /**
     * Code run when the app starts up on the device.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * Default on-board Bluetooth adapter.
         */
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        val successText = "Device supports Bluetooth!"

        val failureText = "Device does not support Bluetooth..."

        // Check if the device supports Bluetooth. Close app if it does not.
        if( bluetoothAdapter == null) {

            /**
             * Small message that alerts user that the device does not support Bluetooth.
             */
            val failToast = Toast.makeText(applicationContext, failureText, Toast.LENGTH_LONG)
            failToast.show() // Show message.

            val timer = object: CountDownTimer(8000,1000) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    /**
                    * Move the process task to the back of the queue.
                    */
                    moveTaskToBack(true)

                    /**
                     * Exit the app with exit failure return value.
                     */
                    exitProcess(-1)
                }
            }
            timer.start()

            /**
             * Move the process task to the back of the queue.
             */
            //moveTaskToBack(true)

            /**
             * Exit the app with exit failure return value.
             */
            //exitProcess(-1)
        }

        val successToast = Toast.makeText(applicationContext, successText, Toast.LENGTH_LONG)
        successToast.show()

        // Check if the Bluetooth adapter is disabled.
        if (bluetoothAdapter != null) {
            if( bluetoothAdapter.isEnabled ) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                // Generate pop-up to allow for bluetooth in-app.
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        }


        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver,filter)

        if(bluetoothAdapter!!.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }
        bluetoothAdapter.startDiscovery()

        // Start then stop discovery to test that it works.
        bluetoothAdapter.cancelDiscovery()

        var doneToast = Toast.makeText(applicationContext, "Stopped looking!", Toast.LENGTH_LONG)
        doneToast.show()

    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    /**
                     * Device has been found. Get the information about the device. It will
                     * never be null since we check earlier if a device exists and quits app
                     * if Bluetooth device does not exist.
                     */
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    val deviceName = device.name
                    //val deviceHardwareAddress = device.address // MAC address.

                    val connectToast = Toast.makeText(applicationContext, "Connected to " + deviceName
                        + ".", Toast.LENGTH_LONG)
                    connectToast.show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        /**
         * Unregister the receiver.
         */
        unregisterReceiver(receiver)

    }
}
