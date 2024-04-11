@file:Suppress("TooManyFunctions")

package com.example.weather.utils.ext

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weather.R
import com.example.weather.data.anotation.Icon
import com.example.weather.data.anotation.Icon.Companion.CLEAR
import com.example.weather.data.anotation.Icon.Companion.CLOUDS
import com.example.weather.data.anotation.Icon.Companion.RAIN
import com.example.weather.data.anotation.Icon.Companion.SNOW
import com.example.weather.utils.Constant.KELVIN_TO_CELSIUS_NUMBER
import com.example.weather.utils.Constant.MPS_TO_KMPH_NUMBER
import com.example.weather.utils.Constant.NIGHT_TIME_END
import com.example.weather.utils.Constant.NIGHT_TIME_START
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.util.*

fun Int.unixTimestampToDateTimeString(): String {
    try {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this * 1000.toLong()

        val outputDateFormat = SimpleDateFormat("dd MMM - hh:mm a", Locale.ENGLISH)
        outputDateFormat.timeZone = TimeZone.getDefault() // user's default time zone
        return outputDateFormat.format(calendar.time)
    } catch (e: IllegalArgumentException) {
        println(e)
    }

    return this.toString()
}

fun Int.unixTimestampToDateYearString(): String {
    try {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this * 1000.toLong()

        val outputDateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
        outputDateFormat.timeZone = TimeZone.getDefault()
        return outputDateFormat.format(calendar.time)
    } catch (e: IllegalArgumentException) {
        println(e)
    }

    return this.toString()
}

fun Int.unixTimestampToDateMonthString(): String {
    try {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this * 1000.toLong()

        val outputDateFormat = SimpleDateFormat("MMM, dd", Locale.ENGLISH)
        outputDateFormat.timeZone = TimeZone.getDefault()
        return outputDateFormat.format(calendar.time)
    } catch (e: IllegalArgumentException) {
        println(e)
    }

    return this.toString()
}

fun Int.unixTimestampToDateString(): String {
    try {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this * 1000.toLong()

        val outputDateFormat = SimpleDateFormat("dd", Locale.ENGLISH)
        outputDateFormat.timeZone = TimeZone.getDefault()
        return outputDateFormat.format(calendar.time)
    } catch (e: IllegalArgumentException) {
        println(e)
    }

    return this.toString()
}

fun Int.unixTimestampToHourString(): String {
    try {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this * 1000.toLong()

        val outputDateFormat = SimpleDateFormat("HH", Locale.ENGLISH)
        outputDateFormat.timeZone = TimeZone.getDefault()
        return outputDateFormat.format(calendar.time)
    } catch (e: IllegalArgumentException) {
        println(e)
    }

    return this.toString()
}

fun Int.unixTimestampToTimeString(): String {
    try {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this * 1000.toLong()

        val outputDateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        outputDateFormat.timeZone = TimeZone.getDefault()
        return outputDateFormat.format(calendar.time)
    } catch (e: IllegalArgumentException) {
        println(e)
    }

    return this.toString()
}

@RequiresApi(Build.VERSION_CODES.O)
fun Int.offsetToUTC(): String {
    val zoneOffset = ZoneOffset.ofTotalSeconds(this)
    return "UTC$zoneOffset"
}

/**
 * The temperature T in degrees Celsius (°C) is equal to the temperature T in Kelvin (K) minus 273.15:
 * T(°C) = T(K) - 273.15
 *
 * Example
 * Convert 300 Kelvin to degrees Celsius:
 * T(°C) = 300K - 273.15 = 26.85 °C
 */
fun Double.kelvinToCelsius(): Int {
    return (this - KELVIN_TO_CELSIUS_NUMBER).toInt()
}

fun Double.mpsToKmph(): Int {
    return (this * MPS_TO_KMPH_NUMBER).toInt()
}

fun getIcon(@Icon icon: String, time: Int): Int? {
    return if (time > NIGHT_TIME_START || time < NIGHT_TIME_END) {
        getNightIcons()[icon]
    } else {
        getBrightIcons()[icon]
    }
}

private fun getBrightIcons() = hashMapOf(
    CLEAR to R.drawable.ic_clear_day,
    RAIN to R.drawable.ic_rain_day,
    SNOW to R.drawable.ic_snow_day,
    CLOUDS to R.drawable.ic_clouds_day
)

private fun getNightIcons() = hashMapOf(
    CLEAR to R.drawable.ic_clear_night,
    RAIN to R.drawable.ic_rain_night,
    SNOW to R.drawable.ic_snow_night,
    CLOUDS to R.drawable.ic_clouds_night
)
