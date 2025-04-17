package com.example.weather.data.repository.source.local.database

import com.example.weather.data.model.Weather

interface IDBHelper {
    fun insertWeather(current: Weather, hourly: Weather, daily: Weather)
    fun getAllData(): List<Weather>
    fun getWeather(idWeather: String): Weather?
    fun getAllOverall(): List<Weather>
    fun deleteWeather(idWeather: String)
}
