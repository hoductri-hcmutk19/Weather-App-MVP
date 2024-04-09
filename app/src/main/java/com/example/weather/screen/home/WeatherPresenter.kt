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
    private var mView: WeatherContract.View? = null

    private val mExecutor: Executor = Executors.newCachedThreadPool()
    private val mHandler = Handler(Looper.getMainLooper())
    private var mCurrent: Weather? = null
    private var mHourly: Weather? = null
    private var mDaily: Weather? = null
    private var mIsDataFetching = false

    override fun setView(view: WeatherContract.View?) {
        this.mView = view
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
        if (mIsDataFetching) {
            return
        }
        mIsDataFetching = true

        mView?.onProgressLoading(true)
        mCurrent = null
        mHourly = null
        mDaily = null
        try {
            mExecutor.execute {
                repository.fetchWeatherForecastCurrent(
                    latitude,
                    longitude,
                    object : RequestCompleteListener<Weather> {
                        override fun onRequestSuccess(data: Weather) {
                            mCurrent = data
                            insertWeatherIfDataAvailable(mCurrent, mHourly, mDaily, isCurrent)
                        }

                        override fun onRequestFailed(e: Exception?) {
                            mView?.onProgressLoading(false)
                            e?.let { mView?.onError(e) }
                        }
                    }
                )
                repository.fetchWeatherForecastHourly(
                    latitude,
                    longitude,
                    object : RequestCompleteListener<Weather> {
                        override fun onRequestSuccess(data: Weather) {
                            mHourly = data
                            insertWeatherIfDataAvailable(mCurrent, mHourly, mDaily, isCurrent)
                        }

                        override fun onRequestFailed(e: Exception?) {
                            mView?.onProgressLoading(false)
                            e?.let { mView?.onError(e) }
                        }
                    }
                )
                repository.fetchWeatherForecastDaily(
                    latitude,
                    longitude,
                    object : RequestCompleteListener<Weather> {
                        override fun onRequestSuccess(data: Weather) {
                            mDaily = data
                            insertWeatherIfDataAvailable(mCurrent, mHourly, mDaily, isCurrent)
                        }

                        override fun onRequestFailed(e: Exception?) {
                            mView?.onProgressLoading(false)
                            e?.let { mView?.onError(e) }
                        }
                    }
                )
            }
        } catch (e: NetworkErrorException) {
            println(e)
        } finally {
            mIsDataFetching = false
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
        mView?.onGetSpinnerList(listWeather)
        val current = repository.getLocalWeather(idWeather)
        if (current != null) {
            mView?.onProgressLoading(false)
            mView?.onGetCurrentWeatherSuccess(current)
        }
        mIsDataFetching = false
    }

    private fun getLocalWeather() {
        val listWeather = repository.getAllLocalWeathers()
        mView?.onProgressLoading(false)
        mView?.onGetSpinnerList(listWeather)
        if (listWeather.isNotEmpty()) {
            handleLocalWeatherList(listWeather)
        } else {
            mView?.onDBEmpty()
        }
    }

    private fun handleLocalWeatherList(listWeather: List<Weather>) {
        var hasCurrentWeather = false
        for (weather in listWeather) {
            if (weather.isFavorite == "false") {
                mView?.onGetCurrentWeatherSuccess(weather)
                hasCurrentWeather = true
            }
        }
        if (!hasCurrentWeather) {
            mView?.onGetCurrentWeatherSuccess(listWeather[0])
        }
    }

    override fun onStop() {
        // Unregister
    }
}
