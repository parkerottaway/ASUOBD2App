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
import org.json.JSONObject


/**
 * Activity that shows the data the Bluetooth device is sending to the phone.
 */
class DisplayMetrics: AppCompatActivity() {

    private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var device: BluetoothDevice? = null
    private var mmOutputStream: OutputStream? = null
    private var mmInputStream: InputStream? = null
    private var mmSocket: BluetoothSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.display_metrics)

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
            this.mmOutputStream = this.mmSocket!!.outputStream // Send
            this.mmInputStream = this.mmSocket!!.inputStream // Receive
        }
        catch (ex:IOException){
            this.toast("Could not connect")

        }

        // Start reading from the Arduino.
        class ReadThread(private val ins: InputStream) : Thread() {

            /**
             * StringBuilder to create JSON byte by byte.
             */
            private val sb: StringBuilder = StringBuilder()

            /**
             * Object version of the JSON.
             */
            private var rootObj : JSONObject? = null

            /**
             * String to place each char received from ESP32.
             */
            private var jsonString: String = ""

            private var canDeser: Boolean = false

            /**
             * Thread logic for listening to ESP32.
             */
            override fun run() {
                while (true) {
                    sb.clear() // Empty the string builder to build new JSON.
                    canDeser = false
                    while (ins.available() > 0) {
                        canDeser = true
                        sb.append(jsonString).append(ins.read().toChar())
                    }

                    if(sb.length != 0) {
                        jsonString = sb.toString()
                        println("jsonString: " + jsonString)
                    }
                    if(canDeser && jsonString.length > 1 && jsonString[0].equals('{')){ // Check if you can deserialzie.
                        rootObj = JSONObject(jsonString)

                        // Display speed
                        textView4.text = rootObj!!.getInt("speed").toString() + " " + getString(R.string.speed_postfix)
                        textView6.text = rootObj!!.getDouble("engineRPM").toString() + " " + getString(R.string.engine_rpm_postfix)
                    }
                    canDeser = false
                }
            }
        }

        val rt: ReadThread = ReadThread(this.mmInputStream!!)
        rt.start()
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