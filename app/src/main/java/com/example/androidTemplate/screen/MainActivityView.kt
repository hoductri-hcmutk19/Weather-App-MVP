package com.example.androidTemplate.screen

import com.example.androidTemplate.data.model.remoteData.City
import com.example.androidTemplate.data.model.CurrentWeatherModel

interface MainActivityView {
    fun handleProgressBarVisibility(visibility: Int)
    fun onCityListFetchSuccess(cityList: MutableList<City>)
    fun onCityListFetchFailure(errorMessage: String)
    fun onWeatherInfoFetchSuccess(currentWeatherModel: CurrentWeatherModel)
    fun onWeatherInfoFetchFailure(errorMessage: String)
}



