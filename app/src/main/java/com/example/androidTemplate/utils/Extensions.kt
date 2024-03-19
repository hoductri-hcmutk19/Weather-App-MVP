package com.example.androidTemplate.utils

import com.example.androidTemplate.data.model.remoteData.City
import java.text.SimpleDateFormat
import java.util.*

fun Int.unixTimestampToDateTimeString() : String {

    try {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this * 1000.toLong()

        val outputDateFormat = SimpleDateFormat("dd MMM, yyyy - hh:mm a", Locale.ENGLISH)
        outputDateFormat.timeZone = TimeZone.getDefault() // user's default time zone
        return outputDateFormat.format(calendar.time)

    } catch (e: IllegalArgumentException) {
        println(e)
    }

    return this.toString()
}

fun Int.unixTimestampToDateString() : String {

    try {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this * 1000.toLong()

        val outputDateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
        outputDateFormat.timeZone = TimeZone.getDefault() // user's default time zone
        return outputDateFormat.format(calendar.time)

    } catch (e: IllegalArgumentException) {
        println(e)
    }

    return this.toString()
}

fun Int.unixTimestampToTimeString() : String {

    try {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this * 1000.toLong()

        val outputDateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        outputDateFormat.timeZone = TimeZone.getDefault()
        return outputDateFormat.format(calendar.time)

    } catch (e: IllegalArgumentException) {
        println(e)
    }

    return this.toString()
}

fun MutableList<City>.convertToListOfCityName() : MutableList<String> {

    val cityNameList: MutableList<String> = mutableListOf()

    for (city in this) {
        cityNameList.add(city.name)
    }

    return  cityNameList
}

/**
 * The temperature T in degrees Celsius (°C) is equal to the temperature T in Kelvin (K) minus 273.15:
 * T(°C) = T(K) - 273.15
 *
 * Example
 * Convert 300 Kelvin to degrees Celsius:
 * T(°C) = 300K - 273.15 = 26.85 °C
 */
fun Double.kelvinToCelsius() : Int {
    val differenceNumber: Double = 273.15
    return  (this - differenceNumber).toInt()
}

fun Double.mpsToKmph() : Int {
    val coefficient: Double = 3.6
    return  (this * coefficient).toInt()
}


