package com.autoaccounting.data.repository

import com.autoaccounting.data.local.dao.BudgetDao
import com.autoaccounting.data.local.entity.BudgetEntity
import com.autoaccounting.domain.model.Budget
import com.autoaccounting.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override suspend fun insert(budget: Budget): Long {
        return budgetDao.insert(budget.toEntity())
    }

    override suspend fun update(budget: Budget) {
        budgetDao.update(budget.toEntity())
    }

    override suspend fun delete(budget: Budget) {
        budgetDao.delete(budget.toEntity())
    }

    override fun getActiveBudgets(): Flow<List<Budget>> {
        return budgetDao.getActiveBudgets().map { entities ->
            entities.map { entity ->
                entity.toDomain().copy(
                    spent = budgetDao.getSpentAmount(entity.startDate, entity.endDate)
                )
            }
        }
    }

    override fun getAllBudgets(): Flow<List<Budget>> {
        return budgetDao.getAllBudgets().map { entities ->
            entities.map { entity ->
                entity.toDomain().copy(
                    spent = budgetDao.getSpentAmount(entity.startDate, entity.endDate)
                )
            }
        }
    }

    override suspend fun getById(id: Long): Budget? {
        return budgetDao.getById(id)?.toDomain()
    }

    override suspend fun getSpentAmount(startDate: Long, endDate: Long): Double {
        return budgetDao.getSpentAmount(startDate, endDate)
    }

    private fun BudgetEntity.toDomain(): Budget {
        return Budget(
            id = id,
            name = name,
            amount = amount,
            startDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(startDate), ZoneId.systemDefault()),
            endDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(endDate), ZoneId.systemDefault()),
            categoryId = categoryId,
            isActive = isActive,
            createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault())
        )
    }

    private fun Budget.toEntity(): BudgetEntity {
        return BudgetEntity(
            id = id,
            name = name,
            amount = amount,
            startDate = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            endDate = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            categoryId = categoryId,
            isActive = isActive,
            createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }
}
