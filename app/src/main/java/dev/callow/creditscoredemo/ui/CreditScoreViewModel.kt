package dev.callow.creditscoredemo.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.callow.creditscoredemo.data.repository.CreditReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CreditScoreViewModel @Inject constructor(
    private val repository: CreditReportRepository
) : ViewModel() {

    private val _creditReport = MutableStateFlow<CreditReportUiState>(CreditReportUiState.Loading)
    val creditReport = _creditReport.asStateFlow()
}

sealed interface CreditReportUiState {
    data object Loading : CreditReportUiState
    data class Success(val report: dev.callow.creditscoredemo.data.model.CreditReportResponse) :
        CreditReportUiState

    data class Error(val message: String) : CreditReportUiState
}
