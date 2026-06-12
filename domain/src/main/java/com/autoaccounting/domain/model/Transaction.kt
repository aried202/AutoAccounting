package com.autoaccounting.domain.model

import java.time.LocalDateTime

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val note: String? = null,
    val merchant: String? = null,
    val categoryId: Long? = null,
    val accountId: Long,
    val source: TransactionSource = TransactionSource.MANUAL,
    val originalMessage: String? = null,
    val transactionDate: LocalDateTime = LocalDateTime.now(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

enum class TransactionSource {
    MANUAL, SMS, NOTIFICATION
}
