package com.casahayak.app.ui.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casahayak.app.data.repository.AuthRepository
import com.casahayak.app.data.repository.UserRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel shared by LoginScreen and RegisterScreen.
 * Handles email auth and Google Sign-In via Credential Manager.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    /**
     * Signs in with email and password.
     */
    fun signIn(email: String, password: String) {
        if (!validateInputs(email, password)) return
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = authRepository.signInWithEmail(email.trim(), password)
            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState(isSuccess = true)
                },
                onFailure = { e ->
                    _uiState.value = AuthUiState(errorMessage = friendlyError(e.message))
                }
            )
        }
    }

    /**
     * Registers a new user with email and password, then creates their Firestore profile.
     */
    fun register(name: String, email: String, password: String) {
        if (name.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Please enter your name")
            return
        }
        if (!validateInputs(email, password)) return

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = authRepository.signUpWithEmail(email.trim(), password)
            result.fold(
                onSuccess = { user ->
                    // Create Firestore user document with trial
                    userRepository.createUser(user.uid, user.email ?: email.trim(), name.trim())
                    _uiState.value = AuthUiState(isSuccess = true)
                },
                onFailure = { e ->
                    _uiState.value = AuthUiState(errorMessage = friendlyError(e.message))
                }
            )
        }
    }

    /**
     * Launches Google Sign-In using Credential Manager.
     * Pass your Web Client ID from Firebase → Authentication → Google → Web SDK config.
     */
    fun signInWithGoogle(context: Context, webClientId: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                val credentialResponse = credentialManager.getCredential(context, request)
                val googleCredential = GoogleIdTokenCredential.createFrom(credentialResponse.credential.data)
                val result = authRepository.signInWithGoogle(googleCredential.idToken)
                result.fold(
                    onSuccess = { user ->
                        userRepository.getUser(user.uid, user.email ?: "", user.displayName ?: "")
                        _uiState.value = AuthUiState(isSuccess = true)
                    },
                    onFailure = { e ->
                        _uiState.value = AuthUiState(errorMessage = friendlyError(e.message))
                    }
                )
            } catch (e: GetCredentialException) {
                _uiState.value = AuthUiState(errorMessage = "Google Sign-In failed. Try email login.")
            } catch (e: Exception) {
                _uiState.value = AuthUiState(errorMessage = friendlyError(e.message))
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Please enter your email")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = AuthUiState(errorMessage = "Please enter a valid email address")
            return false
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState(errorMessage = "Password must be at least 6 characters")
            return false
        }
        return true
    }

    private fun friendlyError(message: String?): String {
        return when {
            message == null -> "Something went wrong. Please try again."
            message.contains("email address is already in use") -> "This email is already registered. Try logging in."
            message.contains("password is invalid") || message.contains("wrong-password") -> "Incorrect password. Please try again."
            message.contains("no user record") || message.contains("user-not-found") -> "No account found with this email."
            message.contains("network") -> "Network error. Please check your connection."
            else -> message
        }
    }
}
