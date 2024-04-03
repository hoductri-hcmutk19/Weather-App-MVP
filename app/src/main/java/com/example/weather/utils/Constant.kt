package com.example.weather.utils

import com.example.weather.BuildConfig

object Constant {
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    var BASE_API_KEY = "&appid=" + BuildConfig.APP_ID
    const val CURRENT = "weather?"
    const val HOURLY = "forecast?"
    const val DAILY = "forecast/daily?"
    const val DAILY_NUM_DAY = "&cnt=7"
    const val HOURLY_NUM_TIME = "&cnt=8"
    const val KELVIN_TO_CELSIUS_NUMBER = 273.15
    const val MPS_TO_KMPH_NUMBER = 3.6
    const val LATITUDE_KEY = "LATITUDE"
    const val LONGITUDE_KEY = "LONGITUDE"
    const val NIGHT_TIME_START = 18
    const val TRUE = "true"
    const val FALSE = "false"
}
