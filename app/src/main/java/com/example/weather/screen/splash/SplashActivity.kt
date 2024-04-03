package com.example.weather.screen.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.screen.MainActivity
import com.example.weather.utils.Constant

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val mHandler = Handler(Looper.getMainLooper())
    private lateinit var mRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mRunnable = Runnable {
            goToMainActivity()
        }
        mHandler.postDelayed(mRunnable, Constant.SPLASH_TIME)
    }

    private fun goToMainActivity() {
        Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .also { intent ->
                startActivity(intent)
            }
    }

    override fun onDestroy() {
        mHandler.removeCallbacks(mRunnable)
        super.onDestroy()
    }
}
