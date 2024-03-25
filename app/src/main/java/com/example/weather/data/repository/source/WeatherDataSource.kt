package com.example.weather.data.repository.source

import com.example.weather.data.model.Weather
import com.example.weather.screen.RequestCompleteListener

interface WeatherDataSource {
    /**
     * Local
     */
    interface Local

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
