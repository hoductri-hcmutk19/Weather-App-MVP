package com.example.weather.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CurrentWeather(
    var dateTime: Int = 0,
    var temperature: Double = 0.0,
    var city: String = "",
    var country: String = "",
    var weatherConditionIconUrl: String = "",
    var weatherConditionIconDescription: String = "",
    var weatherMainCondition: String = "",
    var humidity: Int = 0,
    var windSpeed: Double = 0.0
) : Parcelable

object WeatherEntry {
    const val CURRENT_WEATHER = "weather?"
    const val DATE_TIME = "dt"
    const val TEMPERATURE = "temp"
    const val CITY = "name"
    const val COUNTRY = "country"
    const val WEATHER_CONDITION_ICON_URL = "icon"
    const val WEATHER_CONDITION_ICON_DESCRIPTION = "description"
    const val WEATHER_MAIN_CONDITION = "main"
    const val HUMIDITY = "humidity"
    const val WIND_SPEED = "speed"
    const val MAIN = "main"
    const val WEATHER = "weather"
    const val SYS = "sys"
    const val WIND = "wind"
}
