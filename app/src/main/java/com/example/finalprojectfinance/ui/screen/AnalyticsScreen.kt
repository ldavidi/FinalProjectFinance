package com.example.finalprojectfinance.ui.screen

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalprojectfinance.data.repository.FinanceRepository
import com.example.finalprojectfinance.ui.viewmodel.LoanViewModel
import com.example.finalprojectfinance.ui.viewmodel.PortfolioViewModel
import com.example.finalprojectfinance.ui.viewmodel.SavingsViewModel
import com.example.finalprojectfinance.util.ProjectionUtils
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun AnalyticsScreen(
    portfolioVM: PortfolioViewModel = viewModel(),
    loanVM: LoanViewModel         = viewModel(),
    savingsVM: SavingsViewModel   = viewModel(),
    repository: FinanceRepository = FinanceRepository()
) {
    // 1) Collect flows
    val totalAssets   by portfolioVM.totalValue.collectAsState()
    val totalLoans    by loanVM.totalOutstanding.collectAsState()
    // 2) Compute deposit total
    val accounts by savingsVM.accounts.collectAsState()
    val totalDeposits = remember(accounts) {
        accounts.sumOf { it.balance }
    }

    // 3) Pie chart entries & colors
    val pieEntries = listOf(
        PieEntry(totalAssets.toFloat(),   "Assets"),
        PieEntry(totalLoans.toFloat(),    "Loans"),
        PieEntry(totalDeposits.toFloat(), "Deposits")
    )
    val pieColors = listOf(
        AndroidColor.parseColor("#66BB6A"), // LightGreen :contentReference[oaicite:5]{index=5}
        AndroidColor.RED,
        AndroidColor.BLUE
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Assets vs. Loans vs. Deposits",
            style = MaterialTheme.typography.titleLarge)

        AndroidView(
            factory = { ctx ->
                PieChart(ctx).apply {
                    description.isEnabled = false
                    legend.isEnabled      = true
                }
            },
            update = { chart ->
                val ds = PieDataSet(pieEntries, "").apply {
                    setColors(pieColors)
                    valueTextSize = 12f
                    sliceSpace    = 2f
                }
                chart.data = PieData(ds)
                chart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )

        // 4) Build a simple net-worth projection: assets minus loans growing at avg savings rate
        //    Fetch accounts for their rates:
        val accounts by savingsVM.accounts.collectAsState()
        val avgRate = remember(accounts) {
            if (accounts.isEmpty()) 0.0
            else accounts.map { it.interestRate }.average()
        }
        // principal = totalAssets - totalLoans
        val netPrincipal = totalAssets - totalLoans
        val projection = remember(netPrincipal, avgRate) {
            ProjectionUtils.projectBalance(
                principal  = netPrincipal,
                annualRate = avgRate,
                months     = 12
            )
        }

        Text("Net Worth Projection", style = MaterialTheme.typography.titleLarge)

        // Reuse your public ChartSection (now non-private)
        ChartSection(
            projection = projection,
            formatter  = DateTimeFormatter.ofPattern("MMM yy"),
            modifier   = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}
