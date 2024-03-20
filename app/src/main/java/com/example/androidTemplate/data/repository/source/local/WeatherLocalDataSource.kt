package com.example.androidTemplate.data.repository.source.local

import com.example.androidTemplate.data.model.City
import com.example.androidTemplate.data.repository.source.WeatherDataSource
import com.example.androidTemplate.screen.RequestCompleteListener
import java.io.IOException

class WeatherLocalDataSource : WeatherDataSource.Local {

    override fun getCityLocal(listener: RequestCompleteListener<MutableList<City>>) {
        try {
            val cityList: MutableList<City> = mutableListOf()

            cityList.add(City(HCM,"Hồ Chí Minh","VN"))
            cityList.add(City(TDM,"Thủ Dầu Một","VN"))
            cityList.add(City(HN,"Hà Nội","VN"))
            cityList.add(City(MT,"Manchester","GB"))
            cityList.add(City(MC,"Moscow","RU"))

            listener.onRequestSuccess(cityList)     //let presenter know the city list

        } catch (e: IOException) {
            listener.onRequestFailed(e.localizedMessage!!)  //let presenter know about failure
        }

    }

    companion object {
        private const val HCM = 1566083
        private const val TDM = 1565022
        private const val HN = 1581130
        private const val MT = 2643123
        private const val MC = 524901

        private var instance: WeatherLocalDataSource? = null

        fun getInstance() = synchronized(this) {
            instance ?: WeatherLocalDataSource().also { instance = it }
        }
    }
}

