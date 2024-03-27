package com.example.weather.screen

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.R
import com.example.weather.screen.home.WeatherFragment
import com.example.weather.utils.PermissionUtils
import com.example.weather.utils.addFragmentToActivity
import com.example.weather.utils.listener.OnFetchLocation

class MainActivity : AppCompatActivity(), OnFetchLocation {
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PermissionUtils.requestPermissions(this)
    }

    override fun onResume() {
        super.onResume()
        val fragment =
            supportFragmentManager.findFragmentByTag(WeatherFragment::class.java.simpleName)
        if (fragment == null) {
            Log.v("myTag", "onResume")
            PermissionUtils.getLastLocation(
                this,
                this,
                PermissionUtils.isLocationEnabled(this)
            )
        }
    }

    private fun initView(location: Location?) {
        Log.v("myTag", "initView")
        location?.let {
            addFragmentToActivity(
                supportFragmentManager,
                WeatherFragment.newInstance(it.latitude, it.longitude),
                R.id.container
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionResult(
            requestCode,
            permissions, grantResults, this
        )
    }

    override fun onRestart() {
        super.onRestart()
        initView(location)
    }

    override fun onDataLocation(location: Location?) {
        Log.v("myTag", "onDataLocation")
        this.location = location
        initView(location)
    }
}
