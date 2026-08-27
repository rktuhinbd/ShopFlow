package com.rktuhin.shopflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rktuhin.shopflow.data.local.converter.ProductReviewListConverter
import com.rktuhin.shopflow.data.local.converter.StringListConverter
import com.rktuhin.shopflow.data.local.dao.CategoryDao
import com.rktuhin.shopflow.data.local.dao.FavoriteDao
import com.rktuhin.shopflow.data.local.dao.ProductDao
import com.rktuhin.shopflow.data.local.dao.RemoteKeyDao
import com.rktuhin.shopflow.data.local.entity.CategoryEntity
import com.rktuhin.shopflow.data.local.entity.FavoriteEntity
import com.rktuhin.shopflow.data.local.entity.ProductEntity
import com.rktuhin.shopflow.data.local.entity.RemoteKeyEntity

@Database(
    entities = [
        ProductEntity::class,
        FavoriteEntity::class,
        CategoryEntity::class,
        RemoteKeyEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    StringListConverter::class,
    ProductReviewListConverter::class
)
abstract class ShopFlowDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun categoryDao(): CategoryDao

    abstract fun remoteKeyDao(): RemoteKeyDao
}
