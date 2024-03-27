package com.example.weather.utils.listener

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log

class MyLocationListener(
    private val onFetchLocation: OnFetchLocation
): LocationListener {
    override fun onLocationChanged(location: Location) {
        Log.v("myTag", "onLocationChanged")
        onFetchLocation.onDataLocation(location)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}
}
