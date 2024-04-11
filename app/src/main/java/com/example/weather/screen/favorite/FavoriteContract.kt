package com.example.weather.screen.favorite

import com.example.weather.data.model.Weather
import com.example.weather.utils.base.BasePresenter

class FavoriteContract {

    interface View {
        fun onGetWeatherListSuccess(weatherList: List<Weather>)
    }

    interface Presenter : BasePresenter<View> {
        fun getAllFavorite()
        fun removeFavoriteWeather(id: String)
    }
}
