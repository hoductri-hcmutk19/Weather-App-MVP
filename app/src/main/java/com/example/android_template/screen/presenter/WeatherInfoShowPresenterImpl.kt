package com.example.android_template.screen.presenter

import android.view.View
import com.example.android_template.data.model.remote_data.City
import com.example.android_template.data.model.CurrentWeatherModel
import com.example.android_template.data.model.CurrentWeatherResponse
import com.example.android_template.data.repository.WeatherRepository
import com.example.android_template.screen.MainActivityView
import com.example.android_template.screen.RequestCompleteListener
import com.example.android_template.utils.kelvinToCelsius
import com.example.android_template.utils.mpsToKmph
import com.example.android_template.utils.unixTimestampToDateString

class WeatherInfoShowPresenterImpl(
    private var view: MainActivityView?,
    private val model: WeatherRepository
) : WeatherInfoShowPresenter {

    override fun fetchCityList() {

        model.getCityList(object : RequestCompleteListener<MutableList<City>> {

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

        model.getWeatherInformation(cityId, object : RequestCompleteListener<CurrentWeatherResponse> {

            // if successfully fetch the data
            override fun onRequestSuccess(data: CurrentWeatherResponse) {

                view?.handleProgressBarVisibility(View.GONE) // let view know about progress bar visibility

                // data formatting to show on UI
                val currentWeatherModel = CurrentWeatherModel(
                    dateTime = data.dt.unixTimestampToDateString(),
                    temperature = data.main.temp.kelvinToCelsius().toString(),
                    cityAndCountry = "${data.name}, ${data.sys.country}",
                    weatherConditionIconUrl = "https://openweathermap.org/img/wn/${data.weather[0].icon}@2x.png",
                    weatherConditionIconDescription = data.weather[0].description,
                    weatherMainCondition = data.weather[0].main,
                    humidity = "${data.main.humidity}%",
                    windSpeed = data.wind.speed.mpsToKmph().toString()
                )

                view?.onWeatherInfoFetchSuccess(currentWeatherModel)   //let view know the formatted weather data
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