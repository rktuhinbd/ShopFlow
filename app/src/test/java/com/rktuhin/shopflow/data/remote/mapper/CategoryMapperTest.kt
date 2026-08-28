package com.rktuhin.shopflow.data.remote.mapper

import com.rktuhin.shopflow.data.local.entity.CategoryEntity
import com.rktuhin.shopflow.data.remote.dto.CategoryDto
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryMapperTest {

    @Test
    fun `toCategoryEntity maps CategoryDto correctly`() {
        val dto = CategoryDto(
            slug = "smartphones",
            name = "Smartphones",
            url = "https://dummyjson.com/products/category/smartphones"
        )
        
        val cachedAt = 987654321L
        val entity = dto.toCategoryEntity(cachedAt)

        assertEquals(dto.slug, entity.slug)
        assertEquals(dto.name, entity.name)
        assertEquals(dto.url, entity.url)
        assertEquals(cachedAt, entity.cachedAt)
    }

    @Test
    fun `toCategory maps CategoryEntity to Category correctly`() {
        val entity = CategoryEntity(
            slug = "smartphones",
            name = "Smartphones",
            url = "https://dummyjson.com/products/category/smartphones",
            cachedAt = 987654321L
        )

        val domain = entity.toCategory()

        assertEquals(entity.slug, domain.slug)
        assertEquals(entity.name, domain.name)
        assertEquals(entity.url, domain.url)
    }
}
