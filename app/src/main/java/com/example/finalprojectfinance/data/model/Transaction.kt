package com.example.finalprojectfinance.data.model

data class Transaction(
    val id: String         = "",
    val accountId: String  = "",
    val amount: Double     = 0.0,
    val timestamp: Long    = 0L,
    val type: String       = "DEPOSIT"
)


