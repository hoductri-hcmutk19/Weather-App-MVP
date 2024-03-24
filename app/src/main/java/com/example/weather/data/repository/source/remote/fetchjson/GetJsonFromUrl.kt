package com.example.weather.data.repository.source.remote.fetchjson

import android.os.AsyncTask
import com.example.weather.data.model.Weather
import com.example.weather.screen.RequestCompleteListener

class GetJsonFromUrl(private val listener: RequestCompleteListener<Weather>) :
    AsyncTask<MutableList<String>, Void, MutableList<String>>() {

    private var exception: Exception? = null

    override fun doInBackground(vararg strings: MutableList<String>?): MutableList<String> {
        var data: MutableList<String> = mutableListOf()
        try {
            val parseDataWithJson = ParseDataWithJson()
            data = parseDataWithJson.getJsonFromUrl(strings[0])
        } catch (e: Exception) {
            exception = e
        }
        return data
    }

    override fun onPostExecute(data: MutableList<String>) {
        super.onPostExecute(data)
        data.let { ParseDataWithJson().parseJsonToWeather(data)
            ?.let { it1 -> listener.onRequestSuccess(it1) } }
            ?: exception?.let(listener::onRequestFailed)
    }
}
