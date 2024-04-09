package com.example.weather.data.repository.source.local.database

import com.example.weather.data.model.Weather

interface IDBHelper {
    fun insertWeatherSplit(current: Weather, hourly: Weather, daily: Weather, isFavorite: String)
    fun insertWeatherFull(weather: Weather, isFavorite: String)
    fun getAllData(): List<Weather>
    fun getWeather(idWeather: String): Weather?
    fun getAllOverall(): List<Weather>
    fun deleteWeather(idWeather: String)
}
