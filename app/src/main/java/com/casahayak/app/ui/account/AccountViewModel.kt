package com.casahayak.app.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casahayak.app.data.model.User
import com.casahayak.app.data.repository.AuthRepository
import com.casahayak.app.data.repository.UserRepository
import com.casahayak.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val trialDaysRemaining: Int = 0
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val firebaseUser = authRepository.currentUser ?: return@launch
            userRepository.getUser(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName ?: ""
            ).onSuccess { user ->
                _uiState.value = AccountUiState(
                    isLoading = false,
                    user = user,
                    trialDaysRemaining = DateUtils.trialDaysRemaining(user.trialEndDate)
                )
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}
