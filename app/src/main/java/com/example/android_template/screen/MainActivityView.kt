package com.example.android_template.screen

import com.example.android_template.data.model.remote_data.City
import com.example.android_template.data.model.CurrentWeatherModel

interface MainActivityView {
    fun handleProgressBarVisibility(visibility: Int)
    fun onCityListFetchSuccess(cityList: MutableList<City>)
    fun onCityListFetchFailure(errorMessage: String)
    fun onWeatherInfoFetchSuccess(currentWeatherModel: CurrentWeatherModel)
    fun onWeatherInfoFetchFailure(errorMessage: String)
}