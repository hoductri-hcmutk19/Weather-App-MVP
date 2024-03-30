package com.example.weather.data.repository.source.local

import android.content.Context
import com.example.weather.data.model.Weather
import com.example.weather.data.repository.source.WeatherDataSource
import com.example.weather.data.repository.source.local.database.DBHelper
import com.example.weather.data.repository.source.local.database.IDBHelper

class WeatherLocalDataSource private constructor(
    private val context: Context?
) : WeatherDataSource.Local {

    private val dbHelper: IDBHelper by lazy { DBHelper.getInstance(context) }

    override fun insertCurrentWeather(current: Weather, hourly: Weather, daily: Weather) {
        dbHelper.insertWeather(current, hourly, daily, "false")
    }

    override fun insertFavoriteWeather(current: Weather, hourly: Weather, daily: Weather) {
        dbHelper.insertWeather(current, hourly, daily, "true")
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
