package com.casahayak.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casahayak.app.data.model.User
import com.casahayak.app.data.repository.AuthRepository
import com.casahayak.app.data.repository.UserRepository
import com.casahayak.app.data.repository.UsageRepository
import com.casahayak.app.util.Constants
import com.casahayak.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val trialDaysRemaining: Int = 0,
    val usageCounts: Map<String, Int> = emptyMap(),
    val errorMessage: String? = null
)

/**
 * ViewModel for DashboardScreen.
 * Loads user info and current month usage counts.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val usageRepository: UsageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val uid = authRepository.currentUser?.uid ?: return@launch
            val email = authRepository.currentUser?.email ?: ""
            val displayName = authRepository.currentUser?.displayName ?: ""
            val userResult = userRepository.getUser(uid, email, displayName)
            userResult.fold(
                onSuccess = { user ->
                    // Load usage counts for all features
                    val usageCounts = mutableMapOf<String, Int>()
                    for (feature in Constants.ALL_FEATURES) {
                        usageCounts[feature] = usageRepository.getUsageCount(uid, feature)
                    }
                    _uiState.value = DashboardUiState(
                        isLoading = false,
                        user = user,
                        trialDaysRemaining = DateUtils.trialDaysRemaining(user.trialEndDate),
                        usageCounts = usageCounts
                    )
                },
                onFailure = { e ->
                    _uiState.value = DashboardUiState(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            )
        }
    }
}
