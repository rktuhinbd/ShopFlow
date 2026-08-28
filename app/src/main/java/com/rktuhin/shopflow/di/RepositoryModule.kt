package com.rktuhin.shopflow.di

import com.rktuhin.shopflow.data.repository.FavoriteRepositoryImpl
import com.rktuhin.shopflow.data.repository.ProductRepositoryImpl
import com.rktuhin.shopflow.domain.repository.FavoriteRepository
import com.rktuhin.shopflow.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    abstract fun bindFavoriteRepository(
        favoriteRepositoryImpl: FavoriteRepositoryImpl
    ): FavoriteRepository
}
