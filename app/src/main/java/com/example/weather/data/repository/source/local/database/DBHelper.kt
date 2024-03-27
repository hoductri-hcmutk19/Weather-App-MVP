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
import com.example.weather.utils.ext.DBUtils
import org.json.JSONObject

class DBHelper(
    context: Context?
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION), IDBHelper {

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

    override fun insertWeather(current: Weather, hourly: Weather, daily: Weather) {
        val idWeather =
            (current.city + current.country)
        val cursorWeather = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_WEATHER WHERE $COLUMN_ID_WEATHER = '$idWeather'",
            null
        )
        cursorWeather.apply {
            if (moveToFirst() && count > 0) {
                deleteWeather(idWeather)
            }
        }
        ContentValues().apply {
            put(COLUMN_ID_WEATHER, idWeather)
            put(WeatherEntry.LATITUDE, current.latitude)
            put(WeatherEntry.LONGITUDE, current.longitude)
            put(WeatherEntry.TIME_ZONE, current.timeZone)
            put(WeatherEntry.CITY, current.city)
            put(WeatherEntry.COUNTRY, current.country)
            writableDatabase.insert(TABLE_WEATHER, null, this)
        }
        insertBasic(current.weatherCurrent, idWeather, TABLE_CURRENT)
        hourly.weatherHourlyList?.let { listData ->
            listData.forEach { element ->
                insertBasic(element, idWeather, TABLE_HOURLY)
            }
        }
        daily.weatherDailyList?.let { listData ->
            listData.forEach { element ->
                insertBasic(element, idWeather, TABLE_DAILY)
            }
        }
        cursorWeather.close()
    }

    private fun insertBasic(
        weatherBasic: WeatherBasic?,
        idWeather: String,
        tableName: String
    ) {
        weatherBasic?.apply {
            ContentValues().apply {
                put(
                    COLUMN_VALUE,
                    DBUtils.createJsonObject(
                        dateTime,
                        temperature,
                        weatherMainCondition,
                        weatherDescription,
                        humidity,
                        windSpeed
                    )
                )
                put(COLUMN_ID_WEATHER, idWeather)
                writableDatabase.insert(tableName, null, this)
            }
        }
    }

    @SuppressLint("Range")
    override fun getAllData(): List<Weather> {
        val weatherList = mutableListOf<Weather>()
        val weatherCursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_WEATHER",
            null
        )
        weatherCursor.apply {
            moveToFirst()
            while (!isAfterLast) {
                val idWeather = weatherCursor.getString(getColumnIndex(COLUMN_ID_WEATHER))
                weatherList.add(
                    Weather(
                        getDouble(getColumnIndex(WeatherEntry.LATITUDE)),
                        getDouble(getColumnIndex(WeatherEntry.LONGITUDE)),
                        getInt(getColumnIndex(WeatherEntry.TIME_ZONE)),
                        getString(getColumnIndex(WeatherEntry.CITY)),
                        getString(getColumnIndex(WeatherEntry.COUNTRY)),
                        getWeatherElement(TABLE_CURRENT, idWeather) as WeatherBasic,
                        getWeatherElement(TABLE_HOURLY, idWeather) as List<WeatherBasic>,
                        getWeatherElement(TABLE_DAILY, idWeather) as List<WeatherBasic>
                    )
                )
                moveToNext()
            }
        }
        weatherCursor.close()
        return weatherList
    }

    @SuppressLint("Range")
    override fun getAllOverall(): List<Weather> {
        val weatherOverallList = mutableListOf<Weather>()
        val weatherCursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_WEATHER",
            null
        )
        weatherCursor.apply {
            moveToFirst()
            while (!isAfterLast) {
                weatherOverallList.add(
                    Weather(
                        getDouble(getColumnIndex(WeatherEntry.LATITUDE)),
                        getDouble(getColumnIndex(WeatherEntry.LONGITUDE)),
                        null,
                        getString(getColumnIndex(WeatherEntry.CITY)),
                        getString(getColumnIndex(WeatherEntry.COUNTRY)),
                        null,
                        null,
                        null
                    )
                )
                moveToNext()
            }
        }
        weatherCursor.close()
        return weatherOverallList
    }

    @SuppressLint("Range")
    override fun getWeather(idWeather: String): Weather? {
        var weather: Weather? = null
        val weatherCursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_WEATHER WHERE $COLUMN_ID_WEATHER = ?",
            arrayOf(idWeather)
        )
        weatherCursor.apply {
            moveToFirst()
            while (!isAfterLast) {
                weather = Weather(
                    getDouble(getColumnIndex(WeatherEntry.LATITUDE)),
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
            null
        )
        return when (tableName) {
            TABLE_CURRENT -> {
                var basicWeather: WeatherBasic? = null
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val value = JSONObject(cursor.getString(cursor.getColumnIndex(COLUMN_VALUE)))
                    basicWeather = DBUtils.parseJsonToBasicWeather(
                        value,
                        WeatherEntry.CURRENTLY_OBJECT
                    )
                }
                cursor.close()
                basicWeather
            }

            TABLE_HOURLY -> {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val value = JSONObject(cursor.getString(cursor.getColumnIndex(COLUMN_VALUE)))
                    listData.add(
                        DBUtils.parseJsonToBasicWeather(
                            value,
                            WeatherEntry.HOURLY_OBJECT
                        )
                    )
                    cursor.moveToNext()
                }
                cursor.close()
                listData
            }

            else -> {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val value = JSONObject(cursor.getString(cursor.getColumnIndex(COLUMN_VALUE)))
                    listData.add(
                        DBUtils.parseJsonToBasicWeather(
                            value,
                            WeatherEntry.DAILY_OBJECT
                        )
                    )
                    cursor.moveToNext()
                }
                cursor.close()
                listData
            }
        }
    }

    override fun deleteWeather(idWeather: String) {
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
                    tableName,
                    "$COLUMN_ID_WEATHER = '$idWeather'",
                    null
                )
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
