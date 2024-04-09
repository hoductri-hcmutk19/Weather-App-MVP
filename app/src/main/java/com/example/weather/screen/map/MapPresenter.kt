package com.example.weather.screen.map

import com.example.weather.data.model.Weather
import com.example.weather.data.repository.WeatherRepository

class MapPresenter(
    private val repository: WeatherRepository
) : MapContract.Presenter {
    override fun getWeather(latitude: Double, longitude: Double, isNetworkEnable: Boolean, isCurrent: Boolean) {
        // TODO implement later
    }

    override fun favoriteWeather(weather: Weather) {
        // TODO implement later
    }

    override fun removeFavoriteWeather(id: String) {
        // TODO implement later
    }

    override fun onStart() {
        // TODO implement later
    }

    override fun onStop() {
        // TODO implement later
    }

    override fun setView(view: MapContract.View?) {
        // TODO implement later
    }
}
