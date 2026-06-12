package com.autoaccounting.data.repository

import com.autoaccounting.data.local.dao.AccountDao
import com.autoaccounting.data.mapper.toDomain
import com.autoaccounting.data.mapper.toEntity
import com.autoaccounting.domain.model.Account
import com.autoaccounting.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val dao: AccountDao
) : AccountRepository {

    override fun getActiveAccounts(): Flow<List<Account>> {
        return dao.getActiveAccounts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllAccounts(): Flow<List<Account>> {
        return dao.getAllAccounts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: Long): Account? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun insert(account: Account): Long {
        return dao.insert(account.toEntity())
    }

    override suspend fun update(account: Account) {
        dao.update(account.toEntity())
    }

    override suspend fun delete(account: Account) {
        dao.delete(account.toEntity())
    }

    override suspend fun updateBalance(accountId: Long, amount: Double) {
        dao.updateBalance(accountId, amount)
    }
}
