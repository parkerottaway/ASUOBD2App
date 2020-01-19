package com.example.asuobd2app


import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.displayMetrics
import org.jetbrains.anko.toast

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
     * When moving data, will use EXTRA_ADDRESS and EXTRA_NAME to access data.
     */
    companion object {
        const val EXTRA_ADDRESS: String = "Device_address"
        const val EXTRA_NAME:    String = "Device_name"
    }

    /**
     * Code run when the app starts up on the device.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if( this.bluetoothAdapter == null ) {

            // Tell the user that Bluetooth not supported and exit.
            this.toast("Bluetooth not supported...")
            return
        }

        // Specify the adapter will not be null and check if the adapter is enabled.
        if( !this.bluetoothAdapter!!.isEnabled) {

            // Prompt user to enable Bluetooth on device.
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            this.startActivityForResult(enableBluetoothIntent, this.REQUEST_ENABLE_BLUETOOTH)
        }

        /**
         * Lambda expression that will list paired Bluetooth devices when REFRESH
         * is tapped.
         */
        this.select_device_refresh.setOnClickListener {
            this.pairedDeviceList()
        }

    }

    private fun pairedDeviceList() {

        /*
         * Assign the connected devices to the pairedDevices set.
         */
        this.pairedDevices = this.bluetoothAdapter!!.bondedDevices

        val btNameList: ArrayList<String> = ArrayList()

        val list: ArrayList<BluetoothDevice> = ArrayList()

        /*
         * Check if there are devices.
         */
        if(this.pairedDevices.isNotEmpty()) {

            // Iterate through paired devices and add them to list.
            for (device: BluetoothDevice in this.pairedDevices) {
                btNameList.add(device.name + "\n" + device.address)
                list.add(device)
            }
        } else {
            this.toast("No paired Bluetooth devices found.")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,btNameList)

        this.select_device_list.adapter = adapter

        this.select_device_list.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address
            val name: String = device.name

            // Transition to the DisplayMetrics activity.
            val intent = Intent(this, DisplayMetrics::class.java)
            val data = Bundle()
            data.putString(EXTRA_NAME,name)
            data.putString(EXTRA_ADDRESS,address)
            //intent.putExtra(EXTRA_ADDRESS,address)
            intent.putExtras(data)
            this.startActivity(intent)
            finish() // Prevent user from changing the device after they have connected.
        }
    }

    /**
     * Function run to report to user if Bluetooth was enabled properly.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == this.REQUEST_ENABLE_BLUETOOTH) {
            if(resultCode == Activity.RESULT_OK) {
                if(this.bluetoothAdapter!!.isEnabled) {
                    this.toast("Bluetooth has been enabled!")
                    this.pairedDeviceList()
                } else {
                    this.toast("Bluetooth has been disabled!")
                }
            } else if(resultCode == Activity.RESULT_CANCELED){
                this.toast("Bluetooth enabling has been canceled.")
            }
        }
    }
}