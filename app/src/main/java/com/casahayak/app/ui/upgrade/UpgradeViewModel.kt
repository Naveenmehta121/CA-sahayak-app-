package com.casahayak.app.ui.upgrade

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.casahayak.app.data.repository.AuthRepository
import com.casahayak.app.data.repository.BillingRepository
import com.casahayak.app.data.repository.UserRepository
import com.casahayak.app.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UpgradeUiState(
    val isLoading: Boolean = false,
    val isPremium: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class UpgradeViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpgradeUiState())
    val uiState: StateFlow<UpgradeUiState> = _uiState

    init {
        viewModelScope.launch {
            billingRepository.isPremium.collect { isPremium ->
                _uiState.value = _uiState.value.copy(isPremium = isPremium)
                if (isPremium) {
                    // Update Firestore when Play Billing confirms premium
                    authRepository.currentUser?.uid?.let { uid ->
                        userRepository.updateSubscription(uid, Constants.PLAN_PREMIUM)
                    }
                }
            }
        }
    }

    fun launchPurchase(activity: Activity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val uid = authRepository.currentUser?.uid
            if (uid != null) {
                billingRepository.initialize(uid)
            }
            val result = billingRepository.launchPurchaseFlow(activity)
            _uiState.value = _uiState.value.copy(isLoading = false)
            if (result.responseCode != BillingClient.BillingResponseCode.OK &&
                result.responseCode != BillingClient.BillingResponseCode.USER_CANCELED
            ) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Purchase failed: ${result.debugMessage}"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
