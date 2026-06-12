package com.autoaccounting.domain.repository

import com.autoaccounting.domain.model.CategorySummary
import com.autoaccounting.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Transaction>>
    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>>
    suspend fun insert(transaction: Transaction): Long
    suspend fun update(transaction: Transaction)
    suspend fun delete(transaction: Transaction)
    suspend fun getById(id: Long): Transaction?
    suspend fun getTotalByTypeAndDateRange(type: String, startDate: LocalDateTime, endDate: LocalDateTime): Double?
    suspend fun getExpenseSummaryByCategory(startDate: LocalDateTime, endDate: LocalDateTime): List<CategorySummary>
}
