package com.example.androidTemplate.data.repository.source.remote.fetchjson

import android.util.Log
import com.example.androidTemplate.data.model.CurrentWeather
import com.example.androidTemplate.utils.notNull
import org.json.JSONException
import org.json.JSONObject

class ParseDataWithJson {
    fun parseJsonToData(jsonObject: JSONObject?): Any {
        var data : Any = CurrentWeather()
        try {
            val item = parseJsonToObject(jsonObject)
            item.notNull {
                data = it
            }
        } catch (e: JSONException) {
            Log.e("ParseDataWithJson", "parseJsonToData: ", e)
        }
        return data
    }

    private fun parseJsonToObject(jsonObject: JSONObject?): Any? {
        try {
            jsonObject?.notNull {
                return ParseJson().weatherParseJson(it)
            }
        } catch (e: JSONException) {
            Log.e("ParseDataWithJson", "parseJsonToData: ", e)
        }
        return null
    }
}


