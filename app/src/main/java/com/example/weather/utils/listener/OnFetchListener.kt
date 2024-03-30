package com.example.weather.utils.listener

import android.location.Location

interface OnFetchListener {
    fun onDataLocation(location: Location?)
}
