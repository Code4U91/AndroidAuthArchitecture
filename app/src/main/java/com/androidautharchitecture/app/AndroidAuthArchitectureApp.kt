package com.androidautharchitecture.app

import android.app.Application
import com.androidautharchitecture.BuildConfig
import com.razorpay.Checkout
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AndroidAuthArchitectureApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        // Preload Razorpay Checkout resources for faster checkout load time
        Checkout.preload(applicationContext)
    }
}
