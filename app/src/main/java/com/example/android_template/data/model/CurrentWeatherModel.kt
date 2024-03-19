package com.example.android_template.data.model

data class CurrentWeatherModel(
        var dateTime: String = "",
        var temperature: String = "0",
        var cityAndCountry: String = "",
        var weatherConditionIconUrl: String = "",
        var weatherConditionIconDescription: String = "",
        var weatherMainCondition: String = "",
        var humidity: String = "",
        var windSpeed: String = ""
)