package com.example.weather.screen.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.weather.R
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.data.repository.source.local.WeatherLocalDataSource
import com.example.weather.data.repository.source.remote.WeatherRemoteDataSource
import com.example.weather.screen.MainActivity
import com.example.weather.utils.Constant
import com.example.weather.utils.ext.getIcon
import com.example.weather.utils.ext.kelvinToCelsius
import com.example.weather.utils.ext.unixTimestampToHourString

class WeatherWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        val context = context ?: return

        val intent = Intent(context, MainActivity::class.java)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val repository = context.let { context ->
            WeatherRepository.getInstance(
                WeatherLocalDataSource.getInstance(context),
                WeatherRemoteDataSource.getInstance()
            )
        }
        val weatherList = repository.getAllLocalWeathers()
        val currentWeather = weatherList.find { it.isFavorite == Constant.FALSE }
        appWidgetIds?.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.widget_weather)

            currentWeather?.weatherCurrent?.let { weatherCurrent ->
                val time = weatherCurrent.dateTime?.unixTimestampToHourString()?.toInt()
                if (time != null) {
                    weatherCurrent.weatherMainCondition?.let { mainCondition ->
                        getIcon(mainCondition, time)?.let { image ->
                            views.setImageViewResource(R.id.widget_img, image)
                        }
                    }
                }
            }
            views.setTextViewText(R.id.tv_description_widget, currentWeather?.weatherCurrent?.weatherDescription)
            views.setTextViewText(R.id.tv_location_widget, currentWeather?.getLocation())
            views.setTextViewText(
                R.id.tv_temperature_widget,
                currentWeather?.weatherCurrent?.temperature?.kelvinToCelsius().toString()
            )

            views.setOnClickPendingIntent(R.id.widget_weather, pendingIntent)

            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}
