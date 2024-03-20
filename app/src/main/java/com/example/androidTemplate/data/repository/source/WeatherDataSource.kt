package com.example.androidTemplate.data.repository.source

import com.example.androidTemplate.data.model.City
import com.example.androidTemplate.data.model.CurrentWeather
import com.example.androidTemplate.screen.RequestCompleteListener

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

