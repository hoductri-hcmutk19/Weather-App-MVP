package com.example.weather.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.weather.R
import com.example.weather.utils.listener.MyLocationListener
import com.example.weather.utils.listener.OnFetchListener

object PermissionUtils {
    private const val PERMISSION_LOCATION_ID = 44
    private const val MIN_TIME_TRIGGER = 0L // 10 min
    private const val MIN_DISTANCE_TRIGGER = 0F // 5 km

    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_LOCATION_ID
        )
    }

    fun isLocationEnabled(activity: Activity): Boolean {
        val locationManager =
            activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
    }

    fun getLastLocation(activity: Activity, onFetchLocation: OnFetchListener, boolean: Boolean) {
        if (boolean) {
            val locationManager =
                activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                val listener = MyLocationListener(onFetchLocation)
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_TRIGGER,
                    MIN_DISTANCE_TRIGGER,
                    listener
                )
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_TRIGGER,
                    MIN_DISTANCE_TRIGGER,
                    listener
                )
            } catch (e: SecurityException) {
                println(e)
            }
        } else {
            turnOnLocation(activity)
        }
    }

    fun onRequestPermissionResult(
        requestCode: Int,
        grantResults: IntArray,
        activity: Activity
    ) {
        if (requestCode == PERMISSION_LOCATION_ID) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(activity, R.string.thank_you, Toast.LENGTH_SHORT).show()
            } else {
                requestPermissions(activity)
            }
        }
    }

    fun checkPermissions(activity: Activity): Boolean {
        return (
            ActivityCompat.checkSelfPermission(
                activity.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                activity.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
                == PackageManager.PERMISSION_GRANTED
            )
    }

    private fun turnOnLocation(activity: Activity) {
        Toast.makeText(
            activity,
            R.string.turn_on_location,
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity.startActivity(intent)
    }

    fun isNetWorkEnabled(activity: Activity): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val actNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            // for other device how are able to connect with Ethernet
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            // for check internet over Bluetooth
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }
}
