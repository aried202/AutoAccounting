package com.autoaccounting.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.autoaccounting.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("""
        SELECT * FROM transactions
        WHERE transactionDate BETWEEN :startDate AND :endDate
        ORDER BY transactionDate DESC
    """)
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions
        WHERE categoryId = :categoryId
        ORDER BY transactionDate DESC
    """)
    fun getTransactionsByCategory(categoryId: Long): Flow<List<TransactionEntity>>

    @Query("""
        SELECT SUM(amount) FROM transactions
        WHERE type = :type AND transactionDate BETWEEN :startDate AND :endDate
    """)
    suspend fun getTotalByTypeAndDateRange(type: String, startDate: Long, endDate: Long): Double?

    @Query("""
        SELECT categoryId, SUM(amount) as total
        FROM transactions
        WHERE type = 'EXPENSE' AND transactionDate BETWEEN :startDate AND :endDate
        GROUP BY categoryId
    """)
    suspend fun getExpenseSummaryByCategory(startDate: Long, endDate: Long): List<CategorySummaryEntity>
}

data class CategorySummaryEntity(
    val categoryId: Long?,
    val total: Double
)
