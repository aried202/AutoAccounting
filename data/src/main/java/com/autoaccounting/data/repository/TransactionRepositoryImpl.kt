package com.autoaccounting.data.repository

import com.autoaccounting.data.local.dao.TransactionDao
import com.autoaccounting.data.mapper.toDomain
import com.autoaccounting.data.mapper.toEntity
import com.autoaccounting.domain.model.CategorySummary
import com.autoaccounting.domain.model.Transaction
import com.autoaccounting.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTransactionsByDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Flow<List<Transaction>> {
        return dao.getTransactionsByDateRange(startDate.toEpochMilli(), endDate.toEpochMilli())
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> {
        return dao.getTransactionsByCategory(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insert(transaction: Transaction): Long {
        return dao.insert(transaction.toEntity())
    }

    override suspend fun update(transaction: Transaction) {
        dao.update(transaction.toEntity())
    }

    override suspend fun delete(transaction: Transaction) {
        dao.delete(transaction.toEntity())
    }

    override suspend fun getById(id: Long): Transaction? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun getTotalByTypeAndDateRange(
        type: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Double? {
        return dao.getTotalByTypeAndDateRange(
            type,
            startDate.toEpochMilli(),
            endDate.toEpochMilli()
        )
    }

    override suspend fun getExpenseSummaryByCategory(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<CategorySummary> {
        return dao.getExpenseSummaryByCategory(
            startDate.toEpochMilli(),
            endDate.toEpochMilli()
        ).map {
            CategorySummary(categoryId = it.categoryId, categoryName = null, total = it.total)
        }
    }
}

/** LocalDateTime → epoch millis，使用系统时区做一次转换。 */
private fun LocalDateTime.toEpochMilli(): Long =
    atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
