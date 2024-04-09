package com.example.weather.screen.map

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import com.example.weather.R
import com.example.weather.databinding.FragmentMapBinding
import com.example.weather.utils.Constant
import com.example.weather.utils.base.BaseFragment
import com.example.weather.utils.goBackFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment private constructor() : BaseFragment<FragmentMapBinding>(), OnMapReadyCallback {

    private var mGoogleMap: GoogleMap? = null
    private var mCurrentLatitude: Double = 0.0
    private var mCurrentLongitude: Double = 0.0

    override fun inflateViewBinding(inflater: LayoutInflater): FragmentMapBinding {
        return FragmentMapBinding.inflate(inflater)
    }

    override fun initData() {
        arguments?.let {
            mCurrentLatitude = it.getDouble(Constant.LATITUDE_KEY)
            mCurrentLongitude = it.getDouble(Constant.LONGITUDE_KEY)
        }
    }

    override fun initView() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewBinding.currentLocationButton.setOnClickListener {
            mGoogleMap?.clear()
            zoomOnMap(LatLng(mCurrentLatitude, mCurrentLongitude))
            addMarker(LatLng(mCurrentLatitude, mCurrentLongitude))
        }
        viewBinding.favoriteLocationButton.setOnClickListener {
            // TODO implement later
        }
        viewBinding.icBack.setOnClickListener {
            goBackFragment()
        }
        viewBinding.layoutWeatherMap.cardViewMap.setOnClickListener {
            // TODO implement later
        }
        viewBinding.layoutWeatherMap.icStar.setOnClickListener {
            // TODO implement later
        }
    }

    override fun checkNetwork(activity: Activity?) {
        // TODO implement later
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID

        mGoogleMap?.setOnMapLongClickListener { position ->
            mGoogleMap?.clear()
            addMarker(position)
        }
        mGoogleMap?.setOnMarkerClickListener { marker ->
            marker.remove()
            false
        }

        zoomOnMap(LatLng(mCurrentLatitude, mCurrentLongitude))
        addMarker(LatLng(mCurrentLatitude, mCurrentLongitude))
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
