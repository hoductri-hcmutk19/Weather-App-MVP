package com.example.androidTemplate.data.model.remoteData

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class City(
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("name")
        val name: String = "",
        @SerializedName("country")
        val country: String = ""
): Serializable {
        companion object {
                const val serialVersionUID = 1L
        }
}



