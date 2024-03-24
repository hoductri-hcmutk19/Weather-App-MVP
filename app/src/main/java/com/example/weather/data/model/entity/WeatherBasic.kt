package com.example.weather.data.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherBasic(
    var dateTime: Int? = 0,
    var temperature: Double? = 0.0,
    var weatherMainCondition: String? = "",
    var weatherDescription: String? = "",
    var humidity: Int? = 0,
    var windSpeed: Double? = 0.0,
) : Parcelable

object WeatherBasicEntry {
    const val DATE_TIME = "dt"
    const val TEMPERATURE = "temp"
    const val MAIN = "main"
    const val TEMP_DAY = "day"
    const val WEATHER_DESCRIPTION = "description"
    const val HUMIDITY = "humidity"
    const val WIND_SPEED = "speed"
    const val WIND = "wind"
    const val WEATHER = "weather"
    const val LIST = "list"
}
