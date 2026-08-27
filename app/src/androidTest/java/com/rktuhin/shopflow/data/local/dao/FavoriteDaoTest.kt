package com.rktuhin.shopflow.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rktuhin.shopflow.data.local.ShopFlowDatabase
import com.rktuhin.shopflow.data.local.entity.FavoriteEntity
import com.rktuhin.shopflow.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteDaoTest {

    private lateinit var database: ShopFlowDatabase
    private lateinit var favoriteDao: FavoriteDao
    private lateinit var productDao: ProductDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShopFlowDatabase::class.java
        ).allowMainThreadQueries().build()
        favoriteDao = database.favoriteDao()
        productDao = database.productDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun upsertFavorite_insertsAndUpdates() = runTest {
        val fav = FavoriteEntity(productId = 1, favoritedAt = 1000L)
        favoriteDao.upsertFavorite(fav)

        val inserted = favoriteDao.observeFavoriteByProductId(1).first()
        assertEquals(1000L, inserted?.favoritedAt)

        val favUpdated = FavoriteEntity(productId = 1, favoritedAt = 2000L)
        favoriteDao.upsertFavorite(favUpdated)

        val updated = favoriteDao.observeFavoriteByProductId(1).first()
        assertEquals(2000L, updated?.favoritedAt)
    }

    @Test
    fun removeFavorite_removesOnlyTargetFavorite() = runTest {
        favoriteDao.upsertFavorite(FavoriteEntity(1, 1000L))
        favoriteDao.upsertFavorite(FavoriteEntity(2, 2000L))

        favoriteDao.removeFavorite(1)

        val fav1 = favoriteDao.observeFavoriteByProductId(1).first()
        val fav2 = favoriteDao.observeFavoriteByProductId(2).first()

        assertNull(fav1)
        assertEquals(2, fav2?.productId)
    }

    @Test
    fun observeAllFavorites_returnsOrderedList() = runTest {
        val fav1 = FavoriteEntity(1, 1000L)
        val fav2 = FavoriteEntity(2, 3000L)
        val fav3 = FavoriteEntity(3, 2000L)

        favoriteDao.upsertFavorite(fav1)
        favoriteDao.upsertFavorite(fav2)
        favoriteDao.upsertFavorite(fav3)

        val list = favoriteDao.observeAllFavorites().first()
        assertEquals(listOf(fav2, fav3, fav1), list) // DESC order of favoritedAt
    }

    @Test
    fun observeFavoriteByProductId_emitsCorrectly() = runTest {
        favoriteDao.upsertFavorite(FavoriteEntity(1, 1000L))
        
        val emitted = favoriteDao.observeFavoriteByProductId(1).first()
        assertEquals(1, emitted?.productId)
    }

    @Test
    fun favorite_survivesProductDeletion() = runTest {
        val product = ProductEntity(
            id = 1, title = "Title", description = "Desc", category = "Cat", price = 10.0,
            discountPercentage = 0.0, rating = 4.0, stock = 10, tags = emptyList(), brand = "Brand",
            sku = "SKU", weight = 1, dimensionWidth = 1.0, dimensionHeight = 1.0, dimensionDepth = 1.0,
            warrantyInformation = "1", shippingInformation = "1", availabilityStatus = "1",
            reviews = emptyList(), returnPolicy = "1", minimumOrderQuantity = 1, images = emptyList(),
            thumbnail = "thumb.jpg", cachedAt = System.currentTimeMillis()
        )
        productDao.upsertAll(listOf(product))

        val fav = FavoriteEntity(productId = 1, favoritedAt = 1000L)
        favoriteDao.upsertFavorite(fav)

        productDao.clearAll() // Or delete product

        val retrievedFav = favoriteDao.observeFavoriteByProductId(1).first()
        assertEquals(1, retrievedFav?.productId)
    }
}
