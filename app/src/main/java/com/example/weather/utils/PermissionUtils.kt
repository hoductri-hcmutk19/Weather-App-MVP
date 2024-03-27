package com.example.weather.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.weather.utils.listener.MyLocationListener
import com.example.weather.utils.listener.OnFetchLocation

object PermissionUtils {
    private const val PERMISSION_LOCATION_ID = 44

    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            PERMISSION_LOCATION_ID
        )
    }

    fun isLocationEnabled(activity: Activity): Boolean {
        val locationManager =
            activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER)
    }

    fun getLastLocation(activity: Activity, onFetchLocation: OnFetchLocation, boolean: Boolean) {
        if (boolean) {
            val locationManager =
                activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                val listener = MyLocationListener(onFetchLocation)
                var location =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                location?.let { listener.onLocationChanged(it) }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            turnOnLocation(activity)
        }
    }

    fun onRequestPermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        activity: Activity
    ) {
        if (requestCode == PERMISSION_LOCATION_ID) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) { }
        }
    }

    fun checkPermissions(activity: Activity): Boolean {
        if (ActivityCompat.checkSelfPermission(
                activity.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity.applicationContext,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) { return true }
        return false
    }

    private fun turnOnLocation(activity: Activity) {
        Toast.makeText(activity,
            "Turn on location",
            Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity.startActivity(intent)
    }
    
    fun isNetWorkEnabled(activity: Activity): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netWorkInfo = connectivityManager.activeNetworkInfo
        return netWorkInfo != null && netWorkInfo.isConnectedOrConnecting
    }
}
