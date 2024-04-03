package com.example.weather.utils

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object Utils {
    fun distanceBetweenPoints(
        latA: Double,
        lonA: Double,
        latB: Double,
        lonB: Double
    ): Double {
        val dLat = Math.toRadians(latB - latA)
        val dLon = Math.toRadians(lonB - lonA)
        val latARad = Math.toRadians(latA)
        val latBRad = Math.toRadians(latB)

        // Haversine formula
        val a = sin(dLat / 2) * sin(dLat / 2) +
            sin(dLon / 2) * sin(dLon / 2) * cos(latARad) * cos(latBRad)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return Constant.EARTH_RADIUS * c
    }
}
