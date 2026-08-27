package com.rktuhin.shopflow.di

import android.content.Context
import androidx.room.Room
import com.rktuhin.shopflow.data.local.ShopFlowDatabase
import com.rktuhin.shopflow.data.local.dao.CategoryDao
import com.rktuhin.shopflow.data.local.dao.FavoriteDao
import com.rktuhin.shopflow.data.local.dao.ProductDao
import com.rktuhin.shopflow.data.local.dao.RemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideShopFlowDatabase(
        @ApplicationContext context: Context
    ): ShopFlowDatabase {
        return Room.databaseBuilder(
            context,
            ShopFlowDatabase::class.java,
            "shopflow.db"
        ).build()
    }

    @Provides
    fun provideProductDao(database: ShopFlowDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    fun provideFavoriteDao(database: ShopFlowDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    fun provideCategoryDao(database: ShopFlowDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideRemoteKeyDao(database: ShopFlowDatabase): RemoteKeyDao {
        return database.remoteKeyDao()
    }
}
