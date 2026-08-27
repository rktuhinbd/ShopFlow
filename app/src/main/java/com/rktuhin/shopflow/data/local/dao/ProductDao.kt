package com.rktuhin.shopflow.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.rktuhin.shopflow.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Upsert
    suspend fun upsertAll(products: List<ProductEntity>)

    @Query("SELECT * FROM products ORDER BY id ASC")
    fun observeAllProducts(): PagingSource<Int, ProductEntity>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY id ASC")
    fun observeProductsByCategory(category: String): PagingSource<Int, ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    fun observeProductById(id: Int): Flow<ProductEntity?>

    @Query("DELETE FROM products")
    suspend fun clearAll()
}
