package com.example.finalprojectfinance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojectfinance.data.model.Loan
import com.example.finalprojectfinance.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoanViewModel(
    private val repo: FinanceRepository = FinanceRepository()
) : ViewModel() {
    private val _loans = MutableStateFlow<List<Loan>>(emptyList())
    val loans: StateFlow<List<Loan>> = _loans.asStateFlow()

    /** Sum of outstanding principals */
    val totalOutstanding: StateFlow<Double> = loans
        .map { list -> list.sumOf { it.principal } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    init {
        viewModelScope.launch {
            repo.getAllLoans().collect { _loans.value = it }
        }
    }

    fun upsertLoan(loan: Loan) {
        viewModelScope.launch { repo.upsertLoan(loan) }
    }

    fun deleteLoan(id: String) {
        viewModelScope.launch { repo.deleteLoan(id) }
    }
}
