package com.autoaccounting.domain.usecase

import com.autoaccounting.domain.model.TransactionType
import com.autoaccounting.domain.repository.TransactionRepository
import java.time.LocalDateTime
import javax.inject.Inject

class GetMonthlySummaryUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(): MonthlySummary {
        val now = LocalDateTime.now()
        val startDate = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
        val endDate = now.withHour(23).withMinute(59).withSecond(59)

        val income = repository.getTotalByTypeAndDateRange(
            TransactionType.INCOME.name, startDate, endDate
        ) ?: 0.0

        val expense = repository.getTotalByTypeAndDateRange(
            TransactionType.EXPENSE.name, startDate, endDate
        ) ?: 0.0

        return MonthlySummary(
            income = income,
            expense = expense,
            balance = income - expense
        )
    }
}

data class MonthlySummary(
    val income: Double,
    val expense: Double,
    val balance: Double
)
