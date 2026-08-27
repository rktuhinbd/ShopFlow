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

import com.rktuhin.shopflow.data.local.dao.CacheContextDao
import com.rktuhin.shopflow.data.local.entity.CacheContextEntity
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ProductEntity::class,
        FavoriteEntity::class,
        CategoryEntity::class,
        RemoteKeyEntity::class,
        CacheContextEntity::class
    ],
    version = 2,
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

    abstract fun cacheContextDao(): CacheContextDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Create cache_context table
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `cache_context` (" +
                            "`query` TEXT NOT NULL, " +
                            "`lastUpdated` INTEGER NOT NULL, " +
                            "PRIMARY KEY(`query`))"
                )

                // 2. Recreate remote_keys using v2 schema (prevKey, nextKey as skip offsets, drop currentPage/createdAt)
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `remote_keys_new` (" +
                            "`productId` INTEGER NOT NULL, " +
                            "`query` TEXT NOT NULL, " +
                            "`prevKey` INTEGER, " +
                            "`nextKey` INTEGER, " +
                            "PRIMARY KEY(`productId`, `query`))"
                )

                // 3. Migrate data: reset obsolete page indices to NULL, and map legacy "" context to "ALL"
                db.execSQL(
                    "INSERT INTO `remote_keys_new` (`productId`, `query`, `prevKey`, `nextKey`) " +
                            "SELECT `productId`, CASE WHEN `query` = '' THEN 'ALL' ELSE `query` END, NULL, NULL FROM `remote_keys`"
                )

                // 4. Swap tables
                db.execSQL("DROP TABLE `remote_keys`")
                db.execSQL("ALTER TABLE `remote_keys_new` RENAME TO `remote_keys`")
            }
        }
    }
}
