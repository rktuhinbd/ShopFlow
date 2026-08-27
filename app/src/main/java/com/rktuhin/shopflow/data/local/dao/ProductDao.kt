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

    @Query("""
        SELECT p.* FROM products p
        INNER JOIN remote_keys r ON p.id = r.productId
        WHERE r.query = :query
        ORDER BY p.id ASC
    """)
    fun observeProductsByContext(query: String): PagingSource<Int, ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    fun observeProductById(id: Int): Flow<ProductEntity?>

}
