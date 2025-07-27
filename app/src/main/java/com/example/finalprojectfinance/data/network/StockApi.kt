package com.example.finalprojectfinance.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class QuoteResponse(
    @Json(name = "Global Quote")
    val globalQuote: GlobalQuote?
)

@JsonClass(generateAdapter = true)
data class GlobalQuote(
    @Json(name = "05. price") val price: String?
)

interface StockApi {
    @GET("query?function=GLOBAL_QUOTE")
    suspend fun getQuote(
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): QuoteResponse
}
