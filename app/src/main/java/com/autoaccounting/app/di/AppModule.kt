package com.autoaccounting.app.di

import android.content.Context
import com.autoaccounting.data.local.dao.AccountDao
import com.autoaccounting.data.local.dao.BudgetDao
import com.autoaccounting.data.local.dao.CategoryDao
import com.autoaccounting.data.local.dao.TransactionDao
import com.autoaccounting.data.local.db.AppDatabase
import com.autoaccounting.data.repository.AccountRepositoryImpl
import com.autoaccounting.data.repository.BudgetRepositoryImpl
import com.autoaccounting.data.repository.CategoryRepositoryImpl
import com.autoaccounting.data.repository.TransactionRepositoryImpl
import com.autoaccounting.domain.repository.AccountRepository
import com.autoaccounting.domain.repository.BudgetRepository
import com.autoaccounting.domain.repository.CategoryRepository
import com.autoaccounting.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.create(context)
    }

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()

    @Provides
    fun provideBudgetDao(db: AppDatabase): BudgetDao = db.budgetDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    abstract fun bindBudgetRepository(impl: BudgetRepositoryImpl): BudgetRepository
}
