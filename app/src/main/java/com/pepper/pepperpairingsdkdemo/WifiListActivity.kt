package com.pepper.pepperpairingsdk

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

const val WIFI_SSID = "com.pepper.pepperpairingsdk.WIFI_SSID"

class WifiListActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_list)

        val wifiList = intent.getStringArrayListExtra(WIFI_SSIDs)

        listView = findViewById(R.id.wifi_list_view)
        val listItems = arrayOfNulls<String>(wifiList!!.size)

        for (i in listItems.indices) {
            listItems[i] = wifiList[i]
        }

        listView.adapter = ArrayAdapter(this, R.layout.wifi_list_item, listItems)
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val itemValue = listView.getItemAtPosition(position) as String

            val wifiInfo = SoftAPManager.getWifiInfo(itemValue)
            println("Wifi info for chosen ssid: $wifiInfo")

            println("Chosen SSID: $itemValue")
            navigateToWifiCredentials(itemValue)
        }
    }

    fun navigateToWifiCredentials(wifiSSID: String) {
        val intent = Intent(this, WifiCredentialsActivity::class.java).apply {
            putExtra(WIFI_SSID, wifiSSID)
        }
        startActivity(intent)
    }
}
