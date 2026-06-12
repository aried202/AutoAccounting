package com.autoaccounting.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String,
    val balance: Double = 0.0,
    val currency: String = "CNY",
    @ColumnInfo(name = "isActive")
    val isActive: Boolean = true,
    @ColumnInfo(name = "createdAt")
    val createdAt: Long = System.currentTimeMillis()
)
