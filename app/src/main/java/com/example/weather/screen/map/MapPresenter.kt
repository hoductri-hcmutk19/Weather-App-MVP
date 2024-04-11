package com.example.weather.screen.map

import android.accounts.NetworkErrorException
import android.os.Handler
import android.os.Looper
import com.example.weather.data.model.Weather
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.screen.RequestCompleteListener
import com.example.weather.utils.Constant
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MapPresenter(
    private val repository: WeatherRepository
) : MapContract.Presenter {

    private var mView: MapContract.View? = null
    private val mExecutor: Executor = Executors.newCachedThreadPool()
    private val mHandler = Handler(Looper.getMainLooper())
    private var mCurrent: Weather? = null
    private var mHourly: Weather? = null
    private var mDaily: Weather? = null
    private var mIsDataFetching = false

    override fun setView(view: MapContract.View?) {
        this.mView = view
    }

    override fun onStart() {
        // TODO implement later
    }

    override fun onStop() {
        // TODO implement later
    }

    override fun favoriteWeather(weather: Weather) {
        repository.insertFavoriteWeather(weather)
    }

    override fun removeFavoriteWeather(id: String) {
        repository.deleteWeather(id)
    }

    override fun getWeatherRemote(latitude: Double, longitude: Double) {
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
                            insertWeatherIfDataAvailable(mCurrent, mHourly, mDaily)
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
                            insertWeatherIfDataAvailable(mCurrent, mHourly, mDaily)
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
                            insertWeatherIfDataAvailable(mCurrent, mHourly, mDaily)
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

    override fun checkWeatherLocal(id: String) {
        val weather = repository.getLocalWeather(id)
        if (weather != null) {
            mView?.onGetWeatherLocalSuccess(true)
        } else {
            mView?.onGetWeatherLocalSuccess(false)
        }
    }

    fun insertWeatherIfDataAvailable(current: Weather?, hourly: Weather?, daily: Weather?) {
        if (current != null && hourly != null && daily != null) {
            mHandler.post {
                val idWeather = current.city + current.country
                val weather = repository.getLocalWeather(idWeather)
                if (weather != null && weather.isFavorite == Constant.TRUE) {
                    current.isFavorite = Constant.TRUE
                    repository.insertFavoriteWeather(current, hourly, daily)
                    sendToView(current, hourly, daily)
                } else if (weather != null && weather.isFavorite == Constant.FALSE) {
                    current.isFavorite = Constant.FALSE
                    repository.insertCurrentWeather(current, hourly, daily)
                    sendToView(current, hourly, daily)
                } else {
                    current.isFavorite = Constant.FALSE
                    sendToView(current, hourly, daily)
                }
            }
        }
    }

    private fun sendToView(current: Weather, hourly: Weather, daily: Weather) {
        mView?.onProgressLoading(false)
        current.weatherHourlyList = hourly.weatherHourlyList
        current.weatherDailyList = daily.weatherDailyList
        mView?.onGetCurrentWeatherSuccess(current)
        mIsDataFetching = false
    }
}
