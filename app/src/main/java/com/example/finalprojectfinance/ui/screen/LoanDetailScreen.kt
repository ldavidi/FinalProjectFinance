package com.example.finalprojectfinance.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.format.DateTimeFormatter
import com.example.finalprojectfinance.data.model.Loan
import com.example.finalprojectfinance.ui.viewmodel.LoanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailScreen(
    loan: Loan? = null,
    onUpsert: (Loan) -> Unit,
    onBack: () -> Unit
) {
    var nameText      by rememberSaveable { mutableStateOf(loan?.name.orEmpty()) }
    var principalText by rememberSaveable { mutableStateOf(loan?.principal?.toString().orEmpty()) }
    var rateText      by rememberSaveable { mutableStateOf(loan?.annualRate?.toString().orEmpty()) }
    var termText      by rememberSaveable { mutableStateOf(loan?.termMonths?.toString().orEmpty()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (loan == null) "Add Loan" else "Edit Loan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val principal = principalText.toDoubleOrNull() ?: 0.0
                        val rate      = rateText.toDoubleOrNull() ?: 0.0
                        val term      = termText.toIntOrNull()    ?: 0

                        onUpsert(
                            Loan(
                                id          = loan?.id ?: "",
                                name        = nameText,
                                principal   = principal,
                                annualRate  = rate,
                                termMonths  = term
                            )
                        )
                        onBack()
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nameText,
                onValueChange = { nameText = it },
                label = { Text("Loan Name") },
                singleLine = true
            )
            OutlinedTextField(
                value = principalText,
                onValueChange = { principalText = it },
                label = { Text("Principal") },
                singleLine = true
            )
            OutlinedTextField(
                value = rateText,
                onValueChange = { rateText = it },
                label = { Text("Annual Rate (%)") },
                singleLine = true
            )
            OutlinedTextField(
                value = termText,
                onValueChange = { termText = it },
                label = { Text("Term (months)") },
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))
            Text("Amortization Schedule", style = MaterialTheme.typography.titleMedium)

            // reuse your ChartSection to plot the loan balance over time
            val schedule = remember(loan) { loan?.amortizationSchedule() ?: emptyList() }
            ChartSection(
                projection = schedule,
                formatter  = DateTimeFormatter.ofPattern("MMM yy"),
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}
