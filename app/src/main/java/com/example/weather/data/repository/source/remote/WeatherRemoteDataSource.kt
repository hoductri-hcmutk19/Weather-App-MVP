package com.example.weather.data.repository.source.remote

import com.example.weather.data.model.Weather
import com.example.weather.data.repository.source.WeatherDataSource
import com.example.weather.data.repository.source.remote.fetchjson.GetJsonFromUrl
import com.example.weather.screen.RequestCompleteListener
import com.example.weather.utils.Constant

class WeatherRemoteDataSource : WeatherDataSource.Remote {
    override fun getWeather(
        latitude: Double,
        longitude: Double,
        listener: RequestCompleteListener<Weather>
    ) {
        val urlCurrent = (Constant.BASE_URL
                + Constant.CURRENT
                + "lat=$latitude"
                + "&lon=$longitude"
                + Constant.BASE_API_KEY)
        val urlHourly = (Constant.BASE_URL
                + Constant.HOURLY
                + "lat=$latitude"
                + "&lon=$longitude"
                + Constant.HOURLY_NUM_TIME
                + Constant.BASE_API_KEY)
        val urlDaily = (Constant.BASE_URL
                + Constant.DAILY
                + "lat=$latitude"
                + "&lon=$longitude"
                + Constant.DAILY_NUM_DAY
                + Constant.BASE_API_KEY)
        val url: MutableList<String> = mutableListOf(urlCurrent, urlHourly, urlDaily)
        GetJsonFromUrl(url, listener)
    }

    companion object {
        private var instance: WeatherRemoteDataSource? = null

        fun getInstance() = synchronized(this) {
            instance ?: WeatherRemoteDataSource().also { instance = it }
        }
    }
}
