package com.example.androidTemplate.data.repository

import android.content.Context
import com.example.androidTemplate.data.model.remoteData.City
import com.example.androidTemplate.data.model.CurrentWeatherResponse
import com.example.androidTemplate.data.repository.dataSource.remote.ApiInterface
import com.example.androidTemplate.data.repository.dataSource.remote.RetrofitClient
import com.example.androidTemplate.screen.RequestCompleteListener
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import com.google.gson.reflect.TypeToken
import java.util.ArrayList

class WeatherRepositoryImpl(private val context: Context) : WeatherRepository {

    /**
     * Fetch city list from local (assets/city_list.json)
     */
    override fun getCityList(callback: RequestCompleteListener<MutableList<City>>) {

        try {
            val stream = context.assets.open("city_list.json")

            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            val tContents  = String(buffer)

            val groupListType = object : TypeToken<ArrayList<City>>() {}.type
            val gson = GsonBuilder().create()
            val cityList: MutableList<City> = gson.fromJson(tContents, groupListType)

            callback.onRequestSuccess(cityList)     //let presenter know the city list

        } catch (e: IOException) {
            callback.onRequestFailed(e.localizedMessage!!)  //let presenter know about failure
        }

    }

    /**
     * Fetch weather information from remote server via HTTP network request.
     */
    override fun getWeatherInformation(cityId: Int, callback: RequestCompleteListener<CurrentWeatherResponse>) {

        val apiInterface: ApiInterface = RetrofitClient.client.create(ApiInterface::class.java)
        val call: Call<CurrentWeatherResponse> = apiInterface.callApiForWeatherInfo(cityId)

        call.enqueue(object : Callback<CurrentWeatherResponse> {   // bất đồng bộ

            // if retrofit network call success, this method will be triggered
            override fun onResponse(call: Call<CurrentWeatherResponse>, response: Response<CurrentWeatherResponse>) {
                if (response.body() != null)
                //let presenter know the current weather information data
                    callback.onRequestSuccess(response.body()!!)
                else
                    callback.onRequestFailed(response.message()) //let presenter know about failure
            }

            // this method will be triggered if network call failed
            override fun onFailure(call: Call<CurrentWeatherResponse>, t: Throwable) {
                callback.onRequestFailed(t.localizedMessage!!) //let presenter know about failure
            }

        })


    }

}

