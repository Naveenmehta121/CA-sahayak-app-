package com.casahayak.app.ui.splash

import androidx.lifecycle.ViewModel
import com.casahayak.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for SplashScreen.
 * Exposes [authRepository] so the screen can check auth state.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    val authRepository: AuthRepository
) : ViewModel() {

    val isLoggedIn: Boolean
        get() = authRepository.isLoggedIn
}
