package com.casahayak.app.ui.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.casahayak.app.util.Constants
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * AdMob Banner Ad composable.
 *
 * Usage: Only render this for FREE plan users.
 * The [DashboardScreen] and [GeneratorScreen] conditionally include it.
 *
 * In debug builds, ADMOB_BANNER_AD_UNIT_ID is Google's test banner ID.
 * Replace with your real Ad Unit ID before releasing to the Play Store.
 */
@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context: Context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = Constants.ADMOB_BANNER_AD_UNIT_ID
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
