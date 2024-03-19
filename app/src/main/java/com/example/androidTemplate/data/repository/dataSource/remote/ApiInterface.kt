package com.example.androidTemplate.data.repository.dataSource.remote

import com.example.androidTemplate.data.model.CurrentWeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
    fun callApiForWeatherInfo(@Query("id") cityId: Int): Call<CurrentWeatherResponse>
}

