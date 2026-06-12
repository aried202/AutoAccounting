package com.autoaccounting.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: Double,
    @ColumnInfo(name = "start_date")
    val startDate: Long,
    @ColumnInfo(name = "end_date")
    val endDate: Long,
    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
