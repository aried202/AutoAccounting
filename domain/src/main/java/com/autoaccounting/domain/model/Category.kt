package com.autoaccounting.domain.model

import java.time.LocalDateTime

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Long,
    val parentId: Long? = null,
    val sortOrder: Int = 0,
    val isSystem: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
