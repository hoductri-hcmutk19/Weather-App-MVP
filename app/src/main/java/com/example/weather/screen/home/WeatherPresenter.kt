package com.example.weather.screen.home

import android.accounts.NetworkErrorException
import android.os.Handler
import android.os.Looper
import com.example.weather.data.model.Weather
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.screen.RequestCompleteListener
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class WeatherPresenter(
    private val repository: WeatherRepository
) : WeatherContract.Presenter {
    private var view: WeatherContract.View? = null

    private val mExecutor: Executor = Executors.newCachedThreadPool()
    private val mHandler = Handler(Looper.getMainLooper())
    private var current: Weather? = null
    private var hourly: Weather? = null
    private var daily: Weather? = null
    private var isDataFetching = false

    override fun setView(view: WeatherContract.View?) {
        this.view = view
    }

    override fun onStart() {
        // Register
    }

    override fun getWeather(latitude: Double, longitude: Double, isNetworkEnable: Boolean, isCurrent: Boolean) {
        if (isNetworkEnable) {
            getRemoteWeather(latitude, longitude, isCurrent)
        } else {
            getLocalWeather()
        }
    }

    private fun getRemoteWeather(latitude: Double, longitude: Double, isCurrent: Boolean) {
        if (isDataFetching) {
            return
        }
        isDataFetching = true

        view?.onProgressLoading(true)
        current = null
        hourly = null
        daily = null
        try {
            mExecutor.execute {
                repository.fetchWeatherForecastCurrent(
                    latitude,
                    longitude,
                    object : RequestCompleteListener<Weather> {
                        override fun onRequestSuccess(data: Weather) {
                            current = data
                            insertWeatherIfDataAvailable(current, hourly, daily, isCurrent)
                        }

                        override fun onRequestFailed(e: Exception?) {
                            view?.onProgressLoading(false)
                            e?.let { view?.onError(e) }
                        }
                    }
                )
                repository.fetchWeatherForecastHourly(
                    latitude,
                    longitude,
                    object : RequestCompleteListener<Weather> {
                        override fun onRequestSuccess(data: Weather) {
                            hourly = data
                            insertWeatherIfDataAvailable(current, hourly, daily, isCurrent)
                        }

                        override fun onRequestFailed(e: Exception?) {
                            view?.onProgressLoading(false)
                            e?.let { view?.onError(e) }
                        }
                    }
                )
                repository.fetchWeatherForecastDaily(
                    latitude,
                    longitude,
                    object : RequestCompleteListener<Weather> {
                        override fun onRequestSuccess(data: Weather) {
                            daily = data
                            insertWeatherIfDataAvailable(current, hourly, daily, isCurrent)
                        }

                        override fun onRequestFailed(e: Exception?) {
                            view?.onProgressLoading(false)
                            e?.let { view?.onError(e) }
                        }
                    }
                )
            }
        } catch (e: NetworkErrorException) {
            println(e)
        } finally {
            isDataFetching = false
        }
    }

    fun insertWeatherIfDataAvailable(current: Weather?, hourly: Weather?, daily: Weather?, isCurrent: Boolean) {
        if (current != null && hourly != null && daily != null) {
            mHandler.post {
                val idWeather = current.city + current.country
                if (isCurrent) {
                    repository.insertCurrentWeather(current, hourly, daily)
                    onGetDataAndSendToView(idWeather)
                } else {
                    repository.insertFavoriteWeather(current, hourly, daily)
                    onGetDataAndSendToView(idWeather)
                }
            }
        }
    }

    private fun onGetDataAndSendToView(idWeather: String) {
        val listWeather = repository.getAllLocalWeathers()
        view?.onGetSpinnerList(listWeather)
        val current = repository.getLocalWeather(idWeather)
        if (current != null) {
            view?.onProgressLoading(false)
            view?.onGetCurrentWeatherSuccess(current)
        }
        isDataFetching = false
    }

    private fun getLocalWeather() {
        val listWeather = repository.getAllLocalWeathers()
        view?.onProgressLoading(false)
        view?.onGetSpinnerList(listWeather)
        if (listWeather.isNotEmpty()) {
            handleLocalWeatherList(listWeather)
        } else {
            view?.onDBEmpty()
        }
    }

    private fun handleLocalWeatherList(listWeather: List<Weather>) {
        var hasCurrentWeather = false
        for (weather in listWeather) {
            if (weather.isFavorite == "false") {
                view?.onGetCurrentWeatherSuccess(weather)
                hasCurrentWeather = true
            }
        }
        if (!hasCurrentWeather) {
            view?.onGetCurrentWeatherSuccess(listWeather[0])
        }
    }

    override fun onStop() {
        // Unregister
    }
}
