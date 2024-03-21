package com.example.weather.data.repository.source

import com.example.weather.data.model.City
import com.example.weather.data.model.CurrentWeather
import com.example.weather.screen.RequestCompleteListener

interface WeatherDataSource {
    /**
     * Local
     */
    interface Local {
        fun getCityLocal(listener: RequestCompleteListener<MutableList<City>>)
    }

    /**
     * Remote
     */
    interface Remote {
        fun getWeather(cityId: Int, listener: RequestCompleteListener<CurrentWeather>)
    }
}
