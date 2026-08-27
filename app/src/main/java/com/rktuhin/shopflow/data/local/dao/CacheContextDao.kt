package com.rktuhin.shopflow.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rktuhin.shopflow.data.local.entity.CacheContextEntity

@Dao
interface CacheContextDao {
    @Query("SELECT * FROM cache_context WHERE query = :query")
    suspend fun getContext(query: String): CacheContextEntity?

    @Upsert
    suspend fun upsert(context: CacheContextEntity)
}
