package com.example.weather.screen.home

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.R
import com.example.weather.data.model.Weather
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.data.repository.source.local.WeatherLocalDataSource
import com.example.weather.data.repository.source.remote.WeatherRemoteDataSource
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.databinding.FragmentWeatherBinding
import com.example.weather.utils.Constant
import com.example.weather.utils.PermissionUtils
import com.example.weather.utils.addFragmentToActivity
import com.example.weather.utils.setupToolbar
import java.security.cert.Extension

class WeatherFragment private constructor() : Fragment(), WeatherContract.View,
    SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private lateinit var binding: FragmentWeatherBinding

    private val progressBarLoading: ProgressDialog by lazy {
        ProgressDialog(context)
    }

    private val sharedPreferences by lazy {
        context?.getSharedPreferences(Constant.PREF_SPEED_AND_TEMPERATURE_UNIT, Context.MODE_PRIVATE)
    }

    private var repository: WeatherRepository? = null
    private var presenter: WeatherPresenter? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var speedUnit: String? = null
    private var temperatureUnit: String? = null
    private var speedValue: Double = 0.0
    private var weatherTemp: Weather? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        (activity as? AppCompatActivity)?.setupToolbar(binding.toolbarWeather, getString(R.string.app_name))

        binding.swipeRefreshWeather.setOnRefreshListener(this)
    }

    private fun initData() {
        repository = context?.let {
            WeatherRepository.getInstance(
                WeatherLocalDataSource.getInstance(it),
                WeatherRemoteDataSource.getInstance()
            )
        }
        presenter = repository?.let { WeatherPresenter(it) }
        presenter?.setView(this)
        speedUnit = sharedPreferences?.getString(Constant.SPEED_UNIT_KEY, "m/s")
        temperatureUnit =
            sharedPreferences?.getString(Constant.TEMPERATURE_UNIT_KEY, "\u2103")
        arguments?.let {
            latitude = it.getDouble(Constant.LATITUDE_KEY)
            longitude = it.getDouble(Constant.LONGITUDE_KEY)
            activity?.let { activity ->
                if (PermissionUtils.isNetWorkEnabled(activity)) {
                    presenter?.getWeather(latitude, longitude)
                } else {
                    onInternetConnectionFailed()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_weather_screen, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.menu_cities) {
//            activity?.let {
//                (it as AppCompatActivity).addFragmentToActivity(
//                    it.supportFragmentManager,
//                    CitiesFragment.newInstance(),
//                    R.id.container)
//            }
//        }
        return super.onOptionsItemSelected(item)
    }

    override fun onProgressLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBarLoading.show()
            progressBarLoading.setContentView(R.layout.layout_loading_weather)
        } else {
            progressBarLoading.dismiss()
        }
    }

    override fun onGetCurrentWeatherSuccess(weather: Weather) {
        weatherTemp = weather

         bindDataToView(weather)
    }

    private fun bindDataToView(weather: Weather) {
        binding.footer.progressBarHumidity.max = 100
        binding.header.textLocation.text = weather.city + " " + weather.country
        binding.header.textTemperature.text = weather.city
//        weather.apply {
//            weatherCurrent?.let {
//                it.time?.let { time ->
//                    textTimeUpdate.text =
//                        WeatherUtils.formatTime(context, time, Constant.TAG_LAST_UPDATE)
//                }
//                textSummary.text = it.summary
//                textTemperature.text = WeatherUtils.formatTemperature(
//                    getTemperature(it.temperature, unit))
//                textMinMaxTemperature.text = WeatherUtils.formatTemperature(
//                    getTemperature(it.temperatureMin, unit),
//                    getTemperature(it.temperatureMax, unit))
//                it.wind?.let { wind ->
//                    val unit = speedUnit?: SpeedUnit.MS
//                    textWindDirectionValue.text = wind.windDirection?.let { windDirection ->
//                        WeatherUtils.formatWindDirection(windDirection)
//                    }
//                    textWindSpeedValue.text = wind.windSpeed?.let { windSpeed ->
//                        speedValue = windSpeed
//                        WeatherUtils.formatWindSpeed(windSpeed, unit)
//                    }
//                    textWindSpeedUnit.text =
//                        StringBuilder(" - ").append(WeatherUtils.changeSpeedUnit(unit))
//                }
//                it.humidity?.let { humidity ->
//                    progressBarHumidity.progress = humidity.times(100).roundToInt()
//                    textPercentHumidity.text = WeatherUtils.formatHumidity(humidity)
//                }
//            }
//        }
    }

//    private fun getTemperature(temperature: Double?, unit: String): Int {
//        temperature?.let {
//            return WeatherUtils.temperatureUnit(it, unit)
//        }
//        return 0
//    }

    override fun onInternetConnectionFailed() {
        Toast.makeText(context, "The network is not responding, please check the wifi and mobile network",
            Toast.LENGTH_SHORT).show()
        Log.v("myTag1", "onInternetConnectionFailed")
        presenter?.getWeatherLocal()
    }

    override fun onError(exception: Exception) {
        Toast.makeText(context, exception.message.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onRefresh() {
        activity?.let {
            if (PermissionUtils.isNetWorkEnabled(it)) {
                presenter?.getWeather(latitude, longitude)
            } else {
                presenter?.getWeatherLocal()
            }
        }
        binding.swipeRefreshWeather.isRefreshing = false
    }

    override fun onClick(v: View?) {
//        speedUnit?.let {
//            speedUnit = WeatherUtils.changeSpeedUnit(it)
//            textWindSpeedValue.text =
//                WeatherUtils.formatWindSpeed(speedValue, WeatherUtils.changeSpeedUnit(it))
//            textWindSpeedUnit.text = StringBuilder(" - ").append(it)
//        }
//        sharedPreferences?.edit()?.putString(Constant.SPEED_UNIT_KEY, speedUnit)?.apply()
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences?.edit()?.putString(Constant.SPEED_UNIT_KEY, speedUnit)?.apply()
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
