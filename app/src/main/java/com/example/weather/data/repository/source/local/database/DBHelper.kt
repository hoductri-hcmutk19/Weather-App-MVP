package com.example.weather.data.repository.source.local.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.weather.data.model.Weather
import com.example.weather.data.model.WeatherEntry
import com.example.weather.data.model.entity.WeatherBasic
import com.example.weather.data.model.entity.WeatherBasicEntry
import org.json.JSONException
import org.json.JSONObject

class DBHelper(
    private val context: Context?
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.apply {
            execSQL(SQL_CREATE_TABLE_WEATHER)
            execSQL(SQL_CREATE_TABLE_CURRENT)
            execSQL(SQL_CREATE_TABLE_HOURLY)
            execSQL(SQL_CREATE_TABLE_DAILY)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.apply {
            execSQL(SQL_DROP_TABLE_CURRENT)
            execSQL(SQL_DROP_TABLE_HOURLY)
            execSQL(SQL_DROP_TABLE_DAILY)
            execSQL(SQL_DROP_TABLE_WEATHER)
            onCreate(this)
        }
    }

    private fun createJsonObject(vararg any: Any?): String {
        val jsonObject = JSONObject()
        try {
            jsonObject.apply {
                put(WeatherBasicEntry.DATE_TIME, any[0])
                put(WeatherBasicEntry.TEMPERATURE, any[1])
                put(WeatherBasicEntry.MAIN, any[2])
                put(WeatherBasicEntry.WEATHER_DESCRIPTION, any[3])
                put(WeatherBasicEntry.HUMIDITY, any[4])
                put(WeatherBasicEntry.WIND_SPEED, any[5])
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject.toString()
    }

    fun insertWeather(weather: Weather) {
        val idWeather =
            (weather.city + weather.country)
        val cursorWeather = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_WEATHER WHERE $COLUMN_ID_WEATHER = '$idWeather'",
            null)
        cursorWeather.apply {
            if (moveToFirst() && count > 0) {
                deleteWeather(idWeather)
            }
        }
        ContentValues().apply {
            put(COLUMN_ID_WEATHER, idWeather)
            put(WeatherEntry.LATITUDE, weather.latitude)
            put(WeatherEntry.LONGITUDE, weather.longitude)
            put(WeatherEntry.TIME_ZONE, weather.timeZone)
            put(WeatherEntry.CITY, weather.city)
            put(WeatherEntry.COUNTRY, weather.country)
            writableDatabase.insert(TABLE_WEATHER, null, this)
        }
        insertCurrently(weather.weatherCurrent, idWeather, TABLE_CURRENT)
        weather.weatherHourlyList?.let {
            insertData(it, idWeather, TABLE_HOURLY)
        }
        weather.weatherDailyList?.let {
            insertData(it, idWeather, TABLE_DAILY)
        }
        cursorWeather.close()
    }

    private fun insertData(
        listData: List<WeatherBasic>,
        idWeather: String,
        tableName: String
    ) {
        listData.forEach { element ->
            insertCurrently(element, idWeather, tableName)
        }
    }

    private fun insertCurrently(
        weatherBasic: WeatherBasic?,
        idWeather: String,
        tableName: String
    ) {
        weatherBasic?.apply {
            ContentValues().apply {
                put(COLUMN_VALUE, createJsonObject(dateTime, temperature, weatherMainCondition,
                    weatherDescription, humidity, windSpeed))
                put(COLUMN_ID_WEATHER, idWeather)
                writableDatabase.insert(tableName, null, this)
            }
        }
    }

    @SuppressLint("Range")
    fun getAllData(): List<Weather> {
        val weatherList = mutableListOf<Weather>()
        val weatherCursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_WEATHER",
            null
        )
        weatherCursor.apply {
            moveToFirst()
            while (!isAfterLast) {
                val idWeather = weatherCursor.getString(getColumnIndex(COLUMN_ID_WEATHER))
                weatherList.add(Weather(getDouble(getColumnIndex(WeatherEntry.LATITUDE)),
                    getDouble(getColumnIndex(WeatherEntry.LONGITUDE)),
                    getInt(getColumnIndex(WeatherEntry.TIME_ZONE)),
                    getString(getColumnIndex(WeatherEntry.CITY)),
                    getString(getColumnIndex(WeatherEntry.COUNTRY)),
                    getWeatherElement(TABLE_CURRENT, idWeather) as WeatherBasic,
                    getWeatherElement(TABLE_HOURLY, idWeather) as List<WeatherBasic>,
                    getWeatherElement(TABLE_DAILY, idWeather) as List<WeatherBasic>
                ))
                moveToNext()
            }
        }
        weatherCursor.close()
        return weatherList
    }

    @SuppressLint("Range")
    fun getAllOverall(): List<Weather> {
        val weatherOverallList = mutableListOf<Weather>()
        val weatherCursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_WEATHER",
            null
        )
        weatherCursor.apply {
            moveToFirst()
            while (!isAfterLast) {
                weatherOverallList.add(Weather(getDouble(getColumnIndex(WeatherEntry.LATITUDE)),
                    getDouble(getColumnIndex(WeatherEntry.LONGITUDE)),
                    null,
                    getString(getColumnIndex(WeatherEntry.CITY)),
                    getString(getColumnIndex(WeatherEntry.COUNTRY)),
                    null,
                    null,
                    null
                ))
                moveToNext()
            }
        }
        weatherCursor.close()
        return weatherOverallList
    }

    @SuppressLint("Range")
    fun getWeather(idWeather: String): Weather? {
        var weather: Weather? = null
        val weatherCursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_WEATHER WHERE $COLUMN_ID_WEATHER = ?",
            arrayOf(idWeather)
        )
        weatherCursor.apply {
            moveToFirst()
            while (!isAfterLast) {
                weather = Weather(getDouble(getColumnIndex(WeatherEntry.LATITUDE)),
                    getDouble(getColumnIndex(WeatherEntry.LONGITUDE)),
                    getInt(getColumnIndex(WeatherEntry.TIME_ZONE)),
                    getString(getColumnIndex(WeatherEntry.CITY)),
                    getString(getColumnIndex(WeatherEntry.COUNTRY)),
                    getWeatherElement(TABLE_CURRENT, idWeather) as WeatherBasic,
                    getWeatherElement(TABLE_HOURLY, idWeather) as List<WeatherBasic>,
                    getWeatherElement(TABLE_DAILY, idWeather) as List<WeatherBasic>
                )
                moveToNext()
            }
        }
        weatherCursor.close()
        return weather
    }

    @SuppressLint("Range")
    private fun getWeatherElement(tableName: String, idWeather: String): Any? {
        val listData = mutableListOf<WeatherBasic>()
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM $tableName WHERE $COLUMN_ID_WEATHER = '$idWeather'",
            null)
        cursor.apply {
            when(tableName) {
                TABLE_CURRENT -> {
                    moveToFirst()
                    while (!isAfterLast) {
                        val value = JSONObject(getString(getColumnIndex(COLUMN_VALUE)))
                        moveToNext()
                        return parseJsonToBasicWeather(
                            value, WeatherEntry.CURRENTLY_OBJECT
                        )
                    }
                }
                TABLE_HOURLY -> {
                    moveToFirst()
                    while (!isAfterLast) {
                        val value = JSONObject(getString(getColumnIndex(COLUMN_VALUE)))
                        listData.add(parseJsonToBasicWeather(
                            value, WeatherEntry.HOURLY_OBJECT))
                        moveToNext()
                    }
                    return listData
                }
                else -> {
                    moveToFirst()
                    while (!isAfterLast) {
                        val value = JSONObject(getString(getColumnIndex(COLUMN_VALUE)))
                        listData.add(parseJsonToBasicWeather(
                            value, WeatherEntry.DAILY_OBJECT))
                        moveToNext()
                    }
                    return listData
                }
            }
        }
        cursor.close()
        return null
    }

    private fun parseJsonToBasicWeather(jsonObject: JSONObject, tagObject: String): WeatherBasic {
        return when(tagObject) {
            WeatherEntry.HOURLY_OBJECT, WeatherEntry.DAILY_OBJECT -> {
                WeatherBasic(jsonObject.getInt(WeatherBasicEntry.DATE_TIME),
                    jsonObject.getDouble(WeatherBasicEntry.TEMPERATURE),
                    jsonObject.getString(WeatherBasicEntry.MAIN),
                    null,
                    null,
                    null)
            }
            else -> {
                WeatherBasic(jsonObject.getInt(WeatherBasicEntry.DATE_TIME),
                    jsonObject.getDouble(WeatherBasicEntry.TEMPERATURE),
                    jsonObject.getString(WeatherBasicEntry.MAIN),
                    jsonObject.getString(WeatherBasicEntry.WEATHER_DESCRIPTION),
                    jsonObject.getInt(WeatherBasicEntry.HUMIDITY),
                    jsonObject.getDouble(WeatherBasicEntry.WIND_SPEED))
            }
        }
    }

    fun deleteWeather(idWeather: String) {
        val writeDatabase = writableDatabase
        val listCurrentCursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_CURRENT WHERE $COLUMN_ID_WEATHER = '$idWeather'",
            null
        )
        val listHourlyCursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_HOURLY WHERE $COLUMN_ID_WEATHER = '$idWeather'",
            null
        )
        val listDailyCursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_DAILY WHERE $COLUMN_ID_WEATHER = '$idWeather'",
            null
        )
        deleteWeatherElement(listCurrentCursor, idWeather, TABLE_CURRENT)
        deleteWeatherElement(listHourlyCursor, idWeather, TABLE_HOURLY)
        deleteWeatherElement(listDailyCursor, idWeather, TABLE_DAILY)
        writeDatabase.delete(TABLE_WEATHER, "id_weather = '$idWeather'", null)
    }

    private fun deleteWeatherElement(cursor: Cursor, idWeather: String, tableName: String) {
        cursor.apply {
            moveToFirst()
            while (!isAfterLast) {
                writableDatabase.delete(
                    tableName, "$COLUMN_ID_WEATHER = '$idWeather'", null)
                moveToNext()
            }
        }
    }

    companion object {
        const val DATABASE_NAME = "weather.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_WEATHER = "Weather"
        private const val TABLE_CURRENT = "Currently"
        private const val TABLE_HOURLY = "Hourly"
        private const val TABLE_DAILY = "Daily"
        private const val COLUMN_ID_WEATHER = "id_weather"
        private const val COLUMN_VALUE = "value"
        // Create table
        private const val SQL_CREATE_TABLE_WEATHER = "CREATE TABLE $TABLE_WEATHER(" +
                "$COLUMN_ID_WEATHER TEXT PRIMARY KEY, " +
                "${WeatherEntry.LATITUDE} REAL, " +
                "${WeatherEntry.LONGITUDE} REAL, " +
                "${WeatherEntry.TIME_ZONE} INTEGER, " +
                "${WeatherEntry.CITY} TEXT, " +
                "${WeatherEntry.COUNTRY} TEXT)"
        private const val SQL_CREATE_TABLE_BODY =
                    "$COLUMN_VALUE TEXT, " +
                    "$COLUMN_ID_WEATHER TEXT, " +
                    "FOREIGN KEY($COLUMN_ID_WEATHER) REFERENCES $TABLE_WEATHER($COLUMN_ID_WEATHER)"
        private const val SQL_CREATE_TABLE_CURRENT =
            "CREATE TABLE $TABLE_CURRENT($SQL_CREATE_TABLE_BODY)"
        private const val SQL_CREATE_TABLE_HOURLY =
            "CREATE TABLE $TABLE_HOURLY($SQL_CREATE_TABLE_BODY)"
        private const val SQL_CREATE_TABLE_DAILY =
            "CREATE TABLE $TABLE_DAILY($SQL_CREATE_TABLE_BODY)"
        private const val SQL_DROP_TABLE_CURRENT = "DROP TABLE IF EXISTS $TABLE_CURRENT"
        private const val SQL_DROP_TABLE_HOURLY = "DROP TABLE IF EXISTS $TABLE_HOURLY"
        private const val SQL_DROP_TABLE_DAILY = "DROP TABLE IF EXISTS $TABLE_DAILY"
        private const val SQL_DROP_TABLE_WEATHER = "DROP TABLE IF EXISTS $TABLE_WEATHER"

        fun getInstance(context: Context?) = DBHelper(context)
    }
}