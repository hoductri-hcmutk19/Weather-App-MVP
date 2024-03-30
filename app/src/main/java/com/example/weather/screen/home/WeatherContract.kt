package com.example.weather.screen.home

import com.example.weather.data.model.Weather
import com.example.weather.utils.base.BasePresenter
import java.lang.Exception

class WeatherContract {

    interface View {
        fun onProgressLoading(isLoading: Boolean)
        fun onGetCurrentWeatherSuccess(weather: Weather)
        fun onInternetConnectionFailed()
        fun onError(exception: Exception)
        fun onDBEmpty()
    }

    interface Presenter : BasePresenter<View> {
        fun getWeather(latitude: Double, longitude: Double, isNetworkEnable: Boolean)
    }
}
