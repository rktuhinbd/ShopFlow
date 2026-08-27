package com.rktuhin.shopflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cache_context")
data class CacheContextEntity(
    @PrimaryKey val query: String,
    val lastUpdated: Long
)
