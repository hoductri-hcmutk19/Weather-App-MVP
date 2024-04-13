package com.example.weather.screen

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.weather.notification.DailyWorker
import com.example.weather.R
import com.example.weather.screen.home.WeatherFragment
import com.example.weather.utils.Constant
import com.example.weather.utils.PermissionUtils
import com.example.weather.utils.PermissionUtils.checkPermissions
import com.example.weather.utils.Utils
import com.example.weather.utils.Utils.setTimeInitWorker
import com.example.weather.utils.addFragmentToActivity
import com.example.weather.utils.base.BaseActivity
import com.example.weather.utils.listener.OnFetchListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity(), OnFetchListener {
    private var mCurrentLocation: Location? = null
    private var mLastLocation: Location? = null

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()

        mFusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                mLastLocation = location
                initWeatherView(location)
            }

        PermissionUtils.getLastLocation(
            this,
            this,
            PermissionUtils.isLocationEnabled(this)
        )
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onDeviceOffline() {
        requestPermissions()
    }

    override fun onLocationRequest() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        myWorkManager()
    }

    private fun myWorkManager() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val myRequest = PeriodicWorkRequest.Builder(
            DailyWorker::class.java,
            Constant.HOURS_IN_DAY,
            TimeUnit.HOURS
        ).setConstraints(constraints)
            .setInitialDelay(setTimeInitWorker(), TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("my_id", ExistingPeriodicWorkPolicy.KEEP, myRequest)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissions() {
        if (!checkPermissions(this)) {
            PermissionUtils.requestPermissions(this)
        }
    }

    private fun initWeatherView(location: Location?) {
        location?.let { location ->
            addFragmentToActivity(
                supportFragmentManager,
                WeatherFragment.newInstance(location.latitude, location.longitude),
                R.id.container
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
        initWeatherView(mCurrentLocation)
    }

    override fun onDataLocation(location: Location?) {
        this.mCurrentLocation = location
        val distance = mCurrentLocation?.let { currentLocation ->
            mLastLocation?.let { lastLocation ->
                Utils.distanceBetweenPoints(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    lastLocation.latitude,
                    lastLocation.longitude
                )
            }
        }
        if (distance != null) {
            if (distance > Constant.MIN_DISTANCE_FIRST_TRIGGER) {
                initWeatherView(location)
            }
        } else {
            initWeatherView(location)
        }
    }
}
