package com.example.weather.data.anotation

import androidx.annotation.StringDef
import com.example.weather.data.anotation.Icon.Companion.CLEAR_DAY
import com.example.weather.data.anotation.Icon.Companion.CLEAR_NIGHT
import com.example.weather.data.anotation.Icon.Companion.CLOUDS_DAY
import com.example.weather.data.anotation.Icon.Companion.CLOUDS_NIGHT
import com.example.weather.data.anotation.Icon.Companion.RAIN_DAY
import com.example.weather.data.anotation.Icon.Companion.RAIN_NIGHT
import com.example.weather.data.anotation.Icon.Companion.SNOW_DAY
import com.example.weather.data.anotation.Icon.Companion.SNOW_NIGHT

@Retention(AnnotationRetention.SOURCE)
@StringDef(
    CLEAR_DAY,
    CLEAR_NIGHT,
    RAIN_DAY,
    RAIN_NIGHT,
    SNOW_DAY,
    SNOW_NIGHT,
    CLOUDS_DAY,
    CLOUDS_NIGHT
)
annotation class Icon {
    companion object {
        const val CLEAR_DAY = "clear-day"
        const val CLEAR_NIGHT = "clear-night"
        const val RAIN_DAY = "rain-day"
        const val RAIN_NIGHT = "rain-night"
        const val SNOW_DAY = "snow-day"
        const val SNOW_NIGHT = "snow-night"
        const val CLOUDS_DAY = "clouds-day"
        const val CLOUDS_NIGHT = "clouds-night"
    }
}
