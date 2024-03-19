package com.example.android_template.data.repository.dataSource.remote

import com.google.gson.GsonBuilder
import com.example.android_template.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var retrofit: Retrofit? = null
    private val gson = GsonBuilder().setLenient().create()

    val client: Retrofit
        get() {
            if (retrofit == null) {
                synchronized(Retrofit::class.java) {    // đảm bảo rằng chỉ có một luồng có thể truy cập vào phần code bên trong cùng một lúc.
                    if (retrofit == null) {             // để đảm bảo rằng khi một luồng đã tạo ra đối tượng Retrofit, các luồng khác sẽ không tạo ra một đối tượng Retrofit mới.

                        val httpClient = OkHttpClient.Builder()
                            .addInterceptor(QueryParameterAddInterceptor())

                        val client = httpClient.build()

                        retrofit = Retrofit.Builder()
                            .baseUrl(BuildConfig.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .client(client)
                            .build()
                    }
                }

            }
            return retrofit!!
        }
}