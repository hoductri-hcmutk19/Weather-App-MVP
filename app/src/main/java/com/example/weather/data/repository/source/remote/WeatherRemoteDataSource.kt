package com.example.weather.data.repository.source.remote

import com.example.weather.data.model.Weather
import com.example.weather.data.model.WeatherEntry
import com.example.weather.data.repository.source.WeatherDataSource
import com.example.weather.data.repository.source.remote.fetchjson.GetJsonFromUrl
import com.example.weather.screen.RequestCompleteListener
import com.example.weather.utils.Constant

class WeatherRemoteDataSource : WeatherDataSource.Remote {
    override fun fetchWeatherForecastCurrent(
        latitude: Double,
        longitude: Double,
        listener: RequestCompleteListener<Weather>
    ) {
        GetJsonFromUrl(
            urlString = Constant.BASE_URL +
                Constant.CURRENT +
                "lat=$latitude" +
                "&lon=$longitude" +
                Constant.BASE_API_KEY,
            keyEntity = WeatherEntry.CURRENTLY_OBJECT,
            listener = listener
        )
    }

    override fun fetchWeatherForecastHourly(
        latitude: Double,
        longitude: Double,
        listener: RequestCompleteListener<Weather>
    ) {
        GetJsonFromUrl(
            urlString = Constant.BASE_URL +
                Constant.HOURLY +
                "lat=$latitude" +
                "&lon=$longitude" +
                Constant.HOURLY_NUM_TIME +
                Constant.BASE_API_KEY,
            keyEntity = WeatherEntry.HOURLY_OBJECT,
            listener = listener
        )
    }

    override fun fetchWeatherForecastDaily(
        latitude: Double,
        longitude: Double,
        listener: RequestCompleteListener<Weather>
    ) {
        GetJsonFromUrl(
            urlString = Constant.BASE_URL +
                Constant.DAILY +
                "lat=$latitude" +
                "&lon=$longitude" +
                Constant.DAILY_NUM_DAY +
                Constant.BASE_API_KEY,
            keyEntity = WeatherEntry.DAILY_OBJECT,
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
