package com.casahayak.app.data.repository

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.casahayak.app.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Wraps Google Play Billing API for subscription management.
 *
 * Architecture:
 * - Connects to Play Billing on init
 * - Exposes [isPremium] StateFlow for UI to observe
 * - [launchPurchaseFlow] initiates the subscription purchase
 * - [checkPremiumStatus] queries active subscriptions on app start
 */
@Singleton
class BillingRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository
) {
    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium

    private var billingClient: BillingClient? = null

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    /**
     * Initialize and connect the billing client.
     * Call from Application class or ViewModel on app start.
     */
    fun initialize(userId: String) {
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    checkPremiumStatus(userId)
                }
            }

            override fun onBillingServiceDisconnected() {
                // Retry on next operation
            }
        })
    }

    /**
     * Checks if the user has an active premium subscription.
     */
    fun checkPremiumStatus(userId: String) {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { _, purchases ->
            val hasPremium = purchases.any { purchase ->
                purchase.products.contains(Constants.PREMIUM_SUBSCRIPTION_ID) &&
                        purchase.purchaseState == Purchase.PurchaseState.PURCHASED
            }
            _isPremium.value = hasPremium
        }
    }

    /**
     * Launches the Play Store subscription purchase UI.
     * [activity] must be the currently visible Activity.
     */
    suspend fun launchPurchaseFlow(activity: Activity): BillingResult {
        return suspendCancellableCoroutine { continuation ->
            val client = billingClient ?: run {
                continuation.resume(
                    BillingResult.newBuilder()
                        .setResponseCode(BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE)
                        .build()
                )
                return@suspendCancellableCoroutine
            }

            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(Constants.PREMIUM_SUBSCRIPTION_ID)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )

            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            client.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK ||
                    productDetailsList.isEmpty()
                ) {
                    continuation.resume(billingResult)
                    return@queryProductDetailsAsync
                }

                val productDetails = productDetailsList.first()
                val offerToken = productDetails.subscriptionOfferDetails
                    ?.firstOrNull()?.offerToken ?: run {
                    continuation.resume(
                        BillingResult.newBuilder()
                            .setResponseCode(BillingClient.BillingResponseCode.ITEM_UNAVAILABLE)
                            .build()
                    )
                    return@queryProductDetailsAsync
                }

                val productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .setOfferToken(offerToken)
                        .build()
                )

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()

                val result = client.launchBillingFlow(activity, billingFlowParams)
                continuation.resume(result)
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            _isPremium.value = true
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { }
            }
        }
    }
}
