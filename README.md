## PepperPairingSDK

This SDK provides the ability to pair with devices compatible with the Pepper environment.

### Getting Started

##### Permissions

```
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE"/>
```

Add the above permissions to your `AndroidManifest.xml`

For the `SoftAPManager` to be successful, it requires the following conditions:

- If your app is targeting Android 10 (API level 29) SDK or higher, your app has the `ACCESS_FINE_LOCATION` permission.
- If your app is targeting SDK lower than Android 10 (API level 29), your app has the `ACCESS_COARSE_LOCATION` **or** `ACCESS_FINE_LOCATION` permission.
- Your app has the `CHANGE_WIFI_STATE` permission.


#### Initialize SoftAPManager

To initialize the `SoftAPManager`, you will need:

1. Host: Host for socket address of the smart device
2. Port: Port for socket address of the smart device
3. Shift Cipher: An integer used to shift the position of certain characters before sending to the smart device
4. Encoding Type: *(Optional)* Encoding type for socket communication with the smart device (Default: "UTF-8")


```
import com.pepper.pepperpairingsdk.SoftAPManager

...

SoftAPManager.initialize(<host>, <port>, <shift cipher>, <encoding type>)
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
        // Navigate to Activity with a list to display Wi-Fi SSIDs
      }
    }
  }
}
```


#### Auto-join Hotspot

*To successfully auto-join to the smart device, you must have the user grant location permissions.
See [Permissions](#Permissions) section above.*

Also, to successfully auto-join the smart device, you must provide an Android context.

```
import com.pepper.pepperpairingsdk.AutoJoinHotspot
...

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    ...

    // Set context for AutoJoinHotspot
    AutoJoinHotspot.context = this
  }

...
```

To auto-join the device's hotspot,

```
// Make sure we have location permissions

...

// Once we have the correct location permissions, attempt to join hotspot...

val prefixes = listOf(<prefix>, ...)
AutoJoinHotspot.join(prefixes) { ex: Exception?, prefix: String? ->
  if (ex != null) {
    println("Failed to auto join device hotspot: $ex")
  } else {
    println("Auto connected to device hotspot with prefix = $prefix")
  }
}
```


Once you're done using the smart device's hotspot, you will need to disconnect the network so the user doesn't continue to be bound to the hotspot network. This will cause issues for you and your user.

```
AutoJoinHotspot.disconnectNetwork()

...

AutoJoinHotspot.disconnectNetwork(<reconnect to default network>) { disconnected: Boolean ->
  println("Disconnected from device hotspot?: $disconnected")
}
```


#### Perform SofAP

To execute the SoftAP process, you will need four values.

`SoftAPStartParams`

1. SSID: SSID the smart device should connect to for cloud communication
2. Password: The password of the Wi-Fi network the smart device should connect to
3. Accound ID: Pepper account ID the smart device will be assigned to
4. Server URL: URL for the smart device to communicate with the cloud (will be different by brand/device provider)

```
import com.pepper.pepperpairingsdk.SoftAPStartParams
import com.pepper.pepperpairingsdk.SoftAPManager

...

val params = SoftAPStartParams(
  <ssid>,
  <password>,
  <account_id>,
  <server_url>
)
SoftAPManager.performSoftAP(params) { ex: Exception?, response: SoftAPExecutionResponse? ->
  if (ex != null) {
    println("Failed to perform SoftAP: $ex")
  } else {
    println("SoftAP successful for device = ${response!!.deviceId} and provider = ${response.provider}")
  }
}

```
