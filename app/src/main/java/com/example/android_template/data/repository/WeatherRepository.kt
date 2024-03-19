package com.example.android_template.data.repository

import com.example.android_template.data.model.remote_data.City
import com.example.android_template.data.model.CurrentWeatherResponse
import com.example.android_template.screen.RequestCompleteListener


interface WeatherRepository {
    fun getCityList(callback: RequestCompleteListener<MutableList<City>>)
    fun getWeatherInformation(cityId: Int, callback: RequestCompleteListener<CurrentWeatherResponse>)
}