package com.example.weather.data.repository.source

import com.example.weather.data.model.Weather
import com.example.weather.screen.RequestCompleteListener

interface WeatherDataSource {
    /**
     * Local
     */
    interface Local {
        fun insertWeather(current: Weather, hourly: Weather, daily: Weather)
        fun getAllLocalWeathers(): List<Weather>
        fun getLocalWeather(id: String): Weather?
        fun getAllLocalOveralls(): List<Weather>
        fun deleteWeather(id: String)
    }

    /**
     * Remote
     */
    interface Remote {
        fun fetchWeatherForecastCurrent(
            latitude: Double,
            longitude: Double,
            listener: RequestCompleteListener<Weather>
        )

        fun fetchWeatherForecastHourly(
            latitude: Double,
            longitude: Double,
            listener: RequestCompleteListener<Weather>
        )

        fun fetchWeatherForecastDaily(
            latitude: Double,
            longitude: Double,
            listener: RequestCompleteListener<Weather>
        )
    }
}
