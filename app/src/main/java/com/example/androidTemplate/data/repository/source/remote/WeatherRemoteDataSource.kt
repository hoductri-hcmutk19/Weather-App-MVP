package com.example.androidTemplate.data.repository.source.remote

import com.example.androidTemplate.data.model.CurrentWeather
import com.example.androidTemplate.data.model.WeatherEntry
import com.example.androidTemplate.data.repository.source.WeatherDataSource
import com.example.androidTemplate.data.repository.source.remote.fetchjson.GetJsonFromUrl
import com.example.androidTemplate.screen.RequestCompleteListener
import com.example.androidTemplate.utils.Constant

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

