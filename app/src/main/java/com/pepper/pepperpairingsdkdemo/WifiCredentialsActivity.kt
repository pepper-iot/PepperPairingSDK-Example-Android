package com.pepper.pepperpairingsdk

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_wifi_credentials.*

class WifiCredentialsActivity : AppCompatActivity() {

    var wifiSSID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_credentials)

        wifiSSID = intent.getStringExtra(WIFI_SSID)

        if (wifiSSID != null) {
            ssidTextField.setText(wifiSSID)
            passwordTextField.requestFocus()
        } else {
            ssidTextField.requestFocus()
        }

        accountIdTextField.setText("2283e32b-3092-42e7-bb5d-79037b598e38")
//        serverUrlTextField.setText("wss://dev.move.pepperos.io/ws")
        serverUrlTextField.setText("aoff7zsj1ku72-ats.iot.us-east-2.amazonaws.com")
    }

    fun executeSoftAP(view: View) {
        val softAPParams = SoftAPStartParams(
            ssidTextField.text.toString(),
            passwordTextField.text.toString(),
            accountIdTextField.text.toString(),
            serverUrlTextField.text.toString()
        )
        SoftAPManager.performSoftAP(softAPParams) { ex: Exception?, response: SoftAPExecutionResponse? ->
            if (ex != null) {
                println("Failed to execute SoftAP")
                println(ex)
                showAlert("Failed to execute SoftAP: $ex")
            } else {
                println("SoftAP executed successfully: ${response!!.deviceId} ${response.provider}")
                showAlert("SoftAP completed successfully. Device is pairing...")
            }
        }
    }

    private fun showAlert(message: String) {
        runOnUiThread {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Okay") { dialog, which -> }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }
}
