package com.rktuhin.shopflow.data.repository

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.rktuhin.shopflow.data.local.ShopFlowDatabase
import com.rktuhin.shopflow.data.local.dao.CacheContextDao
import com.rktuhin.shopflow.data.local.dao.CategoryDao
import com.rktuhin.shopflow.data.local.dao.FavoriteDao
import com.rktuhin.shopflow.data.local.dao.ProductDao
import com.rktuhin.shopflow.data.local.dao.RemoteKeyDao

class FakeShopFlowDatabase(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao
) : ShopFlowDatabase() {
    override fun productDao(): ProductDao = productDao
    override fun favoriteDao(): FavoriteDao = throw NotImplementedError()
    override fun categoryDao(): CategoryDao = categoryDao
    override fun remoteKeyDao(): RemoteKeyDao = FakeRemoteKeyDao()
    override fun cacheContextDao(): CacheContextDao = FakeCacheContextDao()
    
    override fun createInvalidationTracker(): InvalidationTracker {
        return InvalidationTracker(this, "products", "favorites", "categories", "remote_keys", "cache_context")
    }

    override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
        throw NotImplementedError()
    }

    override fun clearAllTables() {
        throw NotImplementedError()
    }
}
