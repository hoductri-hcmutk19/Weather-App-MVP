package com.example.weather.screen

import android.location.Location
import com.example.weather.R
import com.example.weather.screen.home.WeatherFragment
import com.example.weather.utils.PermissionUtils
import com.example.weather.utils.PermissionUtils.checkPermissions
import com.example.weather.utils.addFragmentToActivity
import com.example.weather.utils.base.BaseActivity
import com.example.weather.utils.listener.OnFetchListener

class MainActivity : BaseActivity(), OnFetchListener {
    private var mLocation: Location? = null

    override fun onResume() {
        super.onResume()
        val fragment =
            supportFragmentManager.findFragmentByTag(WeatherFragment::class.java.simpleName)
        if (fragment == null) {
            PermissionUtils.getLastLocation(
                this,
                this,
                PermissionUtils.isLocationEnabled(this)
            )
        } else {
            initView(mLocation)
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main
    }

    override fun onDeviceOffline() {
        requestPermissions()
    }

    private fun requestPermissions() {
        if (!checkPermissions(this)) {
            PermissionUtils.requestPermissions(this)
        }
    }

    private fun initView(location: Location?) {
        location?.let { location ->
            addFragmentToActivity(
                supportFragmentManager,
                WeatherFragment.newInstance(location.latitude, location.longitude),
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
            grantResults,
            this
        )
    }

    override fun onRestart() {
        super.onRestart()
        initView(mLocation)
    }

    override fun onDataLocation(location: Location?) {
        this.mLocation = location
        initView(location)
    }
}
