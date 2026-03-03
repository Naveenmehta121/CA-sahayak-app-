package com.casahayak.app.ui.generator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casahayak.app.data.model.ResponseRecord
import com.casahayak.app.data.repository.AuthRepository
import com.casahayak.app.data.repository.GroqRepository
import com.casahayak.app.data.repository.ResponseRepository
import com.casahayak.app.data.repository.UsageRepository
import com.casahayak.app.data.repository.UserRepository
import com.casahayak.app.data.prompt.PromptTemplates
import com.casahayak.app.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GeneratorUiState(
    val isLoading: Boolean = false,
    val generatedText: String? = null,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,
    val isLimitReached: Boolean = false,
    val subscriptionType: String = Constants.PLAN_FREE,
    val remainingUses: Int? = null
)

/**
 * ViewModel for GeneratorScreen.
 *
 * Checks usage limits before calling the Groq API.
 * Saves the result to Firestore on user request.
 */
@HiltViewModel
class GeneratorViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val groqRepository: GroqRepository,
    private val usageRepository: UsageRepository,
    private val responseRepository: ResponseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GeneratorUiState())
    val uiState: StateFlow<GeneratorUiState> = _uiState

    /**
     * Loads subscription type and remaining uses for the given feature.
     */
    fun loadFeatureInfo(featureType: String) {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid ?: return@launch
            val email = authRepository.currentUser?.email ?: ""
            val userResult = userRepository.getUser(uid, email)
            userResult.onSuccess { user ->
                val remaining = usageRepository.getRemainingUses(uid, featureType, user.subscriptionType)
                _uiState.value = _uiState.value.copy(
                    subscriptionType = user.subscriptionType,
                    remainingUses = remaining
                )
            }
        }
    }

    /**
     * Generates AI text for the given feature + user input.
     *
     * Steps:
     * 1. Check subscription + usage limits
     * 2. Call Groq API
     * 3. Increment usage counter
     */
    fun generate(featureType: String, userInput: String) {
        if (userInput.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please enter some text before generating"
            )
            return
        }

        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid ?: return@launch
            val email = authRepository.currentUser?.email ?: ""

            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Get current subscription
            val userResult = userRepository.getUser(uid, email)
            val subscriptionType = userResult.getOrNull()?.subscriptionType ?: Constants.PLAN_FREE

            // Check if allowed to use this feature
            val canUse = usageRepository.canUseFeature(uid, featureType, subscriptionType)
            if (!canUse) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLimitReached = true,
                    errorMessage = "Monthly limit reached. Upgrade to Premium for unlimited access."
                )
                return@launch
            }

            // Call Groq API
            val result = groqRepository.generate(featureType, userInput)
            result.fold(
                onSuccess = { generatedText ->
                    // Increment usage counter
                    usageRepository.incrementUsage(uid, featureType)
                    val newRemaining = usageRepository.getRemainingUses(uid, featureType, subscriptionType)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generatedText = generatedText,
                        isSaved = false,
                        remainingUses = newRemaining,
                        subscriptionType = subscriptionType
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = when {
                            e.message?.contains("timeout", ignoreCase = true) == true ->
                                "Request timed out. Please try again."
                            e.message?.contains("401") == true ->
                                "API key invalid. Please check your Groq API key in local.properties."
                            e.message?.contains("rate limit", ignoreCase = true) == true ->
                                "Rate limit reached. Please wait a moment and try again."
                            e.message?.contains("network", ignoreCase = true) == true ->
                                "Network error. Please check your internet connection."
                            else -> "Generation failed: ${e.message}"
                        }
                    )
                }
            )
        }
    }

    /**
     * Saves the current generated response to Firestore history.
     */
    fun saveResponse(featureType: String, inputText: String) {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid ?: return@launch
            val generatedText = _uiState.value.generatedText ?: return@launch
            val record = ResponseRecord(
                userId = uid,
                inputText = inputText,
                outputText = generatedText,
                featureType = featureType,
                featureLabel = PromptTemplates.getFeatureLabel(featureType)
            )
            responseRepository.saveResponse(record)
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null, isLimitReached = false)
    }

    fun clearResult() {
        _uiState.value = _uiState.value.copy(generatedText = null, isSaved = false)
    }
}
