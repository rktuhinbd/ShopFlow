package com.rktuhin.shopflow.data.remote.mapper

import com.rktuhin.shopflow.data.local.entity.CategoryEntity
import com.rktuhin.shopflow.data.remote.dto.CategoryDto
import com.rktuhin.shopflow.domain.model.Category

fun CategoryDto.toCategoryEntity(cachedAt: Long = System.currentTimeMillis()): CategoryEntity {
    return CategoryEntity(
        slug = this.slug,
        name = this.name,
        url = this.url,
        cachedAt = cachedAt
    )
}

fun CategoryEntity.toCategory(): Category {
    return Category(
        slug = this.slug,
        name = this.name,
        url = this.url
    )
}
