package com.rktuhin.shopflow.data.remote.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.rktuhin.shopflow.data.local.ShopFlowDatabase
import com.rktuhin.shopflow.data.local.entity.CacheContextEntity
import com.rktuhin.shopflow.data.local.entity.ProductEntity
import com.rktuhin.shopflow.data.local.entity.RemoteKeyEntity
import com.rktuhin.shopflow.data.remote.api.ProductApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit

@OptIn(ExperimentalPagingApi::class)
@RunWith(AndroidJUnit4::class)
class ProductRemoteMediatorTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: ProductApi
    private lateinit var db: ShopFlowDatabase

    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val contentType = "application/json".toMediaType()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        api = retrofit.create(ProductApi::class.java)

        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShopFlowDatabase::class.java
        ).build()
    }

    @After
    fun teardown() {
        if (::db.isInitialized) db.close()
        if (::mockWebServer.isInitialized) mockWebServer.shutdown()
    }

    // --- INITIALIZE TESTS ---
    @Test
    fun initialize_missingContext_returnsLaunchInitialRefresh() = runBlocking {
        val mediator = ProductRemoteMediator("ALL", api, db)
        val result = mediator.initialize()
        assertEquals(RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH, result)
        assertNull(db.cacheContextDao().getContext("ALL")) // Should not mutate
    }

    @Test
    fun initialize_freshContext_returnsSkipInitialRefresh() = runBlocking {
        val mediator = ProductRemoteMediator("ALL", api, db)
        db.cacheContextDao().upsert(CacheContextEntity("ALL", System.currentTimeMillis()))
        val result = mediator.initialize()
        assertEquals(RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH, result)
    }

    @Test
    fun initialize_staleContext_returnsLaunchInitialRefresh() = runBlocking {
        val mediator = ProductRemoteMediator("ALL", api, db)
        val staleTime = System.currentTimeMillis() - (16 * 60 * 1000) // 16 mins old
        db.cacheContextDao().upsert(CacheContextEntity("ALL", staleTime))
        val result = mediator.initialize()
        assertEquals(RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH, result)
    }

    @Test
    fun initialize_futureTimestamp_returnsSkipInitialRefresh() = runBlocking {
        val mediator = ProductRemoteMediator("ALL", api, db)
        val futureTime = System.currentTimeMillis() + (10 * 60 * 1000) // 10 mins in future
        db.cacheContextDao().upsert(CacheContextEntity("ALL", futureTime))
        val result = mediator.initialize()
        assertEquals(RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH, result)
    }

    // --- PREPEND TEST ---
    @Test
    fun prepend_returnsSuccessEndOfPagination() = runBlocking {
        val mediator = ProductRemoteMediator("ALL", api, db)
        val state = PagingState<Int, ProductEntity>(listOf(), null, PagingConfig(10), 0)
        val result = mediator.load(LoadType.PREPEND, state)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        assertEquals(0, mockWebServer.requestCount)
    }

    // --- REFRESH REPLACEMENT SEMANTICS (MANDATORY) ---
    @Test
    fun refresh_replacesMembershipForCurrentContextOnly() = runBlocking {
        val context = "CATEGORY:smartphones"
        val otherContext = "ALL"
        
        // Seed database
        val p1 = createMockEntity(1)
        val p2 = createMockEntity(2)
        val p3 = createMockEntity(3)
        val p4 = createMockEntity(4)
        db.productDao().upsertAll(listOf(p1, p2, p3, p4))
        
        db.remoteKeyDao().upsertAll(listOf(
            RemoteKeyEntity(1, context, null, null),
            RemoteKeyEntity(2, context, null, null),
            RemoteKeyEntity(3, context, null, null),
            RemoteKeyEntity(4, context, null, null),
            RemoteKeyEntity(4, otherContext, null, null) // p4 also in ALL
        ))

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(
            """{
              "products": [{"id": 1, "title": "P1", "description": "", "category": "smartphones", "price": 0.0, "discountPercentage": 0.0, "rating": 0.0, "stock": 0, "tags": [], "brand": "", "sku": "", "weight": 0, "dimensions": {"width": 0.0, "height": 0.0, "depth": 0.0}, "warrantyInformation": "", "shippingInformation": "", "availabilityStatus": "", "reviews": [], "returnPolicy": "", "minimumOrderQuantity": 0, "meta": {"createdAt": "", "updatedAt": "", "barcode": "", "qrCode": ""}, "images": [], "thumbnail": ""},
                           {"id": 2, "title": "P2", "description": "", "category": "smartphones", "price": 0.0, "discountPercentage": 0.0, "rating": 0.0, "stock": 0, "tags": [], "brand": "", "sku": "", "weight": 0, "dimensions": {"width": 0.0, "height": 0.0, "depth": 0.0}, "warrantyInformation": "", "shippingInformation": "", "availabilityStatus": "", "reviews": [], "returnPolicy": "", "minimumOrderQuantity": 0, "meta": {"createdAt": "", "updatedAt": "", "barcode": "", "qrCode": ""}, "images": [], "thumbnail": ""}],
              "total": 2, "skip": 0, "limit": 2
            }"""
        ))

        val mediator = ProductRemoteMediator(context, api, db)
        val state = PagingState<Int, ProductEntity>(listOf(), null, PagingConfig(2), 0)
        
        val result = mediator.load(LoadType.REFRESH, state)
        assertTrue(result is RemoteMediator.MediatorResult.Success)

        // Verify products 3 and 4 are no longer in CATEGORY:smartphones membership
        assertNull(db.remoteKeyDao().getRemoteKey(3, context))
        assertNull(db.remoteKeyDao().getRemoteKey(4, context))
        
        // Verify product 4 is STILL in ALL context membership
        assertNotNull(db.remoteKeyDao().getRemoteKey(4, otherContext))

        // Verify products 3 and 4 are still physically in database
        assertNotNull(db.productDao().observeProductById(3).first())
        assertNotNull(db.productDao().observeProductById(4).first())
    }

    // --- SHARED PRODUCTENTITY TEST (MANDATORY) ---
    @Test
    fun refresh_updatesSharedProductEntityWithoutLosingOtherContextMembership() = runBlocking {
        val p1 = createMockEntity(1, title = "Old Title")
        db.productDao().upsertAll(listOf(p1))
        
        db.remoteKeyDao().upsertAll(listOf(
            RemoteKeyEntity(1, "ALL", null, null),
            RemoteKeyEntity(1, "CATEGORY:smartphones", null, null)
        ))

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(
            """{
              "products": [{"id": 1, "title": "New Title", "description": "", "category": "smartphones", "price": 0.0, "discountPercentage": 0.0, "rating": 0.0, "stock": 0, "tags": [], "brand": "", "sku": "", "weight": 0, "dimensions": {"width": 0.0, "height": 0.0, "depth": 0.0}, "warrantyInformation": "", "shippingInformation": "", "availabilityStatus": "", "reviews": [], "returnPolicy": "", "minimumOrderQuantity": 0, "meta": {"createdAt": "", "updatedAt": "", "barcode": "", "qrCode": ""}, "images": [], "thumbnail": ""}],
              "total": 1, "skip": 0, "limit": 1
            }"""
        ))

        val mediator = ProductRemoteMediator("CATEGORY:smartphones", api, db)
        val state = PagingState<Int, ProductEntity>(listOf(), null, PagingConfig(1), 0)
        mediator.load(LoadType.REFRESH, state)

        // Product identity updated
        val updatedProduct = db.productDao().observeProductById(1).first()!!
        assertEquals("New Title", updatedProduct.title)

        // Memberships remain
        assertNotNull(db.remoteKeyDao().getRemoteKey(1, "ALL"))
        assertNotNull(db.remoteKeyDao().getRemoteKey(1, "CATEGORY:smartphones"))
    }

    // --- FAILED REFRESH FRESHNESS TEST (MANDATORY) ---
    @Test
    fun refresh_failurePreservesAllLocalState() = runBlocking {
        val context = "ALL"
        db.productDao().upsertAll(listOf(createMockEntity(1)))
        db.remoteKeyDao().upsertAll(listOf(RemoteKeyEntity(1, context, null, null)))
        val oldTime = 1000L
        db.cacheContextDao().upsert(CacheContextEntity(context, oldTime))

        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val mediator = ProductRemoteMediator(context, api, db)
        val state = PagingState<Int, ProductEntity>(listOf(), null, PagingConfig(1), 0)
        val result = mediator.load(LoadType.REFRESH, state)

        assertTrue(result is RemoteMediator.MediatorResult.Error)

        assertNotNull(db.productDao().observeProductById(1).first())
        assertNotNull(db.remoteKeyDao().getRemoteKey(1, context))
        assertEquals(oldTime, db.cacheContextDao().getContext(context)?.lastUpdated)
    }

    // --- APPEND CONTEXT TEST (MANDATORY) ---
    @Test
    fun append_usesContextAwareRemoteKeyLookup() = runBlocking {
        db.productDao().upsertAll(listOf(createMockEntity(1)))
        db.remoteKeyDao().upsertAll(listOf(
            RemoteKeyEntity(1, "ALL", null, 20),
            RemoteKeyEntity(1, "CATEGORY:smartphones", null, 40)
        ))

        // Return empty so it doesn't try to parse real data and succeeds end of pagination
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"products": [], "total": 100, "skip": 40, "limit": 10}"""))

        val mediator = ProductRemoteMediator("CATEGORY:smartphones", api, db)
        val page = androidx.paging.PagingSource.LoadResult.Page<Int, ProductEntity>(
            data = listOf(createMockEntity(1)),
            prevKey = null,
            nextKey = null
        )
        val state = PagingState(listOf(page), null, PagingConfig(10), 0)

        mediator.load(LoadType.APPEND, state)

        val request = mockWebServer.takeRequest()
        // verify request used skip=40
        assertTrue(request.path!!.contains("skip=40"))
    }

    // --- EMPTY CONTEXT TEST ---
    @Test
    fun refresh_emptyContext_succeedsAndSetsFreshness() = runBlocking {
        val context = "CATEGORY:empty"
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("""{"products": [], "total": 0, "skip": 0, "limit": 10}"""))

        val mediator = ProductRemoteMediator(context, api, db)
        val state = PagingState<Int, ProductEntity>(listOf(), null, PagingConfig(10), 0)
        val result = mediator.load(LoadType.REFRESH, state)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        assertNotNull(db.cacheContextDao().getContext(context))
    }

    // --- ALL REFRESH TEST (MANDATORY) ---
    @Test
    fun refresh_allContext_success_insertsProductsAndKeys() = runBlocking {
        val context = "ALL"
        
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(
            """{
              "products": [{"id": 1, "title": "P1", "description": "", "category": "smartphones", "price": 0.0, "discountPercentage": 0.0, "rating": 0.0, "stock": 0, "tags": [], "brand": "", "sku": "", "weight": 0, "dimensions": {"width": 0.0, "height": 0.0, "depth": 0.0}, "warrantyInformation": "", "shippingInformation": "", "availabilityStatus": "", "reviews": [], "returnPolicy": "", "minimumOrderQuantity": 0, "meta": {"createdAt": "", "updatedAt": "", "barcode": "", "qrCode": ""}, "images": [], "thumbnail": ""}],
              "total": 100, "skip": 0, "limit": 1
            }"""
        ))

        val mediator = ProductRemoteMediator(context, api, db)
        val state = PagingState<Int, ProductEntity>(listOf(), null, PagingConfig(1), 0)
        
        val result = mediator.load(LoadType.REFRESH, state)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertTrue(request.path!!.contains("/products"))
        assertTrue(request.path!!.contains("skip=0"))
        assertTrue(request.path!!.contains("limit=1"))

        // Assert database persistence
        assertNotNull(db.productDao().observeProductById(1).first())
        val key = db.remoteKeyDao().getRemoteKey(1, context)
        assertNotNull(key)
        assertNull(key!!.prevKey)
        assertEquals(1, key.nextKey) // skip(0) + size(1)
        
        val cacheContext = db.cacheContextDao().getContext(context)
        assertNotNull(cacheContext)
        assertTrue(cacheContext!!.lastUpdated > 0L)
    }

    // --- APPEND SUCCESS NON-FINAL TEST ---
    @Test
    fun append_success_setsNextKeyForNonFinalPage() = runBlocking {
        val context = "ALL"
        // Seed initial state
        db.productDao().upsertAll(listOf(createMockEntity(1)))
        db.remoteKeyDao().upsertAll(listOf(
            RemoteKeyEntity(1, context, null, 20)
        ))

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(
            """{
              "products": [{"id": 2, "title": "P2", "description": "", "category": "smartphones", "price": 0.0, "discountPercentage": 0.0, "rating": 0.0, "stock": 0, "tags": [], "brand": "", "sku": "", "weight": 0, "dimensions": {"width": 0.0, "height": 0.0, "depth": 0.0}, "warrantyInformation": "", "shippingInformation": "", "availabilityStatus": "", "reviews": [], "returnPolicy": "", "minimumOrderQuantity": 0, "meta": {"createdAt": "", "updatedAt": "", "barcode": "", "qrCode": ""}, "images": [], "thumbnail": ""}],
              "total": 100, "skip": 20, "limit": 20
            }"""
        ))

        val mediator = ProductRemoteMediator(context, api, db)
        val page = androidx.paging.PagingSource.LoadResult.Page<Int, ProductEntity>(
            data = listOf(createMockEntity(1)),
            prevKey = null,
            nextKey = null
        )
        val state = PagingState(listOf(page), null, PagingConfig(20), 0)

        val result = mediator.load(LoadType.APPEND, state)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertTrue(request.path!!.contains("skip=20"))

        assertNotNull(db.productDao().observeProductById(2).first())
        val key = db.remoteKeyDao().getRemoteKey(2, context)
        assertNotNull(key)
        assertEquals(21, key!!.nextKey) // skip(20) + size(1)
    }

    // --- APPEND SUCCESS FINAL PAGE TEST ---
    @Test
    fun append_success_returnsEndOfPaginationForFinalPage() = runBlocking {
        val context = "ALL"
        db.productDao().upsertAll(listOf(createMockEntity(1)))
        db.remoteKeyDao().upsertAll(listOf(
            RemoteKeyEntity(1, context, null, 80)
        ))

        // Return a response where skip + size >= total
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(
            """{
              "products": [{"id": 2, "title": "P2", "description": "", "category": "smartphones", "price": 0.0, "discountPercentage": 0.0, "rating": 0.0, "stock": 0, "tags": [], "brand": "", "sku": "", "weight": 0, "dimensions": {"width": 0.0, "height": 0.0, "depth": 0.0}, "warrantyInformation": "", "shippingInformation": "", "availabilityStatus": "", "reviews": [], "returnPolicy": "", "minimumOrderQuantity": 0, "meta": {"createdAt": "", "updatedAt": "", "barcode": "", "qrCode": ""}, "images": [], "thumbnail": ""}],
              "total": 81, "skip": 80, "limit": 20
            }"""
        ))

        val mediator = ProductRemoteMediator(context, api, db)
        val page = androidx.paging.PagingSource.LoadResult.Page<Int, ProductEntity>(
            data = listOf(createMockEntity(1)),
            prevKey = null,
            nextKey = null
        )
        val state = PagingState(listOf(page), null, PagingConfig(20), 0)

        val result = mediator.load(LoadType.APPEND, state)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        assertNotNull(db.productDao().observeProductById(2).first())
        val key = db.remoteKeyDao().getRemoteKey(2, context)
        assertNotNull(key)
        assertNull(key!!.nextKey) // End of pagination
    }

    // --- IOEXCEPTION TEST ---
    @Test
    fun load_networkFailure_returnsErrorResult_fromIOException() = runBlocking {
        val context = "ALL"
        db.productDao().upsertAll(listOf(createMockEntity(1)))
        db.remoteKeyDao().upsertAll(listOf(RemoteKeyEntity(1, context, null, 20)))
        val oldTime = 1000L
        db.cacheContextDao().upsert(CacheContextEntity(context, oldTime))

        // Simulate network failure
        val response = MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START)
        mockWebServer.enqueue(response)

        val mediator = ProductRemoteMediator(context, api, db)
        val state = PagingState<Int, ProductEntity>(listOf(), null, PagingConfig(20), 0)
        
        val result = mediator.load(LoadType.REFRESH, state)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
        assertTrue((result as RemoteMediator.MediatorResult.Error).throwable is java.io.IOException)

        // Verify cache preservation
        assertNotNull(db.productDao().observeProductById(1).first())
        assertNotNull(db.remoteKeyDao().getRemoteKey(1, context))
        assertEquals(oldTime, db.cacheContextDao().getContext(context)?.lastUpdated)
    }

    // --- SERIALIZATION EXCEPTION TEST ---
    @Test
    fun load_malformedJson_returnsErrorResult_fromSerializationException() = runBlocking {
        val context = "ALL"
        db.productDao().upsertAll(listOf(createMockEntity(1)))
        db.remoteKeyDao().upsertAll(listOf(RemoteKeyEntity(1, context, null, 20)))
        val oldTime = 1000L
        db.cacheContextDao().upsert(CacheContextEntity(context, oldTime))

        // Return malformed JSON
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{ \"invalid\": JSON }"))

        val mediator = ProductRemoteMediator(context, api, db)
        val state = PagingState<Int, ProductEntity>(listOf(), null, PagingConfig(20), 0)
        
        val result = mediator.load(LoadType.REFRESH, state)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
        assertTrue((result as RemoteMediator.MediatorResult.Error).throwable is kotlinx.serialization.SerializationException)

        // Verify cache preservation
        assertNotNull(db.productDao().observeProductById(1).first())
        assertNotNull(db.remoteKeyDao().getRemoteKey(1, context))
        assertEquals(oldTime, db.cacheContextDao().getContext(context)?.lastUpdated)
    }

    private fun createMockEntity(id: Int, title: String = "Test") = ProductEntity(
        id = id, title = title, description = "", category = "", price = 0.0,
        discountPercentage = 0.0, rating = 0.0, stock = 0, tags = emptyList(),
        brand = "", sku = "", weight = 0, dimensionWidth = 0.0, dimensionHeight = 0.0, dimensionDepth = 0.0,
        warrantyInformation = "", shippingInformation = "", availabilityStatus = "",
        reviews = emptyList(), returnPolicy = "", minimumOrderQuantity = 0,
        images = emptyList(), thumbnail = "", cachedAt = 0L
    )
}
