package com.autoaccounting.domain.usecase

import com.autoaccounting.domain.model.Transaction
import com.autoaccounting.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Transaction>> {
        return repository.getAllTransactions()
    }

    fun byDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Transaction>> {
        return repository.getTransactionsByDateRange(startDate, endDate)
    }

    fun byCategory(categoryId: Long): Flow<List<Transaction>> {
        return repository.getTransactionsByCategory(categoryId)
    }
}
