package com.example.weather.data.repository.source.remote.fetchjson

import com.example.weather.data.model.Weather
import com.example.weather.data.model.WeatherEntry
import com.example.weather.data.model.entity.WeatherBasic
import com.example.weather.data.model.entity.WeatherBasicEntry
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ParseDataWithJson {
    @Throws(Exception::class)
    fun getJsonFromUrl(urlList: MutableList<String>?): MutableList<String> {
        val listJsonString: MutableList<String> = mutableListOf()
        if (urlList != null) {
            for (urlString in urlList){
                val url = URL(urlString)
                val httpURLConnection =
                    url.openConnection() as HttpURLConnection
                httpURLConnection.connectTimeout = TIME_OUT
                httpURLConnection.readTimeout = TIME_OUT
                httpURLConnection.requestMethod = METHOD_GET
                httpURLConnection.doOutput = true
                httpURLConnection.connect()
                val bufferedReader =
                    BufferedReader(InputStreamReader(url.openStream()))
                val stringBuilder = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
                bufferedReader.close()
                httpURLConnection.disconnect()
                listJsonString.add(stringBuilder.toString())
            }
        }
        return listJsonString
    }

    private fun parseJsonToDataWeather(
        jsonObject: JSONObject,
        parameterObject: String
    ): MutableList<WeatherBasic> {
        val dataWeatherList = mutableListOf<WeatherBasic>()
        try {
            val jsonArray = jsonObject.getJSONArray(WeatherBasicEntry.LIST)
            when(parameterObject) {
                WeatherEntry.DAILY_OBJECT -> {
                    for (i in 0 until jsonArray.length()) {
                        val weatherDataJson = jsonArray.getJSONObject(i)
                        dataWeatherList.add(
                            parseJsonElementWeather(weatherDataJson, WeatherEntry.DAILY_OBJECT)
                        )
                    }
                }
                WeatherEntry.HOURLY_OBJECT -> {
                    for (i in 0 until jsonArray.length()) {
                        val weatherDataJson = jsonArray.getJSONObject(i)
                        dataWeatherList.add(
                            parseJsonElementWeather(weatherDataJson, WeatherEntry.HOURLY_OBJECT)
                        )
                    }
                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return dataWeatherList
    }

    fun parseJsonElementWeather(jsonObject: JSONObject, tagObject: String): WeatherBasic {
        return when(tagObject) {
            WeatherEntry.DAILY_OBJECT -> {
                WeatherBasic(jsonObject.getInt(WeatherBasicEntry.DATE_TIME),
                    jsonObject.getJSONObject(WeatherBasicEntry.TEMPERATURE)
                        .getDouble(WeatherBasicEntry.TEMP_DAY),
                    jsonObject.getJSONArray(WeatherBasicEntry.WEATHER)
                        .getJSONObject(0)
                        .getString(WeatherBasicEntry.MAIN),
                    null,
                    null,
                    null)
            }
            WeatherEntry.HOURLY_OBJECT -> {
                WeatherBasic(jsonObject.getInt(WeatherBasicEntry.DATE_TIME),
                    jsonObject.getJSONObject(WeatherBasicEntry.MAIN)
                        .getDouble(WeatherBasicEntry.TEMPERATURE),
                    jsonObject.getJSONArray(WeatherBasicEntry.WEATHER)
                        .getJSONObject(0)
                        .getString(WeatherBasicEntry.MAIN),
                    null,
                    null,
                    null)
            }
            else -> {
                WeatherBasic(jsonObject.getInt(WeatherBasicEntry.DATE_TIME),
                    jsonObject.getJSONObject(WeatherBasicEntry.MAIN)
                        .getDouble(WeatherBasicEntry.TEMPERATURE),
                    jsonObject.getJSONArray(WeatherBasicEntry.WEATHER)
                        .getJSONObject(0)
                        .getString(WeatherBasicEntry.MAIN),
                    jsonObject.getJSONArray(WeatherBasicEntry.WEATHER)
                        .getJSONObject(0)
                        .getString(WeatherBasicEntry.WEATHER_DESCRIPTION),
                    jsonObject.getJSONObject(WeatherBasicEntry.MAIN)
                        .getInt(WeatherBasicEntry.HUMIDITY),
                    jsonObject.getJSONObject(WeatherBasicEntry.WIND)
                        .getDouble(WeatherBasicEntry.WIND_SPEED))
            }
        }
    }

    fun parseJsonToWeather(dataList: MutableList<String>): Weather? {
        var weather: Weather? = null
        try {
            val hourlyWeatherList =
                parseJsonToDataWeather(JSONObject(dataList[1]), WeatherEntry.HOURLY_OBJECT)
            val dailyWeatherList =
                parseJsonToDataWeather(JSONObject(dataList[2]), WeatherEntry.DAILY_OBJECT)
            val currentJsonObject = JSONObject(dataList[0])
            weather = Weather(
                currentJsonObject.getJSONObject(WeatherEntry.COORDINATE)
                    .getDouble(WeatherEntry.LAT),
                currentJsonObject.getJSONObject(WeatherEntry.COORDINATE)
                    .getDouble(WeatherEntry.LON),
                currentJsonObject.getInt(WeatherEntry.TIME_ZONE),
                currentJsonObject.getString(WeatherEntry.CITY),
                currentJsonObject.getJSONObject(WeatherEntry.SYS)
                    .getString(WeatherEntry.COUNTRY),
                parseJsonElementWeather(currentJsonObject, WeatherEntry.CURRENTLY_OBJECT),
                hourlyWeatherList,
                dailyWeatherList)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return weather
    }

    companion object {
        private const val TIME_OUT = 15000
        private const val METHOD_GET = "GET"
    }
}