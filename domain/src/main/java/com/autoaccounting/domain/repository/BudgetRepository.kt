package com.autoaccounting.domain.repository

import com.autoaccounting.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    suspend fun insert(budget: Budget): Long
    suspend fun update(budget: Budget)
    suspend fun delete(budget: Budget)
    fun getActiveBudgets(): Flow<List<Budget>>
    fun getAllBudgets(): Flow<List<Budget>>
    suspend fun getById(id: Long): Budget?
    suspend fun getSpentAmount(startDate: Long, endDate: Long): Double
}
