package com.example.weather.screen.detail

import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.children
import com.example.weather.R
import com.example.weather.data.model.Weather
import com.example.weather.data.model.entity.WeatherBasic
import com.example.weather.databinding.FragmentDetailBinding
import com.example.weather.screen.adapter.DailyAdapter
import com.example.weather.screen.adapter.HourlyAdapter
import com.example.weather.utils.Constant
import com.example.weather.utils.base.BaseFragment
import com.example.weather.utils.ext.unixTimestampToDateString
import com.example.weather.utils.ext.unixTimestampToDateYearString
import com.example.weather.utils.goBackFragment
import com.example.weather.utils.listener.OnItemClickListener

class DetailFragment private constructor() : BaseFragment<FragmentDetailBinding>(), OnItemClickListener {

    private val hourlyAdapter: HourlyAdapter by lazy {
        HourlyAdapter(this, requireContext())
    }
    private val dailyAdapter: DailyAdapter by lazy {
        DailyAdapter()
    }
    private var mHourlyPosition = 0

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
        viewBinding.layoutHeader.icBack.setOnClickListener {
            goBackFragment()
        }
    }

    override fun checkNetwork(activity: Activity?) {
        // TODO implement later
    }

    override fun onItemClickListener(view: View, position: Int) {
        mHourlyPosition = position
        viewBinding.layoutHourly.recyclerViewHourly.children.iterator().forEach { item ->
            item as CardView
            item.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.transparent
                )
            )
        }
        view as CardView
        view.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.cardBackgroundOpacity
            )
        )

        val currentDay = mWeather.weatherCurrent?.dateTime?.unixTimestampToDateString()?.toInt()
        val itemHourlyClickDay =
            mWeather.weatherHourlyList?.get(mHourlyPosition)?.dateTime?.unixTimestampToDateString()?.toInt()
        if (itemHourlyClickDay != null) {
            viewBinding.layoutHourly.currentDate.text =
                mWeather.weatherHourlyList?.get(mHourlyPosition)?.dateTime?.unixTimestampToDateYearString()
            val distance = itemHourlyClickDay - currentDay!!
            when (distance) {
                0 -> viewBinding.layoutHourly.today.text = getString(R.string.today)
                1 -> viewBinding.layoutHourly.today.text = getString(R.string.tomorrow)
                2 -> viewBinding.layoutHourly.today.text = getString(R.string.after_tomorrow)
            }
        }
    }

    private fun bindDataToView(weather: Weather) {
        weather.weatherHourlyList?.let {
            hourlyAdapter.updateData(it as MutableList<WeatherBasic>)
        }
        weather.weatherDailyList?.let {
            dailyAdapter.updateData(it as MutableList<WeatherBasic>)
        }
        viewBinding.layoutHourly.currentDate.text =
            mWeather.weatherCurrent?.dateTime?.unixTimestampToDateYearString() ?: ""
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
