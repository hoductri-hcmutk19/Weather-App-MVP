package com.example.weather.screen.favorite

import com.example.weather.data.repository.WeatherRepository
import com.example.weather.utils.Constant

class FavoritePresenter(
    private val repository: WeatherRepository
) : FavoriteContract.Presenter {

    private var mView: FavoriteContract.View? = null

    override fun setView(view: FavoriteContract.View?) {
        this.mView = view
    }

    override fun getAllFavorite() {
        val weatherList = repository.getAllLocalWeathers().toMutableList()
        weatherList.removeIf { it.isFavorite == Constant.FALSE }
        mView?.onGetWeatherListSuccess(weatherList)
    }

    override fun removeFavoriteWeather(id: String) {
        repository.deleteWeather(id)
    }

    override fun onStart() {
        // TODO implement later
    }

    override fun onStop() {
        // TODO implement later
    }
}
