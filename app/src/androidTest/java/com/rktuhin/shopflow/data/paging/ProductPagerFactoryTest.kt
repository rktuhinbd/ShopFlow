package com.rktuhin.shopflow.data.paging

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rktuhin.shopflow.data.local.ShopFlowDatabase
import com.rktuhin.shopflow.data.local.entity.ProductEntity
import com.rktuhin.shopflow.data.local.entity.RemoteKeyEntity
import com.rktuhin.shopflow.data.remote.api.ProductApi
import com.rktuhin.shopflow.data.remote.dto.CategoryDto
import com.rktuhin.shopflow.data.remote.dto.ProductDto
import com.rktuhin.shopflow.data.remote.dto.ProductResponseDto
import com.rktuhin.shopflow.data.remote.mediator.ProductRemoteMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.paging.AsyncPagingDataDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ProductPagerFactoryTest {

    private lateinit var db: ShopFlowDatabase
    private lateinit var api: ProductApi
    private lateinit var factory: ProductPagerFactory

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShopFlowDatabase::class.java
        ).build()

        // Create a dummy api instance for the factory
        api = object : ProductApi {
            override suspend fun getProducts(limit: Int, skip: Int): ProductResponseDto {
                throw NotImplementedError("Not used in test")
            }
            override suspend fun searchProducts(query: String, limit: Int, skip: Int): ProductResponseDto {
                throw NotImplementedError("Not used in test")
            }
            override suspend fun getCategories(): List<CategoryDto> {
                throw NotImplementedError("Not used in test")
            }
            override suspend fun getProductsByCategory(category: String, limit: Int, skip: Int): ProductResponseDto {
                throw NotImplementedError("Not used in test")
            }
            override suspend fun getProduct(id: Int): ProductDto {
                throw NotImplementedError("Not used in test")
            }
        }

        factory = ProductPagerFactory(api, db)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun createPager_withAllContext_succeeds() {
        val pager = factory.createPager(ProductRemoteMediator.ALL_CONTEXT)
        assertNotNull(pager)
    }

    @Test
    fun createPager_withCategoryContext_succeeds() {
        val pager = factory.createPager("CATEGORY:smartphones")
        assertNotNull(pager)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createPager_withEmptyContext_throwsException() {
        factory.createPager("")
    }

    @Test(expected = IllegalArgumentException::class)
    fun createPager_withInvalidPrefixContext_throwsException() {
        factory.createPager("SEARCH:smartphones")
    }

    @Test(expected = IllegalArgumentException::class)
    fun createPager_withEmptyCategorySlug_throwsException() {
        factory.createPager("CATEGORY:")
    }

    @Test(expected = IllegalArgumentException::class)
    fun createPager_withBlankCategorySlug_throwsException() {
        factory.createPager("CATEGORY:   ")
    }
    
    @Test
    fun verify_direct_pagingSource_behavior() = runTest {
        // Seed database
        db.productDao().upsertAll(listOf(
            ProductEntity(
                id = 1,
                title = "All Product",
                description = "",
                category = "other",
                price = 10.0,
                discountPercentage = 0.0,
                rating = 4.0,
                stock = 10,
                tags = emptyList(),
                brand = "",
                sku = "",
                weight = 1,
                dimensionWidth = 1.0,
                dimensionHeight = 1.0,
                dimensionDepth = 1.0,
                warrantyInformation = "",
                shippingInformation = "",
                availabilityStatus = "",
                reviews = emptyList(),
                returnPolicy = "",
                minimumOrderQuantity = 1,
                images = emptyList(),
                thumbnail = "",
                cachedAt = System.currentTimeMillis()
            )
        ))
        db.remoteKeyDao().upsertAll(listOf(
            RemoteKeyEntity(
                productId = 1,
                query = ProductRemoteMediator.ALL_CONTEXT,
                prevKey = null,
                nextKey = null
            )
        ))

        // Direct PagingSource
        val pagingSource = db.productDao().observeProductsByContext(ProductRemoteMediator.ALL_CONTEXT)
        val loadResult = pagingSource.load(
            androidx.paging.PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertNotNull(loadResult)
        org.junit.Assert.assertTrue("LoadResult must be Page", loadResult is androidx.paging.PagingSource.LoadResult.Page)
        val page = loadResult as androidx.paging.PagingSource.LoadResult.Page<Int, ProductEntity>
        assertEquals(1, page.data.size)
        assertEquals("All Product", page.data[0].title)
    }

    @Test
    fun verify_pagingSource_wiring_isolation() = runTest {
        // Seed database with two products belonging to different contexts
        db.productDao().upsertAll(listOf(
            ProductEntity(
                id = 1,
                title = "All Product",
                description = "",
                category = "other",
                price = 10.0,
                discountPercentage = 0.0,
                rating = 4.0,
                stock = 10,
                tags = emptyList(),
                brand = "",
                sku = "",
                weight = 1,
                dimensionWidth = 1.0,
                dimensionHeight = 1.0,
                dimensionDepth = 1.0,
                warrantyInformation = "",
                shippingInformation = "",
                availabilityStatus = "",
                reviews = emptyList(),
                returnPolicy = "",
                minimumOrderQuantity = 1,
                images = emptyList(),
                thumbnail = "",
                cachedAt = System.currentTimeMillis()
            ),
            ProductEntity(
                id = 2,
                title = "Smartphone Product",
                description = "",
                category = "smartphones",
                price = 10.0,
                discountPercentage = 0.0,
                rating = 4.0,
                stock = 10,
                tags = emptyList(),
                brand = "",
                sku = "",
                weight = 1,
                dimensionWidth = 1.0,
                dimensionHeight = 1.0,
                dimensionDepth = 1.0,
                warrantyInformation = "",
                shippingInformation = "",
                availabilityStatus = "",
                reviews = emptyList(),
                returnPolicy = "",
                minimumOrderQuantity = 1,
                images = emptyList(),
                thumbnail = "",
                cachedAt = System.currentTimeMillis()
            )
        ))

        db.remoteKeyDao().upsertAll(listOf(
            RemoteKeyEntity(
                productId = 1,
                query = ProductRemoteMediator.ALL_CONTEXT,
                prevKey = null,
                nextKey = null
            ),
            RemoteKeyEntity(
                productId = 2,
                query = "CATEGORY:smartphones",
                prevKey = null,
                nextKey = null
            )
        ))

        db.cacheContextDao().upsert(
            com.rktuhin.shopflow.data.local.entity.CacheContextEntity(
                query = ProductRemoteMediator.ALL_CONTEXT,
                lastUpdated = System.currentTimeMillis()
            )
        )
        db.cacheContextDao().upsert(
            com.rktuhin.shopflow.data.local.entity.CacheContextEntity(
                query = "CATEGORY:smartphones",
                lastUpdated = System.currentTimeMillis()
            )
        )

        // Create Pagers
        val allPager = factory.createPager(ProductRemoteMediator.ALL_CONTEXT)
        val categoryPager = factory.createPager("CATEGORY:smartphones")

        val testDispatcher = StandardTestDispatcher(testScheduler)

        val differAll = AsyncPagingDataDiffer(
            diffCallback = object : DiffUtil.ItemCallback<ProductEntity>() {
                override fun areItemsTheSame(old: ProductEntity, new: ProductEntity) = old.id == new.id
                override fun areContentsTheSame(old: ProductEntity, new: ProductEntity) = old == new
            },
            updateCallback = object : ListUpdateCallback {
                override fun onInserted(position: Int, count: Int) {}
                override fun onRemoved(position: Int, count: Int) {}
                override fun onMoved(fromPosition: Int, toPosition: Int) {}
                override fun onChanged(position: Int, count: Int, payload: Any?) {}
            },
            mainDispatcher = testDispatcher,
            workerDispatcher = testDispatcher
        )

        val differCat = AsyncPagingDataDiffer(
            diffCallback = object : DiffUtil.ItemCallback<ProductEntity>() {
                override fun areItemsTheSame(old: ProductEntity, new: ProductEntity) = old.id == new.id
                override fun areContentsTheSame(old: ProductEntity, new: ProductEntity) = old == new
            },
            updateCallback = object : ListUpdateCallback {
                override fun onInserted(position: Int, count: Int) {}
                override fun onRemoved(position: Int, count: Int) {}
                override fun onMoved(fromPosition: Int, toPosition: Int) {}
                override fun onChanged(position: Int, count: Int, payload: Any?) {}
            },
            mainDispatcher = testDispatcher,
            workerDispatcher = testDispatcher
        )

        val job1 = launch {
            allPager.flow.collect { differAll.submitData(it) }
        }
        val job2 = launch {
            categoryPager.flow.collect { differCat.submitData(it) }
        }

        // Paging accesses Room on Dispatchers.IO, which is a real thread pool that advanceUntilIdle() cannot fast-forward.
        // We must suspend until the Differ actually receives the data.
        // The initial LoadState is NotLoading with 0 items, so we check for itemCount > 0.
        differAll.loadStateFlow.first { differAll.itemCount > 0 }
        differCat.loadStateFlow.first { differCat.itemCount > 0 }

        assertEquals(1, differAll.itemCount)
        assertEquals("All Product", differAll.snapshot().items[0].title)

        assertEquals(1, differCat.itemCount)
        assertEquals("Smartphone Product", differCat.snapshot().items[0].title)

        job1.cancel()
        job2.cancel()
    }
}
