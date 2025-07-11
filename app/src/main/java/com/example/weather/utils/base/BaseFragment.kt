package com.example.weather.utils.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<viewBinding : ViewBinding> : Fragment() {

    private lateinit var _viewBinding: viewBinding
    protected val viewBinding get() = _viewBinding

    abstract fun inflateViewBinding(inflater: LayoutInflater): viewBinding

    abstract fun initData()

    abstract fun initView()

    abstract fun checkNetwork(activity: Activity?)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        checkNetwork(activity)
        _viewBinding = inflateViewBinding(inflater)
        initView()
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }
}
