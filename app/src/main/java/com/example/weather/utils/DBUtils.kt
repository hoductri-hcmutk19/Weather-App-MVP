package com.example.weather.utils

import com.example.weather.data.model.WeatherEntry
import com.example.weather.data.model.entity.WeatherBasic
import com.example.weather.data.model.entity.WeatherBasicEntry
import org.json.JSONException
import org.json.JSONObject

object DBUtils {
    fun createJsonObject(vararg any: Any?): String {
        val jsonObject = JSONObject()
        try {
            jsonObject.apply {
                put(WeatherBasicEntry.DATE_TIME, any[DATE_TIME_INDEX])
                put(WeatherBasicEntry.TEMPERATURE, any[TEMPERATURE_INDEX])
                put(WeatherBasicEntry.MAIN, any[MAIN_INDEX])
                put(WeatherBasicEntry.WEATHER_DESCRIPTION, any[WEATHER_DESCRIPTION_INDEX])
                put(WeatherBasicEntry.HUMIDITY, any[HUMIDITY_INDEX])
                put(WeatherBasicEntry.WIND_SPEED, any[WIND_SPEED_INDEX])
            }
        } catch (e: JSONException) {
            println(e)
        }
        return jsonObject.toString()
    }

    fun parseJsonToBasicWeather(jsonObject: JSONObject, tagObject: String): WeatherBasic {
        return when (tagObject) {
            WeatherEntry.HOURLY_OBJECT, WeatherEntry.DAILY_OBJECT -> {
                WeatherBasic(
                    jsonObject.getInt(WeatherBasicEntry.DATE_TIME),
                    jsonObject.getDouble(WeatherBasicEntry.TEMPERATURE),
                    jsonObject.getString(WeatherBasicEntry.MAIN),
                    null,
                    null,
                    null
                )
            }

            else -> {
                WeatherBasic(
                    jsonObject.getInt(WeatherBasicEntry.DATE_TIME),
                    jsonObject.getDouble(WeatherBasicEntry.TEMPERATURE),
                    jsonObject.getString(WeatherBasicEntry.MAIN),
                    jsonObject.getString(WeatherBasicEntry.WEATHER_DESCRIPTION),
                    jsonObject.getInt(WeatherBasicEntry.HUMIDITY),
                    jsonObject.getDouble(WeatherBasicEntry.WIND_SPEED)
                )
            }
        }
    }

    // Index
    private const val DATE_TIME_INDEX = 0
    private const val TEMPERATURE_INDEX = 1
    private const val MAIN_INDEX = 2
    private const val WEATHER_DESCRIPTION_INDEX = 3
    private const val HUMIDITY_INDEX = 4
    private const val WIND_SPEED_INDEX = 5
}
