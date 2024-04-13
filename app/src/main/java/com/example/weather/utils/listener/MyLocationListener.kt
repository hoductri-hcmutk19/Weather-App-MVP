package com.example.weather.utils.listener

import android.location.Location
import android.location.LocationListener
import android.os.Bundle

class MyLocationListener(
    private val onFetchLocation: OnFetchListener
) : LocationListener {
    override fun onLocationChanged(location: Location) {
        onFetchLocation.onDataLocation(location)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}
}
