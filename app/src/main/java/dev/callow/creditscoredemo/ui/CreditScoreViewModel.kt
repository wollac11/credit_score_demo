package dev.callow.creditscoredemo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.callow.creditscoredemo.data.repository.CreditReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreditScoreViewModel @Inject constructor(
    private val repository: CreditReportRepository
) : ViewModel() {

    private val _creditReport = MutableStateFlow<CreditReportUiState>(CreditReportUiState.Loading)
    val creditReport = _creditReport.asStateFlow()

    internal fun fetchCreditReport() {
        viewModelScope.launch {
            _creditReport.value = CreditReportUiState.Loading // always set back to loading state
            try {
                val report = repository.getCreditReport()
                _creditReport.value = CreditReportUiState.Success(report)
            } catch (e: Exception) {
                _creditReport.value =
                    CreditReportUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}

sealed interface CreditReportUiState {
    data object Loading : CreditReportUiState
    data class Success(val report: dev.callow.creditscoredemo.data.model.CreditReportResponse) :
        CreditReportUiState

    data class Error(val message: String) : CreditReportUiState
}
