package com.autoaccounting.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.autoaccounting.data.local.dao.AccountDao
import com.autoaccounting.data.local.dao.BudgetDao
import com.autoaccounting.data.local.dao.CategoryDao
import com.autoaccounting.data.local.dao.TransactionDao
import com.autoaccounting.data.local.entity.AccountEntity
import com.autoaccounting.data.local.entity.BudgetEntity
import com.autoaccounting.data.local.entity.CategoryEntity
import com.autoaccounting.data.local.entity.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        AccountEntity::class,
        BudgetEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        const val DATABASE_NAME = "auto_accounting.db"

        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        val database = Room.databaseBuilder(
                            context,
                            AppDatabase::class.java,
                            DATABASE_NAME
                        ).build()
                        database.categoryDao().insertAll(getDefaultCategories())
                        database.accountDao().insert(getDefaultAccount())
                    }
                }
            })
            .build()
        }

        private fun getDefaultCategories(): List<CategoryEntity> {
            return listOf(
                CategoryEntity(name = "餐饮", icon = "restaurant", color = 0xFFFF6B6B, sortOrder = 1, isSystem = true),
                CategoryEntity(name = "交通", icon = "directions_car", color = 0xFF4ECDC4, sortOrder = 2, isSystem = true),
                CategoryEntity(name = "购物", icon = "shopping_bag", color = 0xFFFFBE0B, sortOrder = 3, isSystem = true),
                CategoryEntity(name = "娱乐", icon = "sports_esports", color = 0xFFA855F7, sortOrder = 4, isSystem = true),
                CategoryEntity(name = "居家", icon = "home", color = 0xFF3B82F6, sortOrder = 5, isSystem = true),
                CategoryEntity(name = "医疗", icon = "local_hospital", color = 0xFFEF4444, sortOrder = 6, isSystem = true),
                CategoryEntity(name = "教育", icon = "school", color = 0xFF10B981, sortOrder = 7, isSystem = true),
                CategoryEntity(name = "其他", icon = "more_horiz", color = 0xFF6B7280, sortOrder = 8, isSystem = true)
            )
        }

        private fun getDefaultAccount(): AccountEntity {
            return AccountEntity(
                name = "现金",
                type = "CASH",
                balance = 0.0
            )
        }
    }
}
