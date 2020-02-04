package com.example.asuobd2app

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.display_metrics.*
import org.jetbrains.anko.toast
import java.util.UUID
import java.io.InputStream
import java.io.OutputStream
import java.io.IOException


/**
 * Activity that shows the data the Bluetooth device is sending to the phone.
 */
class DisplayMetrics: AppCompatActivity() {

    private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var device: BluetoothDevice? = null
    private var ledStatus: Boolean = false
    private var disp: String = ""
    private var mmOutputStream: OutputStream? = null
    private var mmSocket: BluetoothSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.display_metrics)
        this.ledTextView.text = getString(R.string.led_prefix) + this.ledStatus.toString()

        /**
         * Get the device selected from the main activity and set the sucket for that device
         */
        this.device = bluetoothAdapter.getRemoteDevice(intent.getStringExtra("Device_address"))
        this.mmSocket = this.device!!.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))

        /**
         * Connect to that device if available and set the output stream
         * Otherwise finish this activity
         */
        try{
            this.mmSocket!!.connect()
            this.mmOutputStream = this.mmSocket!!.outputStream
        }
        catch (ex:IOException){
            this.toast("Could not connect")

        }

        /**
         * if the led button is clicked, the led status changes and displayed.
         * if the status is true it sends '1', and if false it sends '0' to the bluetooth device.
         * if the device is not connected, it will print out a message.
         */
        this.toggleLEDButton.setOnClickListener {
            if(this.mmSocket!!.isConnected) {
                this.ledStatus = !this.ledStatus
                disp = getString(R.string.led_prefix) + this.ledStatus.toString()
                this.ledTextView.text = disp

                if (ledStatus == true) {
                    this.mmOutputStream!!.write(0x31)
                } else {
                    this.mmOutputStream!!.write(0x30)
                }
            }
            else{
                this.toast("Device not connected")
            }
        }
    }
}
/* Arduino code:
*****************************************************************
#include "BluetoothSerial.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

BluetoothSerial SerialBT;

void setup() {
Serial.begin(115200);
SerialBT.begin("ESP32test"); //Bluetooth device name
Serial.println("The device started, now you can pair it with bluetooth!");
pinMode(2, OUTPUT);
}

void loop() {
if (Serial.available()) {
SerialBT.write(Serial.read());
}
if (SerialBT.available()) {
byte recieved = SerialBT.read();
Serial.write(recieved);
if(recieved == '0'){
digitalWrite(2, LOW);
}
else if(recieved == '1'){
digitalWrite(2, HIGH);
}

}
delay(20);
}
*****************************************************************
 */