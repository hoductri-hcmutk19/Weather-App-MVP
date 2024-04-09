package com.example.weather.screen.map

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.weather.R
import com.example.weather.data.model.Weather
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.data.repository.source.local.WeatherLocalDataSource
import com.example.weather.data.repository.source.remote.WeatherRemoteDataSource
import com.example.weather.databinding.FragmentMapBinding
import com.example.weather.screen.detail.DetailFragment
import com.example.weather.utils.Constant
import com.example.weather.utils.PermissionUtils
import com.example.weather.utils.base.BaseFragment
import com.example.weather.utils.ext.getIcon
import com.example.weather.utils.ext.kelvinToCelsius
import com.example.weather.utils.ext.unixTimestampToHourString
import com.example.weather.utils.goBackFragment
import com.example.weather.utils.replaceFragmentToActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.lang.Exception

@Suppress("TooManyFunctions")
class MapFragment private constructor() : BaseFragment<FragmentMapBinding>(), MapContract.View, OnMapReadyCallback {

    private var mPresenter: MapPresenter? = null
    private var mGoogleMap: GoogleMap? = null
    private var mCurrentLatitude: Double = 0.0
    private var mCurrentLongitude: Double = 0.0
    private var mSelectedLatitude: Double = 0.0
    private var mSelectedLongitude: Double = 0.0
    private var mWeather: Weather? = null
    private var mIsNetworkEnable: Boolean = false

    override fun inflateViewBinding(inflater: LayoutInflater): FragmentMapBinding {
        return FragmentMapBinding.inflate(inflater)
    }

    override fun initData() {
        val repository = context?.let { context ->
            WeatherRepository.getInstance(
                WeatherLocalDataSource.getInstance(context),
                WeatherRemoteDataSource.getInstance()
            )
        }
        mPresenter = repository?.let { MapPresenter(it) }
        mPresenter?.setView(this)
        arguments?.let {
            mCurrentLatitude = it.getDouble(Constant.LATITUDE_KEY)
            mCurrentLongitude = it.getDouble(Constant.LONGITUDE_KEY)
            if (mSelectedLatitude == 0.0 && mSelectedLongitude == 0.0) {
                mPresenter?.getWeather(mCurrentLatitude, mCurrentLongitude)
            } else {
                mPresenter?.getWeather(mSelectedLatitude, mSelectedLongitude)
            }
        }
    }

    override fun initView() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewBinding.currentLocationButton.setOnClickListener {
            mPresenter?.getWeather(mCurrentLatitude, mCurrentLongitude)
            moveToLocation(mCurrentLatitude, mCurrentLongitude)
        }

        viewBinding.mapSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = viewBinding.mapSearch.query.toString()
                var addressList: MutableList<Address>? = null
                val geocoder = context?.let { Geocoder(it) }
                try {
                    addressList = geocoder?.getFromLocationName(location, 1)
                } catch (e: IOException) {
                    println(e)
                }
                if (addressList != null) {
                    if (addressList.isNotEmpty()) {
                        mSelectedLatitude = addressList[0].latitude
                        mSelectedLongitude = addressList[0].longitude
                        mPresenter?.getWeather(mSelectedLatitude, mSelectedLongitude)
                        moveToLocation(mSelectedLatitude, mSelectedLongitude)
                    } else {
                        Toast.makeText(
                            context,
                            getString(R.string.location_not_exist),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        viewBinding.favoriteLocationButton.setOnClickListener {
            // TODO implement later
        }
        viewBinding.icBack.setOnClickListener {
            goBackFragment()
        }

        viewBinding.layoutWeatherMap.cardViewMap.setOnClickListener {
            activity?.let {
                mWeather?.let { weather -> DetailFragment.newInstance(weather) }?.let { detailFragment ->
                    (it as AppCompatActivity).replaceFragmentToActivity(
                        it.supportFragmentManager,
                        detailFragment,
                        R.id.container
                    )
                }
            }
        }

        viewBinding.layoutWeatherMap.icStar.setOnClickListener {
            viewBinding.layoutWeatherMap.icStar.isClickable = false
            if (mWeather?.isFavorite == Constant.FALSE) {
                viewBinding.layoutWeatherMap.icStar.setImageResource(R.drawable.ic_star_yellow)
                mWeather?.let { weather -> mPresenter?.favoriteWeather(weather) }
                mWeather?.isFavorite = Constant.TRUE
                viewBinding.layoutWeatherMap.icStar.isClickable = true
            } else {
                viewBinding.layoutWeatherMap.icStar.setImageResource(R.drawable.ic_star_white)
                val id = mWeather?.city + mWeather?.country
                mPresenter?.removeFavoriteWeather(id)
                mWeather?.isFavorite = Constant.FALSE
                viewBinding.layoutWeatherMap.icStar.isClickable = true
            }
        }
    }

    override fun checkNetwork(activity: Activity?) {
        if (activity?.let { PermissionUtils.isNetWorkEnabled(it) } == true) {
            mIsNetworkEnable = true
        } else {
            mIsNetworkEnable = false
            onInternetConnectionFailed()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID

        mGoogleMap?.setOnMapLongClickListener { position ->
            mGoogleMap?.clear()
            mSelectedLatitude = position.latitude
            mSelectedLongitude = position.longitude
            mPresenter?.getWeather(mSelectedLatitude, mSelectedLongitude)
            addMarker(position)
        }
        mGoogleMap?.setOnMarkerClickListener { marker ->
            marker.remove()
            false
        }

        if (mSelectedLatitude == 0.0 && mSelectedLongitude == 0.0) {
            zoomOnMap(LatLng(mCurrentLatitude, mCurrentLongitude))
            addMarker(LatLng(mCurrentLatitude, mCurrentLongitude))
        } else {
            zoomOnMap(LatLng(mSelectedLatitude, mSelectedLongitude))
            addMarker(LatLng(mSelectedLatitude, mSelectedLongitude))
        }
    }

    private fun addMarker(position: LatLng): Marker? {
        return mGoogleMap?.addMarker(
            MarkerOptions()
                .position(position)
                .title("Custom Marker")
                .draggable(true)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        Bitmap.createScaledBitmap(
                            BitmapFactory.decodeResource(resources, R.drawable.ic_marker),
                            Constant.MARKER_SIZE,
                            Constant.MARKER_SIZE,
                            false
                        )
                    )
                )
        )
    }

    private fun zoomOnMap(latLng: LatLng) {
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, Constant.ZOOM_RATIO)
        mGoogleMap?.animateCamera(newLatLngZoom)
    }

    private fun moveToLocation(latitude: Double, longitude: Double) {
        mGoogleMap?.clear()
        zoomOnMap(LatLng(latitude, longitude))
        addMarker(LatLng(latitude, longitude))
    }

    override fun onProgressLoading(isLoading: Boolean) {
        if (isLoading) {
            viewBinding.progressBar.visibility = View.VISIBLE
        } else {
            viewBinding.progressBar.visibility = View.GONE
        }
    }

    override fun onGetCurrentWeatherSuccess(weather: Weather) {
        mWeather = weather
        mWeather?.let { bindDataToView(it) }
    }

    @Suppress("NestedBlockDepth")
    private fun bindDataToView(weather: Weather) {
        weather.weatherCurrent?.let { weatherCurrent ->
            val time = weatherCurrent.dateTime?.unixTimestampToHourString()?.toInt()
            if (time != null) {
                weatherCurrent.weatherMainCondition?.let { mainCondition ->
                    getIcon(mainCondition, time)?.let { image ->
                        viewBinding.layoutWeatherMap.ivMainCondition.setImageResource(image)
                    }
                }
            }
            viewBinding.layoutWeatherMap.tvMainCondition.text = weatherCurrent.weatherMainCondition
            viewBinding.layoutWeatherMap.tvTemperature.text = weatherCurrent.temperature?.kelvinToCelsius().toString()
        }
        viewBinding.layoutWeatherMap.tvLocation.text = weather.getLocation()
        val starResource =
            if (weather.isFavorite == Constant.TRUE) R.drawable.ic_star_yellow else R.drawable.ic_star_white
        viewBinding.layoutWeatherMap.icStar.setImageResource(starResource)
    }

    override fun onInternetConnectionFailed() {
        Toast.makeText(
            context,
            getString(R.string.message_network_not_responding),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onError(exception: Exception) {
        Toast.makeText(context, exception.message.toString(), Toast.LENGTH_LONG).show()
    }

    companion object {
        fun newInstance(latitude: Double, longitude: Double) =
            MapFragment().apply {
                arguments = bundleOf(
                    Constant.LATITUDE_KEY to latitude,
                    Constant.LONGITUDE_KEY to longitude
                )
            }
    }
}
