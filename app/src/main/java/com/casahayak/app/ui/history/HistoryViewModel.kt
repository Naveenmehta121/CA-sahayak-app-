package com.casahayak.app.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casahayak.app.data.model.ResponseRecord
import com.casahayak.app.data.repository.AuthRepository
import com.casahayak.app.data.repository.ResponseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val isLoading: Boolean = true,
    val responses: List<ResponseRecord> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val responseRepository: ResponseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState(isLoading = true)
            val uid = authRepository.currentUser?.uid ?: return@launch
            responseRepository.getResponsesForUser(uid).fold(
                onSuccess = { _uiState.value = HistoryUiState(responses = it) },
                onFailure = { _uiState.value = HistoryUiState(errorMessage = it.message) }
            )
        }
    }

    fun deleteResponse(id: String) {
        viewModelScope.launch {
            responseRepository.deleteResponse(id)
            loadHistory()
        }
    }
}
