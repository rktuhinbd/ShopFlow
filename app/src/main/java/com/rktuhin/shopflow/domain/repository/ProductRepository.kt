package com.rktuhin.shopflow.domain.repository

import androidx.paging.PagingData
import com.rktuhin.shopflow.domain.model.Category
import com.rktuhin.shopflow.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    
    /**
     * Observes the paginated product catalog.
     * Maps to the "ALL" cache context.
     */
    fun getProducts(): Flow<PagingData<Product>>
    
    /**
     * Observes a paginated list of products for a specific category.
     * Maps to the "CATEGORY:<slug>" cache context.
     */
    fun getProductsByCategory(categorySlug: String): Flow<PagingData<Product>>
    
    /**
     * Searches for products using a network-only PagingSource (ADR-010).
     */
    fun searchProducts(query: String): Flow<PagingData<Product>>
    
    /**
     * Observes a single product by its ID for the detail screen.
     */
    fun getProductById(id: Int): Flow<Product?>
    
    /**
     * Observes the list of available categories.
     */
    fun getCategories(): Flow<List<Category>>

    /**
     * Explicit network fetch and cache persistence for a product.
     */
    suspend fun fetchProduct(id: Int)

    /**
     * Explicit network fetch and cache persistence for categories.
     */
    suspend fun fetchCategories()
}
