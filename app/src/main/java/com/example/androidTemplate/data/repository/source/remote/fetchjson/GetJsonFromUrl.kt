package com.example.androidTemplate.data.repository.source.remote.fetchjson

import android.os.Handler
import android.os.Looper
import com.example.androidTemplate.screen.RequestCompleteListener
import com.example.androidTemplate.utils.Constant
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class GetJsonFromUrl<T> constructor(
    private val cityId: Int,
    private val urlString: String,
    private val keyEntity: String,
    private val listener: RequestCompleteListener<T>
) {

    private val mExecutor: Executor = Executors.newSingleThreadExecutor()
    private val mHandler = Handler(Looper.getMainLooper())
    private var data: T? = null

    init {
        callAPI()
    }

    private fun callAPI() {
        mExecutor.execute {
            val responseJson =
                getJsonStringFromUrl(urlString + keyEntity + "id=$cityId" + Constant.BASE_API_KEY )
            data = ParseDataWithJson().parseJsonToData(JSONObject(responseJson)) as? T
            mHandler.post {
                data?.let { listener.onRequestSuccess(it) }
            }
        }
    }

    private fun getJsonStringFromUrl(urlString: String): String {
        val url = URL(urlString)
        val httpURLConnection = url.openConnection() as? HttpURLConnection
        httpURLConnection?.run {
            connectTimeout = TIME_OUT
            readTimeout = TIME_OUT
            requestMethod = METHOD_GET
            doOutput = true
            connect()
        }

        val bufferedReader = BufferedReader(InputStreamReader(url.openStream()))
        val stringBuilder = StringBuilder()
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
        bufferedReader.close()
        httpURLConnection?.disconnect()
        return stringBuilder.toString()
    }

    companion object {
        private const val TIME_OUT = 15000
        private const val METHOD_GET = "GET"
    }
}


