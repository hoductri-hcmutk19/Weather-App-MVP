package com.example.androidTemplate.screen

import com.example.androidTemplate.data.model.City
import com.example.androidTemplate.data.model.CurrentWeatherData

interface MainActivityView {
    fun handleProgressBarVisibility(visibility: Int)
    fun onCityListFetchSuccess(cityList: MutableList<City>)
    fun onCityListFetchFailure(errorMessage: String)
    fun onWeatherInfoFetchSuccess(currentWeather: CurrentWeatherData)
    fun onWeatherInfoFetchFailure(errorMessage: String)
}





