package com.autoaccounting.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("categoryId"),
        Index("accountId"),
        Index("transactionDate"),
        Index("type")
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: String,
    val note: String? = null,
    val merchant: String? = null,
    val categoryId: Long? = null,
    val accountId: Long,
    @ColumnInfo(name = "toAccountId")
    val toAccountId: Long? = null,
    val source: String = "MANUAL",
    @ColumnInfo(name = "originalMessage")
    val originalMessage: String? = null,
    @ColumnInfo(name = "transactionDate")
    val transactionDate: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
