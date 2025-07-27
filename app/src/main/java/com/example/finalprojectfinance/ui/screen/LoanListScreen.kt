package com.example.finalprojectfinance.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalprojectfinance.data.model.Loan
import com.example.finalprojectfinance.ui.viewmodel.LoanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanListScreen(
    viewModel: LoanViewModel = viewModel(),
    onAddLoan: () -> Unit,
    onEditLoan: (Loan) -> Unit
) {
    val loans by viewModel.loans.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Loans") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddLoan) {
                Icon(Icons.Default.Add, contentDescription = "Add Loan")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(loans, key = { it.id }) { loan ->
                ListItem(
                    headlineContent   = { Text(loan.name) },
                    supportingContent = {
                        Text("Principal: \$${"%.2f".format(loan.principal)}")
                    },
                    modifier = Modifier
                        .clickable { onEditLoan(loan) }
                        .padding(vertical = 4.dp),
                    trailingContent = {
                        IconButton(onClick = { viewModel.deleteLoan(loan.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                )
            }
        }
    }
}
