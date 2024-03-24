package com.example.weather.data.repository

import com.example.weather.data.model.Weather
import com.example.weather.data.repository.source.WeatherDataSource
import com.example.weather.data.repository.source.local.WeatherLocalDataSource
import com.example.weather.data.repository.source.remote.WeatherRemoteDataSource
import com.example.weather.screen.RequestCompleteListener

class WeatherRepository private constructor(
    private val localDataSource: WeatherLocalDataSource,
    private val remoteDataSource: WeatherRemoteDataSource
) : WeatherDataSource.Local, WeatherDataSource.Remote {

    override fun getWeather(
        latitude: Double,
        longitude: Double,
        listener: RequestCompleteListener<Weather>
    ) {
        remoteDataSource.getWeather(latitude, longitude, listener)
    }

    override fun insertWeather(weather: Weather) {
        localDataSource.insertWeather(weather)
    }

    override fun getAllLocalWeathers(): List<Weather> {
        return localDataSource.getAllLocalWeathers()
    }

    override fun getLocalWeather(id: String): Weather? {
        return localDataSource.getLocalWeather(id)
    }

    override fun getAllLocalOveralls(): List<Weather> {
        return localDataSource.getAllLocalOveralls()
    }

    override fun deleteWeather(id: String) {
        localDataSource.deleteWeather(id)
    }

    companion object {
        private var instance: WeatherRepository? = null

        fun getInstance(local: WeatherLocalDataSource,
                        remote: WeatherRemoteDataSource) = synchronized(this) {
            instance ?: WeatherRepository(local, remote).also { instance = it }
        }
    }
}
