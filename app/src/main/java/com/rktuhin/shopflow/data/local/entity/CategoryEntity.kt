package com.rktuhin.shopflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a cached product category.
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val slug: String,
    val name: String,
    val url: String,
    val cachedAt: Long
)
