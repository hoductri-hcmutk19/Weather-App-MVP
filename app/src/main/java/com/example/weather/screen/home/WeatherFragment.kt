package com.example.weather.screen.home

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.weather.R
import com.example.weather.data.model.Weather
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.data.repository.source.local.WeatherLocalDataSource
import com.example.weather.data.repository.source.remote.WeatherRemoteDataSource
import com.example.weather.databinding.FragmentWeatherBinding
import com.example.weather.utils.Constant
import com.example.weather.utils.PermissionUtils
import com.example.weather.utils.base.BaseFragment
import com.example.weather.utils.ext.getIcon
import com.example.weather.utils.ext.kelvinToCelsius
import com.example.weather.utils.ext.mpsToKmph
import com.example.weather.utils.ext.unixTimestampToDateTimeString
import com.example.weather.utils.ext.unixTimestampToTimeString

@Suppress("TooManyFunctions")
class WeatherFragment private constructor() : BaseFragment<FragmentWeatherBinding>(), WeatherContract.View {

    private var mRepository: WeatherRepository? = null
    private var mPresenter: WeatherPresenter? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0
    private var mWeatherTemp: Weather? = null
    private var mIsNetworkEnable: Boolean = false

    override fun inflateViewBinding(inflater: LayoutInflater): FragmentWeatherBinding {
        return FragmentWeatherBinding.inflate(inflater)
    }

    override fun initView() {
        viewBinding.layoutHeader.refreshIcon.setOnClickListener {
            onRefresh()
        }
        viewBinding.layoutWeatherBasic.cardView.setOnClickListener { }
        viewBinding.btnNavMap.setOnClickListener { }
    }

    override fun initData() {
        mRepository = context?.let {
            WeatherRepository.getInstance(
                WeatherLocalDataSource.getInstance(it),
                WeatherRemoteDataSource.getInstance()
            )
        }
        mPresenter = mRepository?.let { WeatherPresenter(it) }
        mPresenter?.setView(this)
        arguments?.let {
            mLatitude = it.getDouble(Constant.LATITUDE_KEY)
            mLongitude = it.getDouble(Constant.LONGITUDE_KEY)
            mPresenter?.getWeather(mLatitude, mLongitude, mIsNetworkEnable)
        }
    }

    override fun checkNetwork(activity: Activity?) {
        if (activity?.let { PermissionUtils.isNetWorkEnabled(it) } == true) {
            mIsNetworkEnable = true
        } else {
            mIsNetworkEnable = false
            onInternetConnectionFailed()
        }
    }

    override fun onProgressLoading(isLoading: Boolean) {
        if (isLoading) {
            viewBinding.progressBar.visibility = View.VISIBLE
            viewBinding.outputGroup.visibility = View.GONE
        } else {
            viewBinding.progressBar.visibility = View.GONE
            viewBinding.outputGroup.visibility = View.VISIBLE
        }
    }

    override fun onGetCurrentWeatherSuccess(weather: Weather) {
        mWeatherTemp = weather
        bindDataToView(weather)
    }

    private fun bindDataToView(weather: Weather) {
        weather.weatherCurrent?.let {
            val time = it.dateTime?.unixTimestampToTimeString()?.toInt()
            if (time != null) {
                getIcon(it.weatherMainCondition!!, time)?.let { image ->
                    viewBinding.icWeather.setImageResource(image)
                }
            }

            viewBinding.layoutWeatherBasic.tvDateTime.text =
                "Today, " + it.dateTime?.unixTimestampToDateTimeString()
            viewBinding.layoutWeatherBasic.tvTemperature.text = it.temperature?.kelvinToCelsius().toString()
            viewBinding.layoutWeatherBasic.tvDescription.text = it.weatherDescription
            viewBinding.layoutWeatherBasic.layoutBasicDetail.tvWindValue.text =
                it.windSpeed?.mpsToKmph().toString() + " km/h"
            viewBinding.layoutWeatherBasic.layoutBasicDetail.tvHumidityValue.text = it.humidity.toString() + " %"
        }
    }

    override fun onInternetConnectionFailed() {
        Toast.makeText(
            context,
            getString(R.string.message_network_not_responding),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onError(exception: Exception) {
        Toast.makeText(context, exception.message.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onDBEmpty() {
        viewBinding.tvError.visibility = View.VISIBLE
        viewBinding.outputGroup.visibility = View.GONE
    }

    private fun onRefresh() {
        checkNetwork(activity)
        mPresenter?.getWeather(mLatitude, mLongitude, mIsNetworkEnable)
    }

    companion object {
        fun newInstance(latitude: Double, longitude: Double) =
            WeatherFragment().apply {
                arguments = bundleOf(
                    Constant.LATITUDE_KEY to latitude,
                    Constant.LONGITUDE_KEY to longitude
                )
            }
    }
}
