package com.example.weather.data.model

import android.os.Parcelable
import com.example.weather.data.model.entity.WeatherBasic
import kotlinx.parcelize.Parcelize

@Parcelize
data class Weather(
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
    val timeZone: Int? = 0,
    var city: String? = "",
    var country: String? = "",
    val weatherCurrent: WeatherBasic?,
    val weatherHourlyList: List<WeatherBasic>?,
    val weatherDailyList: List<WeatherBasic>?
) : Parcelable

object WeatherEntry {
    const val CURRENTLY_OBJECT = "currently"
    const val HOURLY_OBJECT = "hourly"
    const val DAILY_OBJECT = "daily"
    const val TIME_ZONE = "timezone"
    const val CITY = "name"
    const val COUNTRY = "country"
    const val SYS = "sys"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"
    const val LAT = "lat"
    const val LON = "lon"
    const val COORDINATE = "coord"
}
