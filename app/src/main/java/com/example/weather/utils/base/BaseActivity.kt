package com.example.weather.utils.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())
        onDeviceOffline()
        onLocationRequest()
    }

    abstract fun getLayoutResourceId(): Int
    abstract fun onDeviceOffline()
    abstract fun onLocationRequest()
}
