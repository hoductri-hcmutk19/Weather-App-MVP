package com.example.weather.data.repository.source.remote

import com.example.weather.data.model.CurrentWeather
import com.example.weather.data.model.WeatherEntry
import com.example.weather.data.repository.source.WeatherDataSource
import com.example.weather.data.repository.source.remote.fetchjson.GetJsonFromUrl
import com.example.weather.screen.RequestCompleteListener
import com.example.weather.utils.Constant

class WeatherRemoteDataSource : WeatherDataSource.Remote {
    override fun getWeather(cityId: Int, listener: RequestCompleteListener<CurrentWeather>) {
        GetJsonFromUrl(
            cityId = cityId,
            urlString = Constant.BASE_URL,
            keyEntity = WeatherEntry.CURRENT_WEATHER,
            listener = listener
        )
    }

    companion object {
        private var instance: WeatherRemoteDataSource? = null

        fun getInstance() = synchronized(this) {
            instance ?: WeatherRemoteDataSource().also { instance = it }
        }
    }
}
