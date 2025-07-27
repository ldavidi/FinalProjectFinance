package com.example.finalprojectfinance.data.repository

import com.example.finalprojectfinance.data.network.StockApi
import com.example.finalprojectfinance.data.model.Holding
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class StockRepository(
    private val apiKey: String
) {
    private val mutex = Mutex()
    private val cache = mutableMapOf<String, Pair<Long, Double>>()

    private val api: StockApi by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .callTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://www.alphavantage.co/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(StockApi::class.java)
    }

    /**
     * Fetch current price, caching for 5 minutes to avoid rate limits.
     */
    suspend fun getCurrentPrice(ticker: String): Double = mutex.withLock {
        val now = System.currentTimeMillis()
        val cached = cache[ticker]
        if (cached != null && now - cached.first < 5 * 60_000) {
            return cached.second
        }
        val resp = api.getQuote(ticker, apiKey)
        val price = resp.globalQuote?.price?.toDoubleOrNull() ?: 0.0
        cache[ticker] = now to price
        return price
    }
}

