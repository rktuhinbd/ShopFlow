package com.rktuhin.shopflow.data.repository

import androidx.paging.PagingSource
import com.rktuhin.shopflow.data.local.dao.CacheContextDao
import com.rktuhin.shopflow.data.local.dao.CategoryDao
import com.rktuhin.shopflow.data.local.dao.ProductDao
import com.rktuhin.shopflow.data.local.dao.RemoteKeyDao
import com.rktuhin.shopflow.data.local.entity.CacheContextEntity
import com.rktuhin.shopflow.data.local.entity.CategoryEntity
import com.rktuhin.shopflow.data.local.entity.ProductEntity
import com.rktuhin.shopflow.data.local.entity.RemoteKeyEntity
import com.rktuhin.shopflow.data.paging.ProductPagerFactory
import com.rktuhin.shopflow.data.remote.api.ProductApi
import com.rktuhin.shopflow.data.remote.dto.CategoryDto
import com.rktuhin.shopflow.data.remote.dto.ProductDto
import com.rktuhin.shopflow.data.remote.dto.ProductResponseDto
import androidx.paging.testing.asSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductRepositoryImplTest {

    private fun createRepository(
        api: ProductApi = FakeProductApiRepo(),
        productDao: ProductDao = FakeProductDaoRepo(),
        categoryDao: CategoryDao = FakeCategoryDao()
    ): ProductRepositoryImpl {
        val db = FakeShopFlowDatabase(productDao, categoryDao)
        val pagerFactory = ProductPagerFactory(api, db)
        return ProductRepositoryImpl(api, productDao, categoryDao, pagerFactory)
    }

    @Test
    fun `getProductById returns cached product`() = runTest {
        val fakeProductDao = FakeProductDaoRepo()
        val repository = createRepository(productDao = fakeProductDao)

        fakeProductDao.upsertAll(listOf(createDummyProductEntity(1, "Phone")))

        val product = repository.getProductById(1).first()
        assertEquals("Phone", product?.title)
    }

    @Test
    fun `fetchProduct calls API and caches`() = runTest {
        val fakeProductDao = FakeProductDaoRepo()
        val fakeApi = FakeProductApiRepo()
        val repository = createRepository(api = fakeApi, productDao = fakeProductDao)

        fakeApi.productToReturn = createDummyProductDto(2, "Remote Phone")

        repository.fetchProduct(2)

        assertEquals(1, fakeApi.getProductCallCount)
        val cached = fakeProductDao.getProductByIdSync(2)
        assertEquals("Remote Phone", cached?.title)
    }

    @Test
    fun `getCategories returns cached categories`() = runTest {
        val fakeCategoryDao = FakeCategoryDao()
        val repository = createRepository(categoryDao = fakeCategoryDao)

        fakeCategoryDao.upsertAll(listOf(CategoryEntity(slug = "cat1", name = "Category 1", url = "", cachedAt = 0L)))

        val cats = repository.getCategories().first()
        assertEquals(1, cats.size)
        assertEquals("cat1", cats[0].slug)
    }

    @Test
    fun `fetchCategories calls API and caches`() = runTest {
        val fakeCategoryDao = FakeCategoryDao()
        val fakeApi = FakeProductApiRepo()
        val repository = createRepository(api = fakeApi, categoryDao = fakeCategoryDao)

        fakeApi.categoriesToReturn = listOf(CategoryDto(slug = "cat2", name = "Category 2", url = ""))

        repository.fetchCategories()

        assertEquals(1, fakeApi.getCategoriesCallCount)
        val cached = fakeCategoryDao.categories.value
        assertEquals(1, cached.size)
        assertEquals("cat2", cached[0].slug)
    }

    @Test
    fun `getProducts uses ALL context and maps entities`() = runTest {
        val fakeProductDao = FakeProductDaoRepo()
        val repository = createRepository(productDao = fakeProductDao)

        fakeProductDao.upsertAll(listOf(createDummyProductEntity(1, "Product 1")))

        val snapshot = repository.getProducts().asSnapshot()
        
        assertEquals("ALL", fakeProductDao.observedContext)
        assertEquals(1, snapshot.size)
        assertEquals("Product 1", snapshot[0].title)
    }

    @Test
    fun `getProductsByCategory uses CATEGORY context and maps entities`() = runTest {
        val fakeProductDao = FakeProductDaoRepo()
        val repository = createRepository(productDao = fakeProductDao)

        fakeProductDao.upsertAll(listOf(createDummyProductEntity(2, "Category Product")))

        val snapshot = repository.getProductsByCategory("smartphones").asSnapshot()

        assertEquals("CATEGORY:smartphones", fakeProductDao.observedContext)
        assertEquals(1, snapshot.size)
        assertEquals("Category Product", snapshot[0].title)
    }

    @Test
    fun `searchProducts delegates to API and maps`() = runTest {
        val fakeApi = FakeProductApiRepo()
        fakeApi.searchResponseToReturn = ProductResponseDto(
            products = listOf(createDummyProductDto(3, "Search Result")),
            total = 1,
            skip = 0,
            limit = 20
        )
        val repository = createRepository(api = fakeApi)

        val snapshot = repository.searchProducts("search query").asSnapshot()

        assertEquals(1, fakeApi.searchProductsCallCount)
        assertEquals("search query", fakeApi.lastSearchQuery)
        assertEquals(1, snapshot.size)
        assertEquals("Search Result", snapshot[0].title)
    }
}

class FakeProductDaoRepo : ProductDao {
    val products = MutableStateFlow<List<ProductEntity>>(emptyList())

    override suspend fun upsertAll(entities: List<ProductEntity>) {
        products.update { current ->
            val map = current.associateBy { it.id }.toMutableMap()
            entities.forEach { map[it.id] = it }
            map.values.toList()
        }
    }

    var observedContext: String? = null
    override fun observeProductsByContext(query: String): androidx.paging.PagingSource<Int, ProductEntity> {
        observedContext = query
        return object : androidx.paging.PagingSource<Int, ProductEntity>() {
            override fun getRefreshKey(state: androidx.paging.PagingState<Int, ProductEntity>): Int? = null
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductEntity> {
                return LoadResult.Page(
                    data = products.value,
                    prevKey = null,
                    nextKey = null
                )
            }
        }
    }

    override fun observeProductById(id: Int): Flow<ProductEntity?> {
        return products.map { list -> list.find { it.id == id } }
    }

    override suspend fun getProductByIdSync(id: Int): ProductEntity? {
        return products.value.find { it.id == id }
    }
}

class FakeCategoryDao : CategoryDao {
    val categories = MutableStateFlow<List<CategoryEntity>>(emptyList())

    override suspend fun upsertAll(entities: List<CategoryEntity>) {
        categories.value = entities
    }

    override fun observeAllCategories(): Flow<List<CategoryEntity>> {
        return categories
    }

    override suspend fun clearAll() {}
}

class FakeRemoteKeyDao : RemoteKeyDao {
    override suspend fun upsertAll(remoteKeys: List<RemoteKeyEntity>) {}
    override suspend fun getRemoteKey(productId: Int, query: String): RemoteKeyEntity? = null
    override suspend fun clearRemoteKeys(query: String) {}
}

class FakeCacheContextDao : CacheContextDao {
    override suspend fun getContext(query: String): CacheContextEntity? {
        return CacheContextEntity(query, System.currentTimeMillis())
    }
    override suspend fun upsert(context: CacheContextEntity) {}
}

class FakeProductApiRepo : ProductApi {
    var getProductCallCount = 0
    var getCategoriesCallCount = 0
    var searchProductsCallCount = 0
    var lastSearchQuery: String? = null
    
    var productToReturn: ProductDto? = null
    var categoriesToReturn: List<CategoryDto> = emptyList()
    var searchResponseToReturn: ProductResponseDto? = null

    override suspend fun getProducts(limit: Int, skip: Int): ProductResponseDto {
        return ProductResponseDto(emptyList(), 0, skip, limit)
    }
    
    override suspend fun getProductsByCategory(categorySlug: String, limit: Int, skip: Int): ProductResponseDto {
        return ProductResponseDto(emptyList(), 0, skip, limit)
    }
    
    override suspend fun searchProducts(query: String, limit: Int, skip: Int): ProductResponseDto {
        searchProductsCallCount++
        lastSearchQuery = query
        return searchResponseToReturn ?: ProductResponseDto(emptyList(), 0, 0, 20)
    }

    override suspend fun getProduct(id: Int): ProductDto {
        getProductCallCount++
        return productToReturn ?: throw Exception("Not found")
    }

    override suspend fun getCategories(): List<CategoryDto> {
        getCategoriesCallCount++
        return categoriesToReturn
    }
}
