package com.autoaccounting.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoaccounting.common.prefs.UserPrefs
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
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val userPrefs: UserPrefs
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUsername()
        loadTransactions()
        loadMonthlySummary()
    }

    private fun loadUsername() {
        viewModelScope.launch {
            userPrefs.usernameFlow.collect { name ->
                _uiState.update { it.copy(username = name) }
            }
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            getTransactionsUseCase().collect { transactions ->
                val today = LocalDate.now()
                val (todayTransactions, historical) = transactions.partition {
                    it.transactionDate.toLocalDate() == today
                }
                val groupedByDate = historical
                    .groupBy { it.transactionDate.toLocalDate() }
                    .toSortedMap(compareByDescending { it })
                val recent = (todayTransactions + historical).take(RECENT_TRANSACTION_LIMIT)

                _uiState.update {
                    it.copy(
                        todayTransactions = todayTransactions,
                        groupedByDate = groupedByDate,
                        recentTransactions = recent
                    )
                }
            }
        }
    }

    private fun loadMonthlySummary() {
        viewModelScope.launch {
            val summary = getMonthlySummaryUseCase()
            _uiState.update {
                it.copy(monthlySummary = summary)
            }
        }
    }
}

data class HomeUiState(
    val username: String = "用户",
    val monthlySummary: MonthlySummary = MonthlySummary(0.0, 0.0, 0.0),
    val todayTransactions: List<Transaction> = emptyList(),
    val groupedByDate: Map<LocalDate, List<Transaction>> = emptyMap(),
    val recentTransactions: List<Transaction> = emptyList()
)

private const val RECENT_TRANSACTION_LIMIT = 5
