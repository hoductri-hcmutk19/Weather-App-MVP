package com.example.weather.data.repository.source

import com.example.weather.data.model.Weather
import com.example.weather.screen.RequestCompleteListener

interface WeatherDataSource {
    /**
     * Local
     */
    interface Local {
        fun insertWeather(weather: Weather)
        fun getAllLocalWeathers(): List<Weather>
        fun getLocalWeather(id: String): Weather?
        fun getAllLocalOveralls(): List<Weather>
        fun deleteWeather(id: String)
    }

    /**
     * Remote
     */
    interface Remote {
        fun getWeather(
            latitude: Double,
            longitude: Double,
            listener: RequestCompleteListener<Weather>
        )
    }
}
