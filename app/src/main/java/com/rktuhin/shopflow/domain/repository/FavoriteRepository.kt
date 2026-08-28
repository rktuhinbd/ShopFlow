package com.rktuhin.shopflow.domain.repository

import com.rktuhin.shopflow.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    
    /**
     * Observes the favorite status of a single product.
     */
    fun observeFavoriteState(productId: Int): Flow<Boolean>
    
    /**
     * Observes the complete list of favorited products.
     * The implementation will combine FavoriteEntity state with Product records.
     */
    fun getFavoriteProducts(): Flow<List<Product>>
    
    /**
     * Adds a product to the favorites list.
     */
    suspend fun addFavorite(productId: Int)
    
    /**
     * Removes a product from the favorites list.
     */
    suspend fun removeFavorite(productId: Int)
}
