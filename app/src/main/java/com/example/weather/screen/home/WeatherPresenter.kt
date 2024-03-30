package com.example.weather.screen.home

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
    private var isDataInserted = false

    override fun setView(view: WeatherContract.View?) {
        this.view = view
    }

    override fun onStart() {
        // Register
    }

    override fun getWeather(latitude: Double, longitude: Double, isNetworkEnable: Boolean) {
        isDataInserted = false
        view?.onProgressLoading(true)
        if (isNetworkEnable) {
            mExecutor.execute {
                repository.fetchWeatherForecastCurrent(
                    latitude,
                    longitude,
                    object : RequestCompleteListener<Weather> {
                        override fun onRequestSuccess(data: Weather) {
                            current = data
                            insertWeatherIfDataAvailable(current, hourly, daily)
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
                            insertWeatherIfDataAvailable(current, hourly, daily)
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
                            insertWeatherIfDataAvailable(current, hourly, daily)
                        }

                        override fun onRequestFailed(e: Exception?) {
                            view?.onProgressLoading(false)
                            e?.let { view?.onError(e) }
                        }
                    }
                )
            }
        } else {
            val listWeather = repository.getAllLocalWeathers()
            view?.onProgressLoading(false)
            if (listWeather.isNotEmpty()) {
                handleLocalWeatherList(listWeather)
            } else {
                view?.onDBEmpty()
            }
        }
    }

    fun insertWeatherIfDataAvailable(current: Weather?, hourly: Weather?, daily: Weather?) {
        if (!isDataInserted) {
            if (current != null && hourly != null && daily != null) {
                mHandler.post {
                    view?.onProgressLoading(false)
                    view?.onGetCurrentWeatherSuccess(current)
                    isDataInserted = true
                    repository.insertCurrentWeather(current, hourly, daily)
                }
            }
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
