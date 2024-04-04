package com.example.weather.screen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.data.model.WeatherEntry
import com.example.weather.data.model.entity.WeatherBasic

class WeatherAdapter(
    private val layoutResource: Int,
    private val tagObject: String
) : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    private val listWeathers by lazy { mutableListOf<WeatherBasic>() }

    fun updateData(listData: MutableList<WeatherBasic>) {
        listWeathers.clear()
        listWeathers.addAll(listData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(layoutResource, parent, false))
    }

    override fun getItemCount() = listWeathers.size

    override fun onBindViewHolder(holder: WeatherAdapter.ViewHolder, position: Int) {
        when(tagObject) {
            WeatherEntry.HOURLY_OBJECT -> holder.bindDataHourly(listWeathers[position])
            else -> holder.bindDataDaily(listWeathers[position])
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindDataHourly(data: WeatherBasic) {
            data.dateTime?.let { time ->
                itemView.textHourlyTime.text =
                    WeatherUtils.formatTime(itemView.context, time, WeatherEntry.HOURLY_OBJECT)
            }
            temperatureUnit?.let {
                data.temperature?.let { temperature ->
                    itemView.textHourlyTemperature.text =
                        WeatherUtils.formatTemperature(
                            WeatherUtils.temperatureUnit(temperature, it))
                }
            }
            data.icon?.let {
                WeatherUtils.getIcon(it)?.let { image ->
                    itemView.imageHourlyIcon.setImageResource(image)
                }
            }
        }

        fun bindDataDaily(data: WeatherStatistics) {
            data.time?.let {
                itemView.textDailyTime.text =
                    WeatherUtils.formatTime(itemView.context, it, WeatherEntry.DAILY_OBJECT)
            }
            temperatureUnit?.let {
                if (data.temperatureMin != null && data.temperatureMax != null) {
                    itemView.textDailyMinMaxTemperature.text =  WeatherUtils.formatTemperature(
                        WeatherUtils.temperatureUnit(data.temperatureMin, it),
                        WeatherUtils.temperatureUnit(data.temperatureMax, it))
                }
            }
            data.icon?.let {
                WeatherUtils.getIcon(it)?.let { image ->
                    itemView.imageDailyIcon.setImageResource(image)
                }
            }
        }
    }
}
