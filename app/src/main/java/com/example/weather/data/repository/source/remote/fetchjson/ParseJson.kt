package com.example.weather.data.repository.source.remote.fetchjson

import com.example.weather.data.model.CurrentWeather
import com.example.weather.data.model.WeatherEntry
import org.json.JSONObject

class ParseJson {
    fun weatherParseJson(jsonObject: JSONObject) = CurrentWeather().apply {
        jsonObject.let {
            dateTime = it.getInt(WeatherEntry.DATE_TIME)
            temperature = it.getJSONObject(WeatherEntry.MAIN).getDouble(WeatherEntry.TEMPERATURE)
            city = it.getString(WeatherEntry.CITY)
            country = it.getJSONObject(WeatherEntry.SYS).getString(WeatherEntry.COUNTRY)
            weatherConditionIconUrl =
                it.getJSONArray(WeatherEntry.WEATHER)
                    .getJSONObject(0)
                    .getString(WeatherEntry.WEATHER_CONDITION_ICON_URL)
            weatherConditionIconDescription =
                it.getJSONArray(WeatherEntry.WEATHER)
                    .getJSONObject(0)
                    .getString(WeatherEntry.WEATHER_CONDITION_ICON_DESCRIPTION)
            weatherMainCondition =
                it.getJSONArray(WeatherEntry.WEATHER)
                    .getJSONObject(0)
                    .getString(WeatherEntry.WEATHER_MAIN_CONDITION)
            humidity = it.getJSONObject(WeatherEntry.MAIN).getInt(WeatherEntry.HUMIDITY)
            windSpeed = it.getJSONObject(WeatherEntry.WIND).getDouble(WeatherEntry.WIND_SPEED)
        }
    }
}
