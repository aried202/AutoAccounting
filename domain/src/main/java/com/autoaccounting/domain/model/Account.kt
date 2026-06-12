package com.autoaccounting.domain.model

import java.time.LocalDateTime

data class Account(
    val id: Long = 0,
    val name: String,
    val type: AccountType,
    val balance: Double = 0.0,
    val currency: String = "CNY",
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class AccountType {
    CASH, BANK_CARD, CREDIT_CARD, ALIPAY, WECHAT, OTHER
}
