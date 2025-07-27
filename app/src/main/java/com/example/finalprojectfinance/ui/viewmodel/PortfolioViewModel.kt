package com.example.finalprojectfinance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojectfinance.data.model.Holding
import com.example.finalprojectfinance.data.source.FirestoreDataSource
import com.example.finalprojectfinance.data.repository.StockRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PortfolioViewModel(
    private val stockRepo: StockRepository = StockRepository("S6D1C4Y614DQH7QL")
) : ViewModel() {
    private val dataSource = FirestoreDataSource()

    private val _holdings = MutableStateFlow<List<Holding>>(emptyList())
    val holdings: StateFlow<List<Holding>> = _holdings.asStateFlow()

    /** Total portfolio value = sum of all holding.value */
    val totalValue: StateFlow<Double> = holdings
        .map { it.sumOf { h -> h.value } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    init {
        // Stream persisted holdings into our state
        viewModelScope.launch {
            dataSource.getAllHoldings().collect { list ->
                _holdings.value = list
            }
        }
    }

    /** Create or update a holding in Firestore */
    fun addHolding(ticker: String, shares: Double) {
        viewModelScope.launch {
            val sym   = ticker.trim().uppercase()
            val price = stockRepo.getCurrentPrice(sym)

            // preserve existing ID if we’re editing
            val existing = _holdings.value.find { it.ticker == sym }
            val h = existing?.copy(shares = shares, currentPrice = price)
                ?: Holding(id = "", ticker = sym, shares = shares, currentPrice = price)

            dataSource.upsertHolding(h)
        }
    }

    /** Remove by Firestore ID */
    fun removeHolding(ticker: String) {
        viewModelScope.launch {
            val sym     = ticker.uppercase()
            val toDelete = _holdings.value.find { it.ticker == sym } ?: return@launch
            dataSource.deleteHolding(toDelete.id)
        }
    }

    /** Refresh prices & overwrite each holding */
    fun refreshPrices() {
        viewModelScope.launch {
            _holdings.value.forEach { h ->
                val price = stockRepo.getCurrentPrice(h.ticker)
                dataSource.upsertHolding(h.copy(currentPrice = price))
            }
        }
    }
}
