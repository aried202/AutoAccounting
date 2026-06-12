package com.autoaccounting.data.mapper

import com.autoaccounting.data.local.entity.AccountEntity
import com.autoaccounting.data.local.entity.CategoryEntity
import com.autoaccounting.data.local.entity.TransactionEntity
import com.autoaccounting.domain.model.Account
import com.autoaccounting.domain.model.AccountType
import com.autoaccounting.domain.model.Category
import com.autoaccounting.domain.model.Transaction
import com.autoaccounting.domain.model.TransactionSource
import com.autoaccounting.domain.model.TransactionType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        type = TransactionType.valueOf(type),
        note = note,
        merchant = merchant,
        categoryId = categoryId,
        accountId = accountId,
        source = TransactionSource.valueOf(source),
        originalMessage = originalMessage,
        transactionDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(transactionDate), ZoneId.systemDefault()),
        createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault()),
        updatedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedAt), ZoneId.systemDefault())
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        type = type.name,
        note = note,
        merchant = merchant,
        categoryId = categoryId,
        accountId = accountId,
        source = source.name,
        originalMessage = originalMessage,
        transactionDate = transactionDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        icon = icon,
        color = color,
        parentId = parentId,
        sortOrder = sortOrder,
        isSystem = isSystem,
        createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault())
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        icon = icon,
        color = color,
        parentId = parentId,
        sortOrder = sortOrder,
        isSystem = isSystem,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}

fun AccountEntity.toDomain(): Account {
    return Account(
        id = id,
        name = name,
        type = AccountType.valueOf(type),
        balance = balance,
        currency = currency,
        isActive = isActive,
        createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault())
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        name = name,
        type = type.name,
        balance = balance,
        currency = currency,
        isActive = isActive,
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}
