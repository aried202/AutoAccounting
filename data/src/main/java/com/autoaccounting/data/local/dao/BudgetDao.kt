package com.autoaccounting.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.autoaccounting.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity): Long

    @Update
    suspend fun update(budget: BudgetEntity)

    @Delete
    suspend fun delete(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE is_active = 1 ORDER BY created_at DESC")
    fun getActiveBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets ORDER BY created_at DESC")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getById(id: Long): BudgetEntity?

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE transactionDate BETWEEN :start AND :end AND type = 'EXPENSE'")
    fun getSpentAmount(start: Long, end: Long): Double
}
