package com.example.asuobd2app


import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.displayMetrics
import org.jetbrains.anko.toast
import kotlin.system.exitProcess

/**
 * Tested with Google Pixel 2 with Android 10.0.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Device's on-board Bluetooth adapter.
     */
    private var bluetoothAdapter: BluetoothAdapter? = null

    /**
     * Set of all Bluetooth devices connected to the Bluetooth adapter.
     */
    private lateinit var pairedDevices: Set<BluetoothDevice>

    /**
     * Arbitrary number used to enable Bluetooth.
     */
    private val REQUEST_ENABLE_BLUETOOTH = 1

    /**
     * When moving data, will use EXTRA_ADDRESS to access data.
     */
    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
    }

    /**
     * Code run when the app starts up on the device.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if( bluetoothAdapter == null ) {

            // Tell the user that Bluetooth not supported and exit.
            toast("Bluetooth not supported...")
            return
        }

        // Specify the adapter will not be null and check if the adapter is enabled.
        if( !bluetoothAdapter!!.isEnabled) {

            // Prompt user to enable Bluetooth on device.
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent,REQUEST_ENABLE_BLUETOOTH)
        }

        /**
         * Lambda expression that will list paired Bluetooth devices when REFRESH
         * is tapped.
         */
        select_device_refresh.setOnClickListener {
            pairedDeviceList()
        }


    }

    private fun pairedDeviceList() {

        /*
         * Assign the connected devices to the pairedDevices set.
         */
        pairedDevices = bluetoothAdapter!!.bondedDevices

        val list: ArrayList<BluetoothDevice> = ArrayList()

        /*
         * Check if there are devices.
         */
        if(!pairedDevices.isEmpty()) {

            // Iterate through paired devices and add them to list.
            for (device: BluetoothDevice in pairedDevices) {
                list.add(device)
            }
        } else {
            toast("No paired Bluetooth devices found.")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,list)

        select_device_list.adapter = adapter

        select_device_list.onItemClickListener = AdapterView.OnItemClickListener{_, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address

            val intent = Intent(this, displayMetrics::class.java)
            intent.putExtra(EXTRA_ADDRESS,address)
            startActivity(intent)
        }
    }

    /**
     * Function run to report to user if Bluetooth was enabled properly.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if(resultCode == Activity.RESULT_OK) {
                if(bluetoothAdapter!!.isEnabled) {
                    toast("Bluetooth has been enabled!")
                } else {
                    toast("Bluetooth has been disabled!")
                }
            } else if(resultCode == Activity.RESULT_CANCELED){
                toast("Bluetooth enabling has been canceled.")
            }
        }
    }
}