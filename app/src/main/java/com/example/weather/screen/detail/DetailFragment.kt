package com.example.weather.screen.detail

import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import com.example.weather.data.model.Weather
import com.example.weather.data.model.entity.WeatherBasic
import com.example.weather.databinding.FragmentDetailBinding
import com.example.weather.screen.adapter.DailyAdapter
import com.example.weather.screen.adapter.HourlyAdapter
import com.example.weather.utils.Constant
import com.example.weather.utils.base.BaseFragment
import com.example.weather.utils.listener.OnItemClickListener

class DetailFragment private constructor() : BaseFragment<FragmentDetailBinding>(), OnItemClickListener {

    private val hourlyAdapter: HourlyAdapter by lazy {
        HourlyAdapter(this)
    }
    private val dailyAdapter: DailyAdapter by lazy {
        DailyAdapter()
    }
    private lateinit var mWeather: Weather

    override fun inflateViewBinding(inflater: LayoutInflater): FragmentDetailBinding {
        return FragmentDetailBinding.inflate(inflater)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initData() {
        arguments?.let { args ->
            mWeather = args.getParcelable(Constant.WEATHER_KEY)!!
            bindDataToView(mWeather)
        }
    }

    override fun initView() {
        viewBinding.layoutHourly.recyclerViewHourly.adapter = hourlyAdapter
        viewBinding.layoutDaily.recyclerViewDaily.adapter = dailyAdapter
        viewBinding.layoutHeader.icBack.setOnClickListener { }
    }

    override fun checkNetwork(activity: Activity?) {
        // TODO implement later
    }

    override fun onItemClickListener(position: Int) {
        // TODO implement later
    }

    private fun bindDataToView(weather: Weather) {
        weather.weatherHourlyList?.let {
            hourlyAdapter.updateData(it as MutableList<WeatherBasic>)
        }
        weather.weatherDailyList?.let {
            dailyAdapter.updateData(it as MutableList<WeatherBasic>)
        }
    }

    companion object {
        fun newInstance(weather: Weather) =
            DetailFragment().apply {
                arguments = bundleOf(
                    Constant.WEATHER_KEY to weather
                )
            }
    }
}
