package com.example.androidTemplate.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CurrentWeatherData(
    var dateTime: String = "",
    var temperature: String = "",
    var cityAndCountry: String = "",
    var weatherConditionIconUrl: String = "",
    var weatherConditionIconDescription: String = "",
    var weatherMainCondition: String = "",
    var humidity: String = "",
    var windSpeed: String = ""
) : Parcelable

