package com.rktuhin.shopflow.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rktuhin.shopflow.data.local.entity.RemoteKeyEntity

@Dao
interface RemoteKeyDao {

    @Upsert
    suspend fun upsertAll(remoteKeys: List<RemoteKeyEntity>)

    @Query("SELECT * FROM remote_keys WHERE productId = :productId AND query = :query")
    suspend fun getRemoteKey(productId: Int, query: String): RemoteKeyEntity?

    @Query("DELETE FROM remote_keys WHERE query = :query")
    suspend fun clearRemoteKeys(query: String)
}
