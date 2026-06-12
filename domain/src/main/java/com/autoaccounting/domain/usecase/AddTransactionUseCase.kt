package com.autoaccounting.domain.usecase

import com.autoaccounting.domain.model.Transaction
import com.autoaccounting.domain.model.TransactionSource
import com.autoaccounting.domain.model.TransactionType
import com.autoaccounting.domain.repository.TransactionRepository
import java.time.LocalDateTime
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(
        amount: Double,
        type: TransactionType,
        categoryId: Long? = null,
        accountId: Long,
        note: String? = null,
        merchant: String? = null,
        source: TransactionSource = TransactionSource.MANUAL,
        originalMessage: String? = null,
        date: LocalDateTime = LocalDateTime.now()
    ): Long {
        val transaction = Transaction(
            amount = amount,
            type = type,
            categoryId = categoryId,
            accountId = accountId,
            note = note,
            merchant = merchant,
            source = source,
            originalMessage = originalMessage,
            transactionDate = date
        )
        return repository.insert(transaction)
    }
}
