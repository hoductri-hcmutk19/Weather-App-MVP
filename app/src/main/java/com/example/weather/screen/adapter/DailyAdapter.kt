package com.example.weather.screen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.data.model.entity.WeatherBasic
import com.example.weather.databinding.LayoutItemDailyBinding
import com.example.weather.utils.ext.getIcon
import com.example.weather.utils.ext.kelvinToCelsius
import com.example.weather.utils.ext.unixTimestampToDateString
import com.example.weather.utils.ext.unixTimestampToHourString

class DailyAdapter : RecyclerView.Adapter<DailyAdapter.DailyViewHolder>() {

    private val listWeathers by lazy { mutableListOf<WeatherBasic>() }

    fun updateData(listData: MutableList<WeatherBasic>) {
        listWeathers.clear()
        listWeathers.addAll(listData)
        notifyDataSetChanged()
    }

    inner class DailyViewHolder(private val binding: LayoutItemDailyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(newItem: WeatherBasic) {
            binding.textDailyTime.text = newItem.dateTime?.unixTimestampToDateString()
            val time = newItem.dateTime?.unixTimestampToHourString()?.toInt()
            if (time != null) {
                newItem.weatherMainCondition?.let { mainCondition ->
                    getIcon(mainCondition, time)?.let { image ->
                        binding.imageDailyIcon.setImageResource(image)
                    }
                }
            }
            binding.textDailyTemperature.text = newItem.temperature?.kelvinToCelsius().toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DailyViewHolder(LayoutItemDailyBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return listWeathers.size
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.bindData(listWeathers[position])
    }
}
