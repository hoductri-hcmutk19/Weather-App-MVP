package com.example.weather.screen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.data.model.entity.WeatherBasic
import com.example.weather.databinding.LayoutItemHourlyBinding
import com.example.weather.utils.ext.getIcon
import com.example.weather.utils.ext.kelvinToCelsius
import com.example.weather.utils.ext.unixTimestampToHourString
import com.example.weather.utils.ext.unixTimestampToTimeString
import com.example.weather.utils.listener.OnItemClickListener

class HourlyAdapter(var listener: OnItemClickListener) : RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>() {

    private val listWeathers by lazy { mutableListOf<WeatherBasic>() }

    fun updateData(listData: MutableList<WeatherBasic>) {
        listWeathers.clear()
        listWeathers.addAll(listData)
        notifyDataSetChanged()
    }

    inner class HourlyViewHolder(private val binding: LayoutItemHourlyBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        fun bindData(newItem: WeatherBasic) {
            binding.textHourlyTime.text = newItem.dateTime?.unixTimestampToTimeString()
            val time = newItem.dateTime?.unixTimestampToHourString()?.toInt()
            if (time != null) {
                newItem.weatherMainCondition?.let { mainCondition ->
                    getIcon(mainCondition, time)?.let { image ->
                        binding.imageHourlyIcon.setImageResource(image)
                    }
                }
            }
            binding.textHourlyTemperature.text = newItem.temperature?.kelvinToCelsius().toString()
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClickListener(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return HourlyViewHolder(LayoutItemHourlyBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return listWeathers.size
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        holder.bindData(listWeathers[position])
    }
}
