package com.autoaccounting.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [Index("parentId")]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Long,
    @ColumnInfo(name = "parentId")
    val parentId: Long? = null,
    @ColumnInfo(name = "sortOrder")
    val sortOrder: Int = 0,
    @ColumnInfo(name = "isSystem")
    val isSystem: Boolean = false,
    @ColumnInfo(name = "createdAt")
    val createdAt: Long = System.currentTimeMillis()
)
