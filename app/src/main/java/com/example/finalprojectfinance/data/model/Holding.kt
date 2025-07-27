package com.example.finalprojectfinance.data.model

data class Holding(
    val id: String          = "",    // Firestore ID
    val ticker: String      = "",    // default empty
    val shares: Double      = 0.0,   // default zero
    val currentPrice: Double= 0.0    // default zero
) {
    val value: Double
        get() = shares * currentPrice
}
