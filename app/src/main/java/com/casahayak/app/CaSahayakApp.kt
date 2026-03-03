package com.casahayak.app

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class — registered in AndroidManifest.xml via android:name.
 * @HiltAndroidApp triggers Hilt's code generation and sets up the DI container.
 */
@HiltAndroidApp
class CaSahayakApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize AdMob SDK once on app start.
        // Ads will only appear for free plan users as enforced in the UI.
        MobileAds.initialize(this) {}
    }
}
