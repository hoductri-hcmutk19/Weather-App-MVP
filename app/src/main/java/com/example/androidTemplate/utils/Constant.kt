package com.example.androidTemplate.utils

import com.example.androidTemplate.BuildConfig

object Constant {
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    var BASE_API_KEY = "&appid=" + BuildConfig.APP_ID
    const val KELVIN_TO_CELSIUS_NUMBER = 273.15
    const val MPS_TO_KMPH_NUMBER = 3.6
}





