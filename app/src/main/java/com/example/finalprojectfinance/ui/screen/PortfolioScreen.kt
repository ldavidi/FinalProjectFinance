package com.example.finalprojectfinance.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalprojectfinance.data.model.Holding
import com.example.finalprojectfinance.ui.viewmodel.PortfolioViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.example.finalprojectfinance.data.source.FirestoreDataSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel = viewModel()
) {
    val holdings by viewModel.holdings.collectAsState()
    val totalValue by viewModel.totalValue.collectAsState()
    var ticker by remember { mutableStateOf("") }
    var sharesText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Portfolio") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.refreshPrices() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Add Account")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = ticker,
                    onValueChange = { ticker = it },
                    label = { Text("Ticker") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = sharesText,
                    onValueChange = { sharesText = it },
                    label = { Text("Shares") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = {
                    val sym    = ticker.trim().uppercase()
                    val shares = sharesText.toDoubleOrNull() ?: return@Button
                    viewModel.addHolding(sym, shares)
                    ticker = ""
                    sharesText = ""
                }) {
                    Text("Add")
                }
            }
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Total Value: \$${"%.2f".format(totalValue)}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(holdings, key = { it.ticker }) { holding ->
                    HoldingRow(holding) { viewModel.removeHolding(holding.ticker) }
                }
            }
        }
    }
}

@Composable
private fun HoldingRow(holding: Holding, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(holding.ticker, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${holding.shares} shares @ \$${"%.2f".format(holding.currentPrice)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove Holding")
            }
        }
    }
}

