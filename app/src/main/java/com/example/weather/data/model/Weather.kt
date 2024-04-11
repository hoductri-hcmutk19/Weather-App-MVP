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
    var isFavorite: String? = "",
    val weatherCurrent: WeatherBasic?,
    var weatherHourlyList: List<WeatherBasic>?,
    var weatherDailyList: List<WeatherBasic>?
) : Parcelable {

    fun getLocation(): String {
        return if (!city.isNullOrEmpty() && !country.isNullOrEmpty()) {
            "$city, $country"
        } else {
            "Unknown"
        }
    }

    fun getId(): String {
        return if (!city.isNullOrEmpty() && !country.isNullOrEmpty()) {
            "$city$country"
        } else {
            "Unknown"
        }
    }
}

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
    const val CITY_LIST = "city"
    const val IS_FAVORITE = "favorite"
}
