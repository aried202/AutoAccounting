package com.autoaccounting.data.repository

import com.autoaccounting.data.local.dao.CategoryDao
import com.autoaccounting.data.mapper.toDomain
import com.autoaccounting.data.mapper.toEntity
import com.autoaccounting.domain.model.Category
import com.autoaccounting.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return dao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRootCategories(): Flow<List<Category>> {
        return dao.getRootCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSubCategories(parentId: Long): Flow<List<Category>> {
        return dao.getSubCategories(parentId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: Long): Category? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun getByName(name: String): Category? {
        return dao.getByName(name)?.toDomain()
    }

    override suspend fun insert(category: Category): Long {
        return dao.insert(category.toEntity())
    }

    override suspend fun update(category: Category) {
        dao.update(category.toEntity())
    }

    override suspend fun delete(category: Category) {
        dao.delete(category.toEntity())
    }

    override suspend fun getCount(): Int {
        return dao.getCount()
    }
}
