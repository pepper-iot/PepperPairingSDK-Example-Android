package com.pepper.pepperpairingsdk

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


const val WIFI_SSIDs = "com.pepper.pepperpairingsdk.WIFI_SSIDs"
const val LOCATION_REQUEST_CODE = 2121

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        hostTextField.setText("192.168.4.1")
        portTextField.setText("8888")
        shiftCipherTextField.setText("4")

        prefixTextField.setText("PEW")

        // Set context for AutoJoinHotspot
        AutoJoinHotspot.context = this

        SoftAPManager.setLogAdapter(1) { logMsg: String ->
            Log.d("MAIN", logMsg)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    println("Location permissions have been denied")
                } else {
                    println("Location permissions have been granted")
                }
            }
            else -> println("Unknown request code for permission. Request code = $requestCode")
        }
    }

    fun connectToDevice(view: View) {
        SoftAPManager.initialize(hostTextField.text.toString(), portTextField.text.toString().toInt(), shiftCipherTextField.text.toString().toInt())
        SoftAPManager.connectCallback = { ex: Exception? ->
            if (ex != null) {
                // Cannot connect!
                println("Failed to connect due to: $ex")
            } else {
                // Connected!
                SoftAPManager.getWifiList { ex2: Exception?, wifiSSIDs: List<String>? ->
                    if (ex2 != null) {
                        println("Failed to get wifi list: $ex2")
                    } else {
                        println("Found wifi ssids: ${wifiSSIDs!!.size}")
                        navigateToWifiList(wifiSSIDs)
                    }
                }
            }
        }
    }

    fun autoJoinHotspot(view: View) {
        // Make sure we have location permissions
        val locationPerm = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        if (locationPerm != PackageManager.PERMISSION_GRANTED) {
            println("Insufficient location permissions. Requesting...")
            requestLocationPermissions()
            return
        }

        AutoJoinHotspot.join(listOf(prefixTextField.text.toString(), "PEY")) { ex: Exception?, prefix: String? ->
            if (ex != null) {
                println("Failed to join device hotspot")
                println(ex)
                showAlert("Failed to join hotspot: $ex")
            } else {
                println("Auto connected to device hotspot with prefix = $prefix")
                showAlert("Joined successfully with prefix: $prefix. Click 'Connect' to start SoftAP flow.")
            }
        }
    }

    fun disconnectHotspot(view: View) {
        AutoJoinHotspot.disconnectNetwork()
    }

    private fun showAlert(message: String) {
        runOnUiThread {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Okay", DialogInterface.OnClickListener { dialog, which -> })
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }

    private fun navigateToWifiList(wifiSSIDs: List<String>) {
        val intent = Intent(this, WifiListActivity::class.java).apply {
            putStringArrayListExtra(WIFI_SSIDs, wifiSSIDs as ArrayList<String>)
        }
        startActivity(intent)
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
    }
}
