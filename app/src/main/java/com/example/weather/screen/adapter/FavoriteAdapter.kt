package com.example.weather.screen.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.data.model.Weather
import com.example.weather.databinding.LayoutItemFavoriteBinding
import com.example.weather.utils.ext.offsetToUTC
import com.example.weather.utils.listener.OnItemClickListener

class FavoriteAdapter(var listener: OnItemClickListener) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    private val mListWeathers by lazy { mutableListOf<Weather>() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(LayoutItemFavoriteBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return mListWeathers.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(mListWeathers[position])
        holder.button.setOnClickListener {
            listener.onItemClickListener(holder.itemView, position)
        }
    }

    fun updateData(listData: List<Weather>?) {
        mListWeathers.clear()
        if (listData != null) {
            mListWeathers.addAll(listData)
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: LayoutItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root) {
        val button = binding.buttonDelete

        @RequiresApi(Build.VERSION_CODES.O)
        fun bindData(newItem: Weather) {
            binding.textFavoriteLocation.text = newItem.getLocation()
            binding.textGmtLocation.text = newItem.timeZone?.offsetToUTC() ?: ""
        }
    }
}
