package com.example.androidTemplate.screen.presenter

import android.view.View
import com.example.androidTemplate.data.model.City
import com.example.androidTemplate.data.model.CurrentWeather
import com.example.androidTemplate.data.model.CurrentWeatherData
import com.example.androidTemplate.data.repository.WeatherRepository
import com.example.androidTemplate.screen.MainActivityView
import com.example.androidTemplate.screen.RequestCompleteListener
import com.example.androidTemplate.utils.kelvinToCelsius
import com.example.androidTemplate.utils.mpsToKmph
import com.example.androidTemplate.utils.unixTimestampToDateString

class WeatherInfoShowPresenterImpl(
    private var view: MainActivityView?,
    private val model: WeatherRepository
) : WeatherInfoShowPresenter {

    override fun fetchCityList() {

        model.getCityLocal(object : RequestCompleteListener<MutableList<City>> {

            // if successfully fetch the city_list data
            override fun onRequestSuccess(data: MutableList<City>) {
                view?.onCityListFetchSuccess(data)  //let view know the formatted city list data
            }

            // if failed to fetch data
            override fun onRequestFailed(errorMessage: String) {
                view?.onCityListFetchFailure(errorMessage)  //let view know about failure
            }
        })
    }

    override fun fetchWeatherInfo(cityId: Int) {

        view?.handleProgressBarVisibility(View.VISIBLE) // let view know about progress bar visibility

        model.getWeather(cityId, object : RequestCompleteListener<CurrentWeather> {

            // if successfully fetch the data
            override fun onRequestSuccess(data: CurrentWeather) {

                view?.handleProgressBarVisibility(View.GONE) // let view know about progress bar visibility

                // data formatting to show on UI
                val currentWeather = CurrentWeatherData(
                    dateTime = data.dateTime.unixTimestampToDateString(),
                    temperature = data.temperature.kelvinToCelsius().toString(),
                    cityAndCountry = data.city + data.country,
                    weatherConditionIconUrl = "https://openweathermap.org/img/wn/${data.weatherConditionIconUrl}@2x.png",
                    weatherConditionIconDescription = data.weatherConditionIconDescription,
                    weatherMainCondition = data.weatherMainCondition,
                    humidity = "${data.humidity}%",
                    windSpeed = data.windSpeed.mpsToKmph().toString()
                )

                view?.onWeatherInfoFetchSuccess(currentWeather)   //let view know the formatted weather data
            }

            // if failed to fetch data
            override fun onRequestFailed(errorMessage: String) {
                view?.handleProgressBarVisibility(View.GONE) // let view know about progress bar visibility

                view?.onWeatherInfoFetchFailure(errorMessage) //let view know about failure
            }
        })
    }

    override fun detachView() {     // ngắt kết nối view và presenter
        view = null
    }
}



