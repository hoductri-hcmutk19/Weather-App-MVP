package com.example.androidTemplate.data.repository.source.local

import com.example.androidTemplate.data.model.City
import com.example.androidTemplate.data.repository.source.WeatherDataSource
import com.example.androidTemplate.screen.RequestCompleteListener
import java.io.IOException

class WeatherLocalDataSource() : WeatherDataSource.Local {

    override fun getCityLocal(listener: RequestCompleteListener<MutableList<City>>) {
        try {
            val cityList: MutableList<City> = mutableListOf()

            cityList.add(City(1566083,"Hồ Chí Minh","VN"))
            cityList.add(City(1565022,"Thủ Dầu Một","VN"))
            cityList.add(City(1581130,"Hà Nội","VN"))
            cityList.add(City(2643123,"Manchester","GB"))
            cityList.add(City(524901,"Moscow","RU"))

            listener.onRequestSuccess(cityList)     //let presenter know the city list

        } catch (e: IOException) {
            listener.onRequestFailed(e.localizedMessage!!)  //let presenter know about failure
        }

    }

    companion object {
        private var instance: WeatherLocalDataSource? = null

        fun getInstance() = synchronized(this) {
            instance ?: WeatherLocalDataSource().also { instance = it }
        }
    }
}

