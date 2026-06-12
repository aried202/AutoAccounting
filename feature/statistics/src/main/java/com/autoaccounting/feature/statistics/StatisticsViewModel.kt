package com.autoaccounting.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoaccounting.domain.model.CategorySummary
import com.autoaccounting.domain.repository.TransactionRepository
import com.autoaccounting.domain.usecase.GetMonthlySummaryUseCase
import com.autoaccounting.domain.usecase.MonthlySummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            val summary = getMonthlySummaryUseCase()
            val now = LocalDateTime.now()
            val startDate = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
            val endDate = now.withHour(23).withMinute(59).withSecond(59)
            val categorySummary = transactionRepository.getExpenseSummaryByCategory(startDate, endDate)

            _uiState.update {
                it.copy(
                    monthlySummary = summary,
                    categorySummary = categorySummary
                )
            }
        }
    }
}

data class StatisticsUiState(
    val monthlySummary: MonthlySummary = MonthlySummary(0.0, 0.0, 0.0),
    val categorySummary: List<CategorySummary> = emptyList()
)
