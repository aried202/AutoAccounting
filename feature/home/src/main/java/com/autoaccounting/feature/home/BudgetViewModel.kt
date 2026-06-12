package com.autoaccounting.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoaccounting.domain.model.Budget
import com.autoaccounting.domain.usecase.CreateBudgetUseCase
import com.autoaccounting.domain.usecase.GetBudgetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val createBudgetUseCase: CreateBudgetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    init {
        loadBudgets()
    }

    private fun loadBudgets() {
        viewModelScope.launch {
            getBudgetsUseCase().collect { budgets ->
                _uiState.update { it.copy(budgets = budgets) }
            }
        }
    }

    fun createBudget(
        name: String,
        amount: Double,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ) {
        viewModelScope.launch {
            val budget = Budget(
                name = name,
                amount = amount,
                startDate = startDate,
                endDate = endDate
            )
            createBudgetUseCase(budget)
        }
    }
}

data class BudgetUiState(
    val budgets: List<Budget> = emptyList(),
    val isLoading: Boolean = false
)
