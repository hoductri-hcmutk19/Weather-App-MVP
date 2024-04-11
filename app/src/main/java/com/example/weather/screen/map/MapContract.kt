package com.example.weather.screen.map

import com.example.weather.data.model.Weather
import com.example.weather.utils.base.BasePresenter
import java.lang.Exception

class MapContract {

    interface View {
        fun onProgressLoading(isLoading: Boolean)
        fun onGetCurrentWeatherSuccess(weather: Weather)
        fun onGetWeatherLocalSuccess(isExist: Boolean)
        fun onInternetConnectionFailed()
        fun onError(exception: Exception)
    }

    interface Presenter : BasePresenter<View> {
        fun getWeatherRemote(latitude: Double, longitude: Double)
        fun checkWeatherLocal(id: String)
        fun favoriteWeather(weather: Weather)
        fun removeFavoriteWeather(id: String)
    }
}
