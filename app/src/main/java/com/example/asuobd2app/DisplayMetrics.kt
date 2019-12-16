package com.example.asuobd2app

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity that shows the data the Bluetooth device is sending to the phone.
 */
class DisplayMetrics: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.display_metrics)
    }

}