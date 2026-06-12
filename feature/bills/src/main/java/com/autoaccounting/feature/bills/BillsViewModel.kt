package com.autoaccounting.feature.bills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoaccounting.domain.model.Transaction
import com.autoaccounting.domain.usecase.GetMonthlySummaryUseCase
import com.autoaccounting.domain.usecase.GetTransactionsUseCase
import com.autoaccounting.domain.usecase.MonthlySummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BillsUiState())
    val uiState: StateFlow<BillsUiState> = _uiState.asStateFlow()

    init {
        loadBills()
    }

    private fun loadBills() {
        viewModelScope.launch {
            val summary = getMonthlySummaryUseCase()
            getTransactionsUseCase().collect { transactions ->
                _uiState.update {
                    it.copy(
                        monthlySummary = summary,
                        transactions = transactions
                    )
                }
            }
        }
    }
}

data class BillsUiState(
    val monthlySummary: MonthlySummary = MonthlySummary(0.0, 0.0, 0.0),
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false
)
