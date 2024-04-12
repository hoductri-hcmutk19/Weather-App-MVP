package com.example.weather

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.example.weather.data.model.Weather
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.data.repository.source.local.WeatherLocalDataSource
import com.example.weather.data.repository.source.remote.WeatherRemoteDataSource
import com.example.weather.screen.MainActivity
import com.example.weather.utils.Constant
import com.example.weather.utils.ext.unixTimestampToDateTimeString

class WeatherWidgetProvider : AppWidgetProvider() {

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)

        Log.v("myTag", "onEnabled")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        Log.v("myTag", "onUpdate")
        val context = context ?: return

        val intent = Intent(context, MainActivity::class.java)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val repository = context?.let { context ->
            WeatherRepository.getInstance(
                WeatherLocalDataSource.getInstance(context),
                WeatherRemoteDataSource.getInstance()
            )
        }
        val weatherList = repository?.getAllLocalWeathers()
        val currentWeather = weatherList?.find { it.isFavorite == Constant.FALSE }
        appWidgetIds?.forEach { appWidgetId ->
            val views = RemoteViews(context?.packageName, R.layout.widget_weather)

            views.setTextViewText(R.id.tx, currentWeather?.weatherCurrent?.dateTime?.unixTimestampToDateTimeString())
            views.setImageViewResource(R.id.widget_img, R.drawable.ic_clear_night)

            views.setOnClickPendingIntent(R.id.widget_weather, pendingIntent)


            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)

    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        Log.v("myTag", "onDeleted")
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        Log.v("myTag", "onDisabled")
    }

}
