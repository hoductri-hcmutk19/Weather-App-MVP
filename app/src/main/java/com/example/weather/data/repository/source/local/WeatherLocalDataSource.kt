package com.example.weather.data.repository.source.local

import android.content.Context
import com.example.weather.data.repository.source.WeatherDataSource

class WeatherLocalDataSource private constructor(
    private val context: Context?
) : WeatherDataSource.Local {

    companion object {
        private var instance: WeatherLocalDataSource? = null

        fun getInstance(context: Context) = synchronized(this) {
            instance ?: WeatherLocalDataSource(context).also { instance = it }
        }
    }
}
