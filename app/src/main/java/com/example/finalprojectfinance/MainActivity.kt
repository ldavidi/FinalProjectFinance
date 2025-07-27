package com.example.finalprojectfinance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.AccountBalance
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalprojectfinance.data.model.Account
import com.example.finalprojectfinance.data.model.Loan
import com.example.finalprojectfinance.ui.screen.PortfolioScreen
import com.example.finalprojectfinance.ui.screen.SavingsDetailScreen
import com.example.finalprojectfinance.ui.screen.SavingsListScreen
import com.example.finalprojectfinance.ui.screen.LoanListScreen
import com.example.finalprojectfinance.ui.screen.LoanDetailScreen
import com.example.finalprojectfinance.ui.screen.AnalyticsScreen
import com.example.finalprojectfinance.ui.viewmodel.LoanViewModel
import com.example.finalprojectfinance.ui.theme.FinalProjectFinanceTheme
import androidx.compose.material.icons.filled.ShowChart

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Savings   : BottomNavItem("savings",   Icons.Filled.Savings,        "Savings")
    object Portfolio : BottomNavItem("portfolio", Icons.AutoMirrored.Filled.ShowChart,       "Portfolio")
    object Loans     : BottomNavItem("loans",     Icons.Filled.AccountBalance, "Loans")
    object Analytics : BottomNavItem("analytics", Icons.Filled.PieChart,        "Analytics")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinalProjectFinanceTheme {
                // Which tab is selected?
                var currentTab     by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Savings) }
                // Savings detail state
                var selectedAccount by remember { mutableStateOf<Account?>(null) }
                // Loans detail state
                var selectedLoan    by remember { mutableStateOf<Loan?>(null) }

                // LoanViewModel instance
                val loanViewModel: LoanViewModel = viewModel()

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor   = MaterialTheme.colorScheme.onPrimary
                        ) {
                            listOf(
                                BottomNavItem.Savings,
                                BottomNavItem.Portfolio,
                                BottomNavItem.Loans,
                                BottomNavItem.Analytics
                            ).forEach { item ->
                                NavigationBarItem(
                                    icon = { Icon(item.icon, contentDescription = item.label) },
                                    label = { Text(item.label) },
                                    selected = (currentTab == item),
                                    onClick = {
                                        currentTab = item
                                        // reset detail when switching tabs
                                        selectedAccount = null
                                        selectedLoan    = null
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentTab) {
                            BottomNavItem.Analytics -> {
                                AnalyticsScreen()
                            }
                            BottomNavItem.Savings -> {
                                if (selectedAccount == null) {
                                    SavingsListScreen(
                                        onAccountClick = { acct ->
                                            selectedAccount = acct
                                        }
                                    )
                                } else {
                                    SavingsDetailScreen(
                                        account = selectedAccount!!,
                                        onBack  = { selectedAccount = null }
                                    )
                                }
                            }
                            BottomNavItem.Portfolio -> {
                                PortfolioScreen()
                            }
                            BottomNavItem.Loans -> {
                                if (selectedLoan == null) {
                                    LoanListScreen(
                                        onAddLoan  = { selectedLoan = Loan() },
                                        onEditLoan = { loan   -> selectedLoan   = loan }
                                    )
                                } else {
                                    LoanDetailScreen(
                                        loan     = selectedLoan,
                                        onUpsert = { loan ->
                                            loanViewModel.upsertLoan(loan)
                                            selectedLoan = null
                                        },
                                        onBack   = { selectedLoan = null }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
