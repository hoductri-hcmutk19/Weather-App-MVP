package com.example.weather.data.repository

import com.example.weather.data.model.Weather
import com.example.weather.data.repository.source.WeatherDataSource
import com.example.weather.data.repository.source.remote.WeatherRemoteDataSource
import com.example.weather.screen.RequestCompleteListener

class WeatherRepository private constructor(
    private val remoteDataSource: WeatherRemoteDataSource
) : WeatherDataSource.Local, WeatherDataSource.Remote {

    override fun fetchWeatherForecastCurrent(
        latitude: Double,
        longitude: Double,
        listener: RequestCompleteListener<Weather>
    ) {
        remoteDataSource.fetchWeatherForecastCurrent(latitude, longitude, listener)
    }

    override fun fetchWeatherForecastHourly(
        latitude: Double,
        longitude: Double,
        listener: RequestCompleteListener<Weather>
    ) {
        remoteDataSource.fetchWeatherForecastHourly(latitude, longitude, listener)
    }

    override fun fetchWeatherForecastDaily(
        latitude: Double,
        longitude: Double,
        listener: RequestCompleteListener<Weather>
    ) {
        remoteDataSource.fetchWeatherForecastDaily(latitude, longitude, listener)
    }

    companion object {
        private var instance: WeatherRepository? = null

        fun getInstance(remote: WeatherRemoteDataSource) = synchronized(this) {
            instance ?: WeatherRepository(remote).also { instance = it }
        }
    }
}
