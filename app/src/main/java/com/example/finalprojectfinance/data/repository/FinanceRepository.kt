package com.example.finalprojectfinance.data.repository

import com.example.finalprojectfinance.data.model.Account
import com.example.finalprojectfinance.data.model.Loan
import com.example.finalprojectfinance.data.model.Transaction
import com.example.finalprojectfinance.data.source.FirestoreDataSource
import kotlinx.coroutines.flow.Flow

class FinanceRepository(
    private val dataSource: FirestoreDataSource = FirestoreDataSource()
) {
    fun getAllAccounts(): Flow<List<Account>> =
        dataSource.getAllAccounts()

    suspend fun upsertAccount(account: Account) =
        dataSource.upsertAccount(account)

    suspend fun deleteAccount(accountId: String) =
        dataSource.deleteAccount(accountId)

    fun getTransactionsFor(accountId: String): Flow<List<Transaction>> =
        dataSource.getTransactionsFor(accountId)

    suspend fun addTransaction(transaction: Transaction) =
        dataSource.addTransaction(transaction)

    fun getAllLoans(): Flow<List<Loan>> = dataSource.getAllLoans()
    suspend fun upsertLoan(loan: Loan) = dataSource.upsertLoan(loan)
    suspend fun deleteLoan(id: String) = dataSource.deleteLoan(id)
    fun getAllTransactions(): Flow<List<Transaction>> =
        dataSource.getAllTransactions()
}
