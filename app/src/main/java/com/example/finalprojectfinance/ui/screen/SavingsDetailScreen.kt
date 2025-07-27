package com.example.finalprojectfinance.ui.screen

import android.view.ViewGroup.LayoutParams
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.viewinterop.AndroidView
import com.example.finalprojectfinance.data.model.Account
import com.example.finalprojectfinance.data.model.Transaction
import com.example.finalprojectfinance.ui.viewmodel.SavingsViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import java.time.format.DateTimeFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsDetailScreen(
    account: Account,
    viewModel: SavingsViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    // Transactions flow
    val transactions by viewModel
        .getTransactionsFor(account.id)
        .collectAsState(initial = emptyList())

    // Projection data
    val projection = remember(account) {
        viewModel.getProjection(account, months = 12)
    }
    val formatter = DateTimeFormatter.ofPattern("MMM yy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(account.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Projection chart
            ChartSection(projection, formatter)

            Spacer(Modifier.height(16.dp))

            Text(
                "Transactions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            LazyColumn(Modifier.fillMaxWidth()) {
                items(transactions) { tx -> TransactionRow(tx) }
            }
        }
    }
}

@Composable
fun ChartSection(
    projection: List<Pair<java.time.YearMonth, Double>>,
    formatter: DateTimeFormatter,
    modifier: Modifier = Modifier // Added modifier parameter
) {
    // Remember the processed data to avoid re-calculation on every recomposition
    // unless 'projection' or 'formatter' changes.
    val chartData = remember(projection, formatter) {
        val entries = projection.mapIndexed { idx, (_, bal) ->
            Entry(idx.toFloat(), bal.toFloat())
        }
        val labels = projection.map { it.first.format(formatter) }
        Pair(entries, labels)
    }
    val entries = chartData.first
    val labels = chartData.second

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                // no manual layoutParams here
                description.isEnabled = false
                // … rest of your setup …
            }
        },
        update = { chart -> // 'chart' is the LineChart instance from factory
            val dataSet = LineDataSet(entries, "Balance").apply {
                setDrawCircles(false)
                lineWidth = 2f
                // Add any other dataset specific styling here
            }
            chart.data = LineData(dataSet)
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            chart.notifyDataSetChanged() // Important: Informs the chart data has changed
            chart.invalidate()          // Redraw the chart
        },
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    )

}

@Composable
private fun TransactionRow(tx: Transaction) {
    val sign = if (tx.type == "DEPOSIT") "+" else "−"
    val color = if (tx.type == "DEPOSIT") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    ListItem(
        headlineContent  = { Text("$sign\$${"%.2f".format(tx.amount)}", color = color) },
        supportingContent  = { Text(java.time.Instant.ofEpochMilli(tx.timestamp).toString()) }
    )
}
