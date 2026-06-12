package com.autoaccounting.domain.repository

import com.autoaccounting.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    fun getRootCategories(): Flow<List<Category>>
    fun getSubCategories(parentId: Long): Flow<List<Category>>
    suspend fun getById(id: Long): Category?
    suspend fun getByName(name: String): Category?
    suspend fun insert(category: Category): Long
    suspend fun update(category: Category)
    suspend fun delete(category: Category)
    suspend fun getCount(): Int
}
