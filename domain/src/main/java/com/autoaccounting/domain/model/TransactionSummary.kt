package com.autoaccounting.domain.model

data class TransactionSummary(
    val income: Double,
    val expense: Double,
    val balance: Double
)

data class CategorySummary(
    val categoryId: Long?,
    val categoryName: String?,
    val total: Double
)
