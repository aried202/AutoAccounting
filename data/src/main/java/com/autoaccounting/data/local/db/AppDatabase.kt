package com.autoaccounting.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.autoaccounting.data.local.dao.AccountDao
import com.autoaccounting.data.local.dao.BudgetDao
import com.autoaccounting.data.local.dao.CategoryDao
import com.autoaccounting.data.local.dao.TransactionDao
import com.autoaccounting.data.local.entity.AccountEntity
import com.autoaccounting.data.local.entity.BudgetEntity
import com.autoaccounting.data.local.entity.CategoryEntity
import com.autoaccounting.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        AccountEntity::class,
        BudgetEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        const val DATABASE_NAME = "auto_accounting.db"

        // 数据库迁移示例
        // 从 version 2 升级到 version 3 时的迁移
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE accounts ADD COLUMN currency TEXT NOT NULL DEFAULT 'CNY'")
            }
        }

        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .addMigrations(MIGRATION_2_3)
            .addCallback(DatabaseCallback())
            .build()
        }
    }
}

/**
 * 数据库创建回调：使用 SupportSQLiteDatabase 同步插入默认数据，
 * 避免在 onCreate 回调中启动协程（onCreate 是同步调用，且 Room 不应被多次构建）。
 */
private class DatabaseCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        seedDefaultCategories(db)
        seedDefaultAccount(db)
    }

    private fun seedDefaultCategories(db: SupportSQLiteDatabase) {
        val sql = """
            INSERT INTO categories (name, icon, color, parentId, sortOrder, isSystem, createdAt)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        val now = System.currentTimeMillis()

        val defaultCategories = listOf(
            // 使用 ARGB 颜色：0xAARRGGBB
            Triple("餐饮", "restaurant", 0xFFFF6B6BL),
            Triple("交通", "directions_car", 0xFF4ECDC4L),
            Triple("购物", "shopping_bag", 0xFFFFBE0BL),
            Triple("娱乐", "sports_esports", 0xFFA855F7L),
            Triple("居家", "home", 0xFF3B82F6L),
            Triple("医疗", "local_hospital", 0xFFEF4444L),
            Triple("教育", "school", 0xFF10B981L),
            Triple("其他", "more_horiz", 0xFF6B7280L)
        )

        defaultCategories.forEachIndexed { index, (name, icon, color) ->
            db.execSQL(
                sql,
                arrayOf(name, icon, color, null, index + 1, 1, now)
            )
        }
    }

    private fun seedDefaultAccount(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            INSERT INTO accounts (name, type, balance, currency, isActive, createdAt)
            VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            arrayOf("现金", "CASH", 0.0, "CNY", 1, System.currentTimeMillis())
        )
    }
}
