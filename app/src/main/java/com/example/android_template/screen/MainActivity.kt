package com.example.android_template.screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.android_template.data.model.remote_data.City
import com.example.android_template.data.model.CurrentWeatherModel
import com.example.android_template.data.repository.WeatherRepository
import com.example.android_template.data.repository.WeatherRepositoryImpl
import com.example.android_template.databinding.ActivityMainBinding
import com.example.android_template.screen.presenter.WeatherInfoShowPresenter
import com.example.android_template.screen.presenter.WeatherInfoShowPresenterImpl
import com.example.android_template.utils.convertToListOfCityName

class MainActivity : AppCompatActivity(), MainActivityView {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var model: WeatherRepository
    private lateinit var presenter: WeatherInfoShowPresenter

    private var cityList: MutableList<City> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // initialize model and presenter
        model = WeatherRepositoryImpl(applicationContext)
        presenter = WeatherInfoShowPresenterImpl(this, model)

        // call for fetching city list
        presenter.fetchCityList()


        binding.layoutHeader.btnViewWeather.setOnClickListener {
            binding.outputGroup.visibility = View.GONE

            val spinnerSelectedItemPos = binding.layoutHeader.spinner.selectedItemPosition

            // fetch weather info of specific city
            presenter.fetchWeatherInfo(cityList[spinnerSelectedItemPos].id)
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    /**
     * Activity doesn't know when should progress bar visible or hide. It only knows
     * how to show/hide it.
     * Presenter will decide the logic of progress bar visibility.
     * This method will be triggered by presenter when needed.
     */
    override fun handleProgressBarVisibility(visibility: Int) {
        binding.progressBar.visibility = visibility
    }

    /**
     * This method will be triggered when city list successfully fetched.
     * From where this list will be come? From local db or network call or from somewhere else?
     * Activity/View doesn't know and doesn't care anything about it. Activity only knows how to
     * show the city list on the UI and listen the click event of the Spinner.
     * Model knows about the data source of city list.
     */
    override fun onCityListFetchSuccess(cityList: MutableList<City>) {
        this.cityList = cityList

        val arrayAdapter = ArrayAdapter(
            this,
            androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item,
            cityList.convertToListOfCityName()
        )

        binding.layoutHeader.spinner.adapter = arrayAdapter
    }

    /**
     * This method will triggered if city list fetching process failed
     */
    override fun onCityListFetchFailure(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    /**
     * This method will triggered when weather information successfully fetched.
     * Activity/View doesn't know anything about the data source of weather API.
     * Only model knows about the data source of weather API.
     */
    override fun onWeatherInfoFetchSuccess(currentWeatherModel: CurrentWeatherModel) {
        binding.outputGroup.visibility = View.VISIBLE
        binding.tvErrorMessage.visibility = View.GONE

        binding.layoutWeatherBasic.tvDateTime.text = "Today, ${currentWeatherModel.dateTime}"
        binding.layoutWeatherBasic.tvTemperature.text = currentWeatherModel.temperature
        //Glide.with(this).load(currentWeatherModel.weatherConditionIconUrl).into(binding.icWeather)
        binding.layoutWeatherBasic.tvMainCondition.text = currentWeatherModel.weatherMainCondition
        binding.layoutWeatherBasic.tvWindValue.text = "${currentWeatherModel.windSpeed} km/h"
        binding.layoutWeatherBasic.tvHumidityValue.text = currentWeatherModel.humidity

        binding.layoutWeatherBasic.tvWind.text = "Wind"
        binding.layoutWeatherBasic.tvHumidity.text = "Hum"
        binding.layoutWeatherBasic.guideline.visibility = View.VISIBLE

    }

    /**
     * This method will triggered if weather information fetching process failed
     */
    override fun onWeatherInfoFetchFailure(errorMessage: String) {
        binding.outputGroup.visibility = View.GONE
        binding.tvErrorMessage.visibility = View.VISIBLE
        binding.tvErrorMessage.text = errorMessage
    }
}
