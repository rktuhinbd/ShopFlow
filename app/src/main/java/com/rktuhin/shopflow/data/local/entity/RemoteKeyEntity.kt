package com.rktuhin.shopflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Paging 3 remote state tracking and context membership.
 * Used by RemoteMediator to know which pages to fetch next (via API skip offsets),
 * and used by ProductDao to associate a canonical ProductEntity to a specific context.
 *
 * PRIMARY KEY(productId, query) guarantees at most one membership/paging row per product per context.
 */
@Entity(
    tableName = "remote_keys",
    primaryKeys = ["productId", "query"]
)
data class RemoteKeyEntity(
    val productId: Int,
    val query: String,
    val prevKey: Int?, // Stores API skip offset (not page index)
    val nextKey: Int?  // Stores API skip offset (not page index)
)
