package com.example.finalprojectfinance.data.model

data class Account(
    val id: String = "",          // Firestore-generated ID
    val name: String = "",
    val balance: Double = 0.0,
    val interestRate: Double = 0.015  // 1.5% APR default
)
