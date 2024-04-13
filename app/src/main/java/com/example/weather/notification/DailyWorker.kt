package com.example.weather.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.location.Location
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weather.R
import com.example.weather.data.model.Weather
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.data.repository.source.local.WeatherLocalDataSource
import com.example.weather.data.repository.source.remote.WeatherRemoteDataSource
import com.example.weather.screen.MainActivity
import com.example.weather.screen.RequestCompleteListener
import com.example.weather.utils.ext.kelvinToCelsius
import com.example.weather.utils.ext.mpsToKmph
import com.example.weather.utils.ext.unixTimestampToTimeString
import com.google.android.gms.location.LocationServices
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DailyWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    private val mExecutor: Executor = Executors.newSingleThreadExecutor()

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        val repository = applicationContext.let { context ->
            WeatherRepository.getInstance(
                WeatherLocalDataSource.getInstance(context),
                WeatherRemoteDataSource.getInstance()
            )
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                fetchWeather(repository, location)
            }

        return Result.success()
    }

    private fun fetchWeather(repository: WeatherRepository, currentLocation: Location?) {
        mExecutor.execute {
            currentLocation?.let { currentLocation ->
                repository.fetchWeatherForecastCurrent(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    object : RequestCompleteListener<Weather> {
                        override fun onRequestSuccess(data: Weather) {
                            val location = data.getLocation()
                            val weatherDescription = data.weatherCurrent?.weatherDescription
                            val temperature = data.weatherCurrent?.temperature?.kelvinToCelsius().toString()
                            val windSpeed = data.weatherCurrent?.windSpeed?.mpsToKmph().toString()
                            val dateTime = data.weatherCurrent?.dateTime?.unixTimestampToTimeString()
                            val notificationTitle = "$location          $dateTime"
                            val notificationContent =
                                "$weatherDescription          ${
                                applicationContext.getString(R.string.temperature)
                                } $temperature℃          ${
                                applicationContext.getString(R.string.wind_speed)
                                } $windSpeed km/h"
                            showNotification(notificationTitle, notificationContent)
                        }

                        override fun onRequestFailed(e: Exception?) {
                            showNotification(
                                applicationContext.getString(R.string.good_morning),
                                applicationContext.getString(R.string.notification_content)
                            )
                        }
                    }
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(title: String, content: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_app)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Weather Daily"
            val channelDescription = "Notify weather in the morning"
            val channelImportance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(CHANNEL_ID, channelName, channelImportance).apply {
                description = channelDescription
            }

            val notificationManager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(NOTIFICATION_ID, notification.build())
        }
    }

    companion object {
        const val CHANNEL_ID = "channel_id"
        const val NOTIFICATION_ID = 1
    }
}
