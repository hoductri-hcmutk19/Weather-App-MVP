package com.example.weather.screen

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.data.model.Weather
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.data.repository.source.local.WeatherLocalDataSource
import com.example.weather.data.repository.source.remote.WeatherRemoteDataSource
import com.example.weather.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var repository: WeatherRepository
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        repository = WeatherRepository.let {
            WeatherRepository.getInstance(
                WeatherLocalDataSource.getInstance(applicationContext),
                WeatherRemoteDataSource.getInstance()
            )
        }

        binding.fetch.setOnClickListener {
            repository.getWeather(
                10.7782076,
                106.704281,
                object : RequestCompleteListener<Weather> {
                    override fun onRequestSuccess(data: Weather) {
                        Log.v("myTag1", "$data")
                        repository.insertWeather(data)
                    }

                    override fun onRequestFailed(e: Exception?) {
                        Log.v("myTag", "failed")
                    }
                })
        }
        binding.getAll.setOnClickListener {
            val weatherList: List<Weather>? = repository.getAllLocalWeathers()
            Log.v("myTag2", "$weatherList")
        }
        binding.getOne.setOnClickListener {
            val weather: Weather? = repository.getLocalWeather("Ho Chi Minh CityVN")
            Log.v("myTag3", "$weather")
        }
        binding.getOverall.setOnClickListener {
            val weatherList: List<Weather>? = repository.getAllLocalOveralls()
            Log.v("myTag4", "$weatherList")
        }
        binding.delete.setOnClickListener {
            repository.deleteWeather("Ho Chi Minh CityVN")
        }

    }
}
