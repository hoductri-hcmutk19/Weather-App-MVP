package com.example.androidTemplate.screen.presenter

interface WeatherInfoShowPresenter {
    fun fetchCityList()
    fun fetchWeatherInfo(cityId: Int)
    fun detachView()
}

