package com.example.finalprojectfinance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojectfinance.data.model.Account
import com.example.finalprojectfinance.data.model.Transaction
import com.example.finalprojectfinance.data.repository.FinanceRepository
import com.example.finalprojectfinance.util.ProjectionUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth

class SavingsViewModel(
    private val repository: FinanceRepository = FinanceRepository()
) : ViewModel() {

    // Expose the list of savings accounts
    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllAccounts()
                .collect { list -> _accounts.value = list }
        }
    }

    /** Add or update an account */
    fun addAccount(name: String, balance: Double, interestRate: Double) {
        viewModelScope.launch {
            repository.upsertAccount(
                Account(
                    name = name,
                    balance = balance,
                    interestRate = interestRate
                )
            )
        }
    }

    /** Delete an account */
    fun deleteAccount(accountId: String) {
        viewModelScope.launch {
            repository.deleteAccount(accountId)
        }
    }

    /** Stream transactions for a given account */
    fun getTransactionsFor(accountId: String): Flow<List<Transaction>> =
        repository.getTransactionsFor(accountId)

    /** Record a new transaction (deposit/withdrawal) */
    fun addTransaction(accountId: String, amount: Double, type: String) {
        viewModelScope.launch {
            repository.addTransaction(
                Transaction(
                    accountId = accountId,
                    amount = amount,
                    type = type
                )
            )
        }
    }

    /** Project future balances over `months` months */
    fun getProjection(account: Account, months: Int = 12): List<Pair<YearMonth, Double>> =
        ProjectionUtils.projectBalance(
            principal    = account.balance,
            annualRate   = account.interestRate,
            months       = months
        )
    fun getAllTransactions(): Flow<List<Transaction>> =
        repository.getAllTransactions()

}
