package com.rktuhin.shopflow.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rktuhin.shopflow.data.local.ShopFlowDatabase
import com.rktuhin.shopflow.data.local.entity.ProductEntity
import com.rktuhin.shopflow.data.local.entity.RemoteKeyEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {

    private lateinit var database: ShopFlowDatabase
    private lateinit var productDao: ProductDao
    private lateinit var remoteKeyDao: RemoteKeyDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShopFlowDatabase::class.java
        ).allowMainThreadQueries().build()
        productDao = database.productDao()
        remoteKeyDao = database.remoteKeyDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    private fun createProduct(id: Int, title: String = "Product $id", category: String = "category_a"): ProductEntity {
        return ProductEntity(
            id = id,
            title = title,
            description = "Description $id",
            category = category,
            price = 10.0,
            discountPercentage = 0.0,
            rating = 4.5,
            stock = 100,
            tags = emptyList(),
            brand = "Brand",
            sku = "SKU$id",
            weight = 1,
            dimensionWidth = 1.0,
            dimensionHeight = 1.0,
            dimensionDepth = 1.0,
            warrantyInformation = "1 year",
            shippingInformation = "Ships in 1 month",
            availabilityStatus = "In Stock",
            reviews = emptyList(),
            returnPolicy = "No return",
            minimumOrderQuantity = 1,
            images = emptyList(),
            thumbnail = "thumb.jpg",
            cachedAt = System.currentTimeMillis()
        )
    }

    @Test
    fun upsertAll_insertsAndUpdatesProducts() = runTest {
        val product1 = createProduct(1, title = "Original")
        productDao.upsertAll(listOf(product1))

        val product1Updated = createProduct(1, title = "Updated")
        productDao.upsertAll(listOf(product1Updated))

        val emitted = productDao.observeProductById(1).first()
        assertEquals("Updated", emitted?.title)
    }

    @Test
    fun observeProductsByContext_returnsOnlyMembershipProducts() = runTest {
        val p1 = createProduct(1)
        val p2 = createProduct(2)
        val p3 = createProduct(3)
        productDao.upsertAll(listOf(p2, p1, p3))

        // Assign p1 and p2 to ALL, and p2 and p3 to CATEGORY:smartphones
        remoteKeyDao.upsertAll(listOf(
            RemoteKeyEntity(1, "ALL", null, null),
            RemoteKeyEntity(2, "ALL", null, null),
            RemoteKeyEntity(2, "CATEGORY:smartphones", null, null),
            RemoteKeyEntity(3, "CATEGORY:smartphones", null, null)
        ))

        val pagingSourceAll = productDao.observeProductsByContext("ALL")
        val resultAll = pagingSourceAll.load(PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false))
        assertTrue(resultAll is PagingSource.LoadResult.Page)
        assertEquals(listOf(p1, p2), (resultAll as PagingSource.LoadResult.Page).data)

        val pagingSourceCat = productDao.observeProductsByContext("CATEGORY:smartphones")
        val resultCat = pagingSourceCat.load(PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false))
        assertTrue(resultCat is PagingSource.LoadResult.Page)
        assertEquals(listOf(p2, p3), (resultCat as PagingSource.LoadResult.Page).data)
    }

}
