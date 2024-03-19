package com.example.androidTemplate.data.repository

import com.example.androidTemplate.data.model.remoteData.City
import com.example.androidTemplate.data.model.CurrentWeatherResponse
import com.example.androidTemplate.screen.RequestCompleteListener


interface WeatherRepository {
    fun getCityList(callback: RequestCompleteListener<MutableList<City>>)
    fun getWeatherInformation(cityId: Int, callback: RequestCompleteListener<CurrentWeatherResponse>)
}

