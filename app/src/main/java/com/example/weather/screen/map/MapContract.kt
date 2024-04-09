package com.example.weather.screen.map

import com.example.weather.data.model.Weather
import com.example.weather.utils.base.BasePresenter
import java.lang.Exception

class MapContract {

    interface View {
        fun onProgressLoading(isLoading: Boolean)
        fun onGetCurrentWeatherSuccess(weather: Weather)
        fun onSearchLocation(latitude: Double, longitude: Double)
        fun onInternetConnectionFailed()
        fun onError(exception: Exception)
    }

    interface Presenter : BasePresenter<View> {
        fun getWeather(latitude: Double, longitude: Double, isNetworkEnable: Boolean, isCurrent: Boolean)
        fun favoriteWeather(weather: Weather)
        fun removeFavoriteWeather(id: String)
    }
}
