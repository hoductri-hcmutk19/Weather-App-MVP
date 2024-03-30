package com.example.weather.utils.listener

import android.location.Location
import android.location.LocationListener

class MyLocationListener(
    private val onFetchLocation: OnFetchListener
) : LocationListener {
    override fun onLocationChanged(location: Location) {
        onFetchLocation.onDataLocation(location)
    }
}
