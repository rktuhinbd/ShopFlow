package com.rktuhin.shopflow.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rktuhin.shopflow.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Upsert
    suspend fun upsertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE productId = :productId")
    suspend fun removeFavorite(productId: Int)

    @Query("SELECT * FROM favorites ORDER BY favoritedAt DESC")
    fun observeAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorites WHERE productId = :productId")
    fun observeFavoriteByProductId(productId: Int): Flow<FavoriteEntity?>
}
