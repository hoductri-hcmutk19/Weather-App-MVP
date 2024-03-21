package com.example.weather.data.repository.source.local

import com.example.weather.data.model.City
import com.example.weather.data.repository.source.WeatherDataSource
import com.example.weather.screen.RequestCompleteListener
import java.io.IOException

class WeatherLocalDataSource : WeatherDataSource.Local {
    override fun getCityLocal(listener: RequestCompleteListener<MutableList<City>>) {
        try {
            val cityList: MutableList<City> = mutableListOf()
            // let presenter know the city list
            listener.onRequestSuccess(cityList)
        } catch (e: IOException) {
            // let presenter know about failure
            listener.onRequestFailed(e.localizedMessage!!)
        }
    }

    companion object {
        private var instance: WeatherLocalDataSource? = null

        fun getInstance() = synchronized(this) {
            instance ?: WeatherLocalDataSource().also { instance = it }
        }
    }
}
