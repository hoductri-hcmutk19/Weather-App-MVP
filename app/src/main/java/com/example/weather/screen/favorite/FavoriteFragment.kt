package com.example.weather.screen.favorite

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import com.example.weather.data.model.Weather
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.data.repository.source.local.WeatherLocalDataSource
import com.example.weather.data.repository.source.remote.WeatherRemoteDataSource
import com.example.weather.databinding.FragmentFavoriteBinding
import com.example.weather.screen.adapter.FavoriteAdapter
import com.example.weather.utils.base.BaseFragment
import com.example.weather.utils.goBackFragment
import com.example.weather.utils.listener.OnItemClickListener
import java.io.IOException

class FavoriteFragment private constructor() :
    BaseFragment<FragmentFavoriteBinding>(),
    FavoriteContract.View,
    OnItemClickListener {

    private var mPresenter: FavoritePresenter? = null
    private var mWeatherList: List<Weather>? = null
    private val favoriteAdapter: FavoriteAdapter by lazy {
        FavoriteAdapter(this)
    }

    override fun inflateViewBinding(inflater: LayoutInflater): FragmentFavoriteBinding {
        return FragmentFavoriteBinding.inflate(inflater)
    }

    override fun initData() {
        val repository = context?.let { context ->
            WeatherRepository.getInstance(
                WeatherLocalDataSource.getInstance(context),
                WeatherRemoteDataSource.getInstance()
            )
        }
        mPresenter = repository?.let { FavoritePresenter(it) }
        mPresenter?.setView(this)
        mPresenter?.getAllFavorite()
    }

    override fun initView() {
        viewBinding.recyclerViewFavorite.adapter = favoriteAdapter
        viewBinding.icBack.setOnClickListener {
            goBackFragment()
        }
    }

    override fun checkNetwork(activity: Activity?) {
        // TODO implement later
    }

    override fun onGetWeatherListSuccess(weatherList: List<Weather>) {
        mWeatherList = weatherList
        favoriteAdapter.updateData(mWeatherList)
    }

    override fun onItemClickListener(view: View, position: Int) {
        val id = mWeatherList?.get(position)?.getId()
        try {
            if (id != null) {
                mPresenter?.removeFavoriteWeather(id)
            }
        } catch (e: IOException) {
            println(e)
        } finally {
            mPresenter?.getAllFavorite()
        }
    }

    companion object {
        fun newInstance() =
            FavoriteFragment().apply {
                arguments = bundleOf()
            }
    }
}
