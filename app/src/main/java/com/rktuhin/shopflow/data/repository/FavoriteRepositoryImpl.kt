package com.rktuhin.shopflow.data.repository

import com.rktuhin.shopflow.data.local.dao.FavoriteDao
import com.rktuhin.shopflow.data.local.dao.ProductDao
import com.rktuhin.shopflow.data.local.entity.FavoriteEntity
import com.rktuhin.shopflow.data.remote.api.ProductApi
import com.rktuhin.shopflow.data.remote.mapper.toProduct
import com.rktuhin.shopflow.data.remote.mapper.toProductEntity
import com.rktuhin.shopflow.domain.model.Product
import com.rktuhin.shopflow.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val productDao: ProductDao,
    private val productApi: ProductApi
) : FavoriteRepository {

    override fun observeFavoriteState(productId: Int): Flow<Boolean> {
        return favoriteDao.observeFavoriteByProductId(productId).map { it != null }
    }

    override fun getFavoriteProducts(): Flow<List<Product>> {
        return favoriteDao.observeFavoriteProducts().map { list ->
            list.map { it.toProduct() }
        }
    }

    override suspend fun addFavorite(productId: Int) {
        val existingProduct = productDao.getProductByIdSync(productId)
        if (existingProduct == null) {
            val productDto = productApi.getProduct(productId)
            productDao.upsertAll(listOf(productDto.toProductEntity()))
        }
        favoriteDao.upsertFavorite(
            FavoriteEntity(
                productId = productId,
                favoritedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun removeFavorite(productId: Int) {
        favoriteDao.removeFavorite(productId)
    }
}
