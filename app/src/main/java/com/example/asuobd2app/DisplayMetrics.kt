package com.example.asuobd2app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.display_metrics.*

/**
 * Activity that shows the data the Bluetooth device is sending to the phone.
 */
class DisplayMetrics: AppCompatActivity() {

    private var ledStatus: Boolean = false
    private var disp: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.display_metrics)
        this.ledTextView.text = getString(R.string.led_prefix) + this.ledStatus.toString()

        this.toggleLEDButton.setOnClickListener {
            this.ledStatus = !this.ledStatus
            disp = getString(R.string.led_prefix) + this.ledStatus.toString()
            this.ledTextView.text = disp
        }
    }

}