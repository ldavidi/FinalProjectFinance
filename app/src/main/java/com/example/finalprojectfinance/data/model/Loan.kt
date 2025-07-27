package com.example.finalprojectfinance.data.model

import com.google.firebase.Timestamp
import java.time.YearMonth
import com.example.finalprojectfinance.util.ProjectionUtils

/**
 * @param termMonths how many months to project (e.g. a 5-year loan → 60)
 * @param startDate optional Firestore timestamp of loan start
 */
data class Loan(
    val id: String            = "",
    val name: String          = "",
    val principal: Double     = 0.0,
    val annualRate: Double    = 0.0,
    val termMonths: Int       = 0,
    val startDate: Timestamp? = null
) {
    /** Simple projection of remaining balance over time */
    fun amortizationSchedule(): List<Pair<YearMonth, Double>> =
        ProjectionUtils.projectBalance(principal, annualRate, termMonths)
}
