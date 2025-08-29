package dev.callow.creditscoredemo.data.repository

import dev.callow.creditscoredemo.data.model.CreditReportResponse
import dev.callow.creditscoredemo.data.network.CreditReportApiService
import javax.inject.Inject

class CreditReportRepository @Inject constructor(
    private val apiService: CreditReportApiService
) {
    suspend fun getCreditReport(): CreditReportResponse {
        return apiService.getCreditReport()
    }
}