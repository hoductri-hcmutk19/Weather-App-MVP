package com.example.weather.screen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.data.model.entity.WeatherBasic
import com.example.weather.databinding.LayoutItemHourlyBinding
import com.example.weather.utils.ext.getIcon
import com.example.weather.utils.ext.kelvinToCelsius
import com.example.weather.utils.ext.unixTimestampToHourString
import com.example.weather.utils.ext.unixTimestampToTimeString
import com.example.weather.utils.listener.OnItemClickListener

class HourlyAdapter(var listener: OnItemClickListener, val context: Context) :
    RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>() {

    private val listWeathers by lazy { mutableListOf<WeatherBasic>() }
    var pairedList: MutableList<Pair<WeatherBasic, Int>> = mutableListOf<Pair<WeatherBasic, Int>>().apply {
        listWeathers.forEach { add(it to 0) }
    }
    private var prePosition = -1

    fun updateData(listData: MutableList<WeatherBasic>) {
        listWeathers.clear()
        listWeathers.addAll(listData)
        pairedList = mutableListOf<Pair<WeatherBasic, Int>>().apply {
            listWeathers.forEach { add(it to 0) }
        }
        notifyDataSetChanged()
    }

    inner class HourlyViewHolder(private val binding: LayoutItemHourlyBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        fun bindData(pairItem: Pair<WeatherBasic, Int>) {
            if (pairItem.second == 0){
                binding.cardViewHourly.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.transparent
                    )
                )
            } else {
                binding.cardViewHourly.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.cardBackgroundOpacity
                    )
                )
            }

            val newItem = pairItem.first
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

        override fun onClick(view: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION && position != prePosition) {
                if(prePosition != -1){
                    pairedList[prePosition] = pairedList[prePosition].copy(second = 0)
                }
                prePosition = position
                pairedList[position] = pairedList[position].copy(second = 1)
                if (view != null) {
                    listener.onItemClickListener(view, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return HourlyViewHolder(LayoutItemHourlyBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return pairedList.size
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        holder.bindData(pairedList[position])
    }
}
