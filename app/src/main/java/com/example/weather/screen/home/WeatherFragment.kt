package com.example.weather.screen.home

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.weather.R
import com.example.weather.data.model.Weather
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.data.repository.source.local.WeatherLocalDataSource
import com.example.weather.data.repository.source.remote.WeatherRemoteDataSource
import com.example.weather.databinding.FragmentWeatherBinding
import com.example.weather.screen.detail.DetailFragment
import com.example.weather.screen.map.MapFragment
import com.example.weather.utils.Constant
import com.example.weather.utils.PermissionUtils
import com.example.weather.utils.Utils
import com.example.weather.utils.base.BaseFragment
import com.example.weather.utils.ext.getIcon
import com.example.weather.utils.ext.kelvinToCelsius
import com.example.weather.utils.ext.mpsToKmph
import com.example.weather.utils.ext.unixTimestampToDateTimeString
import com.example.weather.utils.ext.unixTimestampToHourString
import com.example.weather.utils.replaceFragmentToActivity

@Suppress("TooManyFunctions")
class WeatherFragment private constructor() :
    BaseFragment<FragmentWeatherBinding>(),
    WeatherContract.View,
    AdapterView.OnItemSelectedListener {

    private var mPresenter: WeatherPresenter? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0
    private var mWeatherCurrent: Weather? = null
    private var mIsNetworkEnable: Boolean = false
    private var mPosition: Int = 0
    private var mListItemSpinner: ArrayList<String> = arrayListOf()
    private var mSpinnerCheck: Int = 0
    private var mIsAppStarted: Boolean = false

    private lateinit var mWeatherList: List<Weather>
    private lateinit var mSpinnerAdapter: ArrayAdapter<String>

    override fun inflateViewBinding(inflater: LayoutInflater): FragmentWeatherBinding {
        return FragmentWeatherBinding.inflate(inflater)
    }

    override fun initView() {
        mSpinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, mListItemSpinner)
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewBinding.layoutHeader.spinner.adapter = mSpinnerAdapter

        viewBinding.layoutHeader.refreshIcon.setOnClickListener {
            onRefresh()
        }
        viewBinding.layoutWeatherBasic.cardView.setOnClickListener {
            activity?.let {
                (it as AppCompatActivity).replaceFragmentToActivity(
                    it.supportFragmentManager,
                    DetailFragment.newInstance(mWeatherList[mPosition]),
                    R.id.container
                )
            }
        }
        viewBinding.btnNavMap.setOnClickListener {
            if (mIsNetworkEnable) {
                activity?.let {
                    (it as AppCompatActivity).replaceFragmentToActivity(
                        it.supportFragmentManager,
                        MapFragment.newInstance(mLatitude, mLongitude),
                        R.id.container
                    )
                }
            }
        }

        val initialSelectedPosition = viewBinding.layoutHeader.spinner.selectedItemPosition
        viewBinding.layoutHeader.spinner.setSelection(initialSelectedPosition, false)
        viewBinding.layoutHeader.spinner.onItemSelectedListener = this
    }

    @Suppress("NestedBlockDepth")
    override fun initData() {
        val repository = context?.let { context ->
            WeatherRepository.getInstance(
                WeatherLocalDataSource.getInstance(context),
                WeatherRemoteDataSource.getInstance()
            )
        }
        mPresenter = repository?.let { WeatherPresenter(it) }
        mPresenter?.setView(this)
        arguments?.let {
            mLatitude = it.getDouble(Constant.LATITUDE_KEY)
            mLongitude = it.getDouble(Constant.LONGITUDE_KEY)
            if (!mIsAppStarted) {
                mIsAppStarted = true
                mPresenter?.getWeather(mLatitude, mLongitude, mPosition, mIsNetworkEnable, true)
            } else {
                if (mPosition != 0) { // Fix spinner auto click first item
                    mSpinnerCheck = 0
                }
                if (mIsNetworkEnable) {
                    val dateTime = mWeatherList[mPosition].weatherCurrent?.dateTime
                    if (dateTime?.let { dateTime -> Utils.checkTimeInterval(dateTime) } == true) {
                        onRefresh()
                    } else {
                        val latitude = mWeatherList[mPosition].latitude
                        val longitude = mWeatherList[mPosition].longitude
                        latitude?.let { lat ->
                            longitude?.let { lon ->
                                mPresenter?.getWeather(lat, lon, mPosition)
                            }
                        }
                    }
                } else {
                    onRefresh()
                }
            }
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

    override fun onGetSpinnerList(weatherList: List<Weather>) {
        mWeatherList = weatherList
        val newListItemSpinner: ArrayList<String> = arrayListOf()
        weatherList.forEach { mWeather ->
            mWeather.city?.let { cityName ->
                newListItemSpinner.add(cityName)
            }
        }
        mListItemSpinner.apply {
            clear()
            addAll(newListItemSpinner)
        }
        mSpinnerAdapter.notifyDataSetChanged()
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
        if (weather.isFavorite == Constant.FALSE) {
            mWeatherCurrent = weather
        }
        bindDataToView(weather)
    }

    @Suppress("NestedBlockDepth")
    private fun bindDataToView(weather: Weather) {
        viewBinding.layoutHeader.locationIcon.visibility =
            if (weather.isFavorite == Constant.FALSE) View.VISIBLE else View.GONE

        weather.weatherCurrent?.let { weatherCurrent ->
            val time = weatherCurrent.dateTime?.unixTimestampToHourString()?.toInt()
            if (time != null) {
                weatherCurrent.weatherMainCondition?.let { mainCondition ->
                    getIcon(mainCondition, time)?.let { image ->
                        viewBinding.icWeather.setImageResource(image)
                    }
                }
            }
            viewBinding.layoutWeatherBasic.tvDateTime.text =
                "Today, " + weatherCurrent.dateTime?.unixTimestampToDateTimeString()
            viewBinding.layoutWeatherBasic.tvTemperature.text = weatherCurrent.temperature?.kelvinToCelsius().toString()
            viewBinding.layoutWeatherBasic.tvDescription.text = weatherCurrent.weatherDescription
            viewBinding.layoutWeatherBasic.layoutBasicDetail.tvWindValue.text =
                weatherCurrent.windSpeed?.mpsToKmph().toString() + " km/h"
            viewBinding.layoutWeatherBasic.layoutBasicDetail.tvHumidityValue.text =
                weatherCurrent.humidity.toString() + " %"
        }
    }

    @Suppress("NestedBlockDepth")
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        if (mSpinnerCheck++ > 0) { // fix spinner auto click at default position (pos = 0) when it initialization
            mPosition = pos
            val itemLatitude = mWeatherList[pos].latitude
            val itemLongitude = mWeatherList[pos].longitude
            val itemID = mWeatherList[pos].city + mWeatherList[pos].country
            val currentID = mWeatherCurrent?.city + mWeatherCurrent?.country
            val isCurrent = itemID == currentID

            if (itemLatitude != null && itemLongitude != null) {
                val dateTime = mWeatherList[pos].weatherCurrent?.dateTime
                if (dateTime?.let { dateTime -> Utils.checkTimeInterval(dateTime) } == true) {
                    mPresenter?.getWeather(itemLatitude, itemLongitude, mPosition, mIsNetworkEnable, isCurrent)
                } else {
                    mPresenter?.getWeather(itemLatitude, itemLongitude, mPosition)
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback.
        // TODO implement later
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
        val itemID = mWeatherList[mPosition].city + mWeatherList[mPosition].country
        val currentID = mWeatherCurrent?.city + mWeatherCurrent?.country
        val isCurrent = itemID == currentID
        mWeatherList[mPosition].latitude?.let { latitude ->
            mWeatherList[mPosition].longitude?.let { longitude ->
                mPresenter?.getWeather(
                    latitude,
                    longitude,
                    mPosition,
                    mIsNetworkEnable,
                    isCurrent
                )
            }
        }
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
