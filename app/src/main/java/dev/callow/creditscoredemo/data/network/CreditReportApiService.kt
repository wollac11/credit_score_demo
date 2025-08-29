package dev.callow.creditscoredemo.data.network

import dev.callow.creditscoredemo.data.model.CreditReportResponse
import retrofit2.http.GET

interface CreditReportApiService {
    @GET("endpoint.json")
    suspend fun getCreditReport(): CreditReportResponse
}
