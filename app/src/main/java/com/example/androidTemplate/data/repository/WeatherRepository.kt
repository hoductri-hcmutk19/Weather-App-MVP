package com.example.androidTemplate.data.repository

import com.example.androidTemplate.data.model.City
import com.example.androidTemplate.data.model.CurrentWeather
import com.example.androidTemplate.data.repository.source.WeatherDataSource
import com.example.androidTemplate.screen.RequestCompleteListener

class WeatherRepository private constructor(
    private val remote: WeatherDataSource.Remote,
    private val local: WeatherDataSource.Local
) : WeatherDataSource.Local, WeatherDataSource.Remote {

    override fun getCityLocal(listener: RequestCompleteListener<MutableList<City>>) {
        local.getCityLocal(listener)
    }

    override fun getWeather(cityId: Int, listener: RequestCompleteListener<CurrentWeather>) {
        remote.getWeather(cityId, listener)
    }

    companion object {
        private var instance: WeatherRepository? = null

        fun getInstance(remote: WeatherDataSource.Remote, local: WeatherDataSource.Local) = synchronized(this) {
            instance ?: WeatherRepository(remote, local).also { instance = it }
        }
    }
}

