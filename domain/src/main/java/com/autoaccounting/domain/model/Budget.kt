package com.autoaccounting.domain.model

import java.time.LocalDateTime

data class Budget(
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val spent: Double = 0.0,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val categoryId: Long? = null,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    val remaining: Double
        get() = amount - spent

    val progress: Float
        get() = if (amount > 0) (spent / amount).toFloat().coerceIn(0f, 1f) else 0f

    val isOverBudget: Boolean
        get() = spent > amount
}
