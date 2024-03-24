package com.example.weather.data.repository.source.local

import android.content.Context
import com.example.weather.data.model.Weather
import com.example.weather.data.repository.source.WeatherDataSource
import com.example.weather.data.repository.source.local.database.DBHelper
import com.example.weather.screen.RequestCompleteListener
import java.io.IOException

class WeatherLocalDataSource private constructor(
    private val context: Context?
) : WeatherDataSource.Local {

    private val dbHelper: DBHelper by lazy { DBHelper.getInstance(context) }

    override fun insertWeather(weather: Weather) {
        dbHelper.insertWeather(weather)
    }

    override fun getAllLocalWeathers(): List<Weather> {
        return dbHelper.getAllData()
    }

    override fun getLocalWeather(id: String): Weather? {
        return dbHelper.getWeather(id)
    }

    override fun getAllLocalOveralls(): List<Weather> {
        return dbHelper.getAllOverall()
    }

    override fun deleteWeather(id: String) {
        dbHelper.deleteWeather(id)
    }

    companion object {
        private var instance: WeatherLocalDataSource? = null

        fun getInstance(context: Context) = synchronized(this) {
            instance ?: WeatherLocalDataSource(context).also { instance = it }
        }
    }
}
