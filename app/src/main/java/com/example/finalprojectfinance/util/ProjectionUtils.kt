package com.example.finalprojectfinance.util

import kotlin.math.pow
import java.time.YearMonth

object ProjectionUtils {

    /**
     * Returns a list of (YearMonth, projected balance) pairs,
     * from now out to [months] months in the future (inclusive).
     */
    fun projectBalance(
        principal: Double,
        annualRate: Double,
        months: Int
    ): List<Pair<YearMonth, Double>> {
        val monthlyRate = annualRate / 12.0
        val start = YearMonth.now()
        return (0..months).map { n ->
            val projected = principal * (1 + monthlyRate).pow(n)
            start.plusMonths(n.toLong()) to projected
        }
    }
}
