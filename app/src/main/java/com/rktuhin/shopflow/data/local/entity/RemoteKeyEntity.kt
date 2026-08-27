package com.rktuhin.shopflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Paging 3 remote state tracking.
 * Used by RemoteMediator to know which pages to fetch next.
 */
@Entity(
    tableName = "remote_keys",
    primaryKeys = ["productId", "query"]
)
data class RemoteKeyEntity(
    val productId: Int,
    val query: String,
    val prevKey: Int?,
    val currentPage: Int,
    val nextKey: Int?,
    val createdAt: Long
)
