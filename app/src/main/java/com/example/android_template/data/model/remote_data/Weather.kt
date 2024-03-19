package com.example.android_template.data.model.remote_data

import com.google.gson.annotations.SerializedName

data class Weather(
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("main")
        val main: String = "",
        @SerializedName("description")
        val description: String = "",
        @SerializedName("icon")
        val icon: String = ""
)