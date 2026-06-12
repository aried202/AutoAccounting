package com.autoaccounting.domain.repository

import com.autoaccounting.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getActiveAccounts(): Flow<List<Account>>
    fun getAllAccounts(): Flow<List<Account>>
    suspend fun getById(id: Long): Account?
    suspend fun insert(account: Account): Long
    suspend fun update(account: Account)
    suspend fun delete(account: Account)
    suspend fun updateBalance(accountId: Long, amount: Double)
}
