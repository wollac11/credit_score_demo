package dev.callow.creditscoredemo.ui

import android.util.Log
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.callow.creditscoredemo.R
import dev.callow.creditscoredemo.data.repository.CreditReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreditScoreViewModel @Inject constructor(
    private val repository: CreditReportRepository,
    @param:ApplicationContext private val applicationContext: Context
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
                Log.d("CreditScoreViewModel", "Error fetching credit report: ${e.message}")
                _creditReport.value =
                    CreditReportUiState.Error(applicationContext.getString(R.string.fetch_error))
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
