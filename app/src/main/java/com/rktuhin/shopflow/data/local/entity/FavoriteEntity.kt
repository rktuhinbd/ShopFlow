package com.rktuhin.shopflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a favorited product.
 * Separate from ProductEntity to separate user state from cached catalog state.
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val productId: Int,
    val favoritedAt: Long
)
