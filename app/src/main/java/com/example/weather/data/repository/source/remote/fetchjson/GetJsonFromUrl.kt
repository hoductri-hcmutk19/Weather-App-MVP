package com.example.weather.data.repository.source.remote.fetchjson

import android.os.Handler
import android.os.Looper
import com.example.weather.data.model.Weather
import com.example.weather.screen.RequestCompleteListener
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class GetJsonFromUrl constructor(
    private val urlString: String,
    private val keyEntity: String,
    private val listener: RequestCompleteListener<Weather>
) {

    private val mExecutor: Executor = Executors.newCachedThreadPool()
    private val mHandler = Handler(Looper.getMainLooper())
    private var data: String? = null
    private var exception: Exception? = null

    init {
        callAPI()
    }

    private fun callAPI() {
        mExecutor.execute {
            data = ParseDataWithJson().getJsonFromUrl(urlString)
            mHandler.post {
                data.let {
                    ParseDataWithJson().parseJsonToWeather(data!!, keyEntity)
                        ?.let { it1 ->
                            listener.onRequestSuccess(it1)
                        }
                }
                    ?: exception?.let(listener::onRequestFailed)
            }
        }
    }
}
