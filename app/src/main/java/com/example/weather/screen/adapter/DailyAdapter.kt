package com.example.weather.screen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.data.model.entity.WeatherBasic
import com.example.weather.databinding.LayoutItemDailyBinding
import com.example.weather.utils.ext.getIcon
import com.example.weather.utils.ext.kelvinToCelsius
import com.example.weather.utils.ext.unixTimestampToDateMonthString
import com.example.weather.utils.ext.unixTimestampToHourString

class DailyAdapter : RecyclerView.Adapter<DailyAdapter.ViewHolder>() {

    private val mListWeathers by lazy { mutableListOf<WeatherBasic>() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(LayoutItemDailyBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return mListWeathers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(mListWeathers[position])
    }

    fun updateData(listData: MutableList<WeatherBasic>) {
        mListWeathers.clear()
        mListWeathers.addAll(listData)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: LayoutItemDailyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(newItem: WeatherBasic) {
            binding.textDailyTime.text = newItem.dateTime?.unixTimestampToDateMonthString()
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
}
