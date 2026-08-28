package com.rktuhin.shopflow.data.repository

import com.rktuhin.shopflow.data.local.dao.FavoriteDao
import com.rktuhin.shopflow.data.local.dao.ProductDao
import com.rktuhin.shopflow.data.local.entity.FavoriteEntity
import com.rktuhin.shopflow.data.local.entity.ProductEntity
import com.rktuhin.shopflow.data.remote.api.ProductApi
import com.rktuhin.shopflow.data.remote.dto.CategoryDto
import com.rktuhin.shopflow.data.remote.dto.ProductDto
import com.rktuhin.shopflow.data.remote.dto.ProductResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoriteRepositoryImplTest {

    @Test
    fun `addFavorite when product exists locally`() = runTest {
        val fakeProductDao = FakeProductDao()
        val fakeFavoriteDao = FakeFavoriteDao()
        val fakeApi = FakeProductApi()

        fakeProductDao.upsertAll(
            listOf(createDummyProductEntity(1, "Local"))
        )

        val repository = FavoriteRepositoryImpl(fakeFavoriteDao, fakeProductDao, fakeApi)
        repository.addFavorite(1)

        assertTrue(fakeFavoriteDao.favorites.value.any { it.productId == 1 })
        assertEquals(0, fakeApi.getProductCallCount)
    }

    @Test
    fun `addFavorite when product missing locally fetches from API`() = runTest {
        val fakeProductDao = FakeProductDao()
        val fakeFavoriteDao = FakeFavoriteDao()
        val fakeApi = FakeProductApi()
        
        fakeApi.productToReturn = createDummyProductDto(2, "Remote")

        val repository = FavoriteRepositoryImpl(fakeFavoriteDao, fakeProductDao, fakeApi)
        repository.addFavorite(2)

        assertTrue(fakeFavoriteDao.favorites.value.any { it.productId == 2 })
        assertEquals(1, fakeApi.getProductCallCount)
        assertEquals("Remote", fakeProductDao.products.value.find { it.id == 2 }?.title)
    }

    @Test
    fun `observeFavoriteState returns correct boolean`() = runTest {
        val fakeProductDao = FakeProductDao()
        val fakeFavoriteDao = FakeFavoriteDao()
        val repository = FavoriteRepositoryImpl(fakeFavoriteDao, fakeProductDao, FakeProductApi())

        assertFalse(repository.observeFavoriteState(1).first())

        fakeFavoriteDao.upsertFavorite(FavoriteEntity(1, 123L))

        assertTrue(repository.observeFavoriteState(1).first())
    }

    @Test
    fun `getFavoriteProducts maps joined entities`() = runTest {
        val fakeProductDao = FakeProductDao()
        val fakeFavoriteDao = FakeFavoriteDao(fakeProductDao)
        val repository = FavoriteRepositoryImpl(fakeFavoriteDao, fakeProductDao, FakeProductApi())

        fakeProductDao.upsertAll(
            listOf(createDummyProductEntity(1, "Local"))
        )
        fakeFavoriteDao.upsertFavorite(FavoriteEntity(1, 123L))

        val favorites = repository.getFavoriteProducts().first()
        assertEquals(1, favorites.size)
        assertEquals("Local", favorites[0].title)
    }

    @Test
    fun `removeFavorite delegates to dao`() = runTest {
        val fakeFavoriteDao = FakeFavoriteDao()
        val repository = FavoriteRepositoryImpl(fakeFavoriteDao, FakeProductDao(), FakeProductApi())

        fakeFavoriteDao.upsertFavorite(FavoriteEntity(1, 123L))
        assertTrue(fakeFavoriteDao.favorites.value.any { it.productId == 1 })
        
        repository.removeFavorite(1)
        assertFalse(fakeFavoriteDao.favorites.value.any { it.productId == 1 })
    }

    @Test
    fun `getFavoriteProducts handles orphan favorites gracefully`() = runTest {
        val fakeProductDao = FakeProductDao()
        val fakeFavoriteDao = FakeFavoriteDao(fakeProductDao)
        val repository = FavoriteRepositoryImpl(fakeFavoriteDao, fakeProductDao, FakeProductApi())

        fakeFavoriteDao.upsertFavorite(FavoriteEntity(999, 123L))

        val favorites = repository.getFavoriteProducts().first()
        assertEquals(0, favorites.size)
    }

    @Test
    fun `removeFavorite does not delete product entity`() = runTest {
        val fakeProductDao = FakeProductDao()
        val fakeFavoriteDao = FakeFavoriteDao(fakeProductDao)
        val repository = FavoriteRepositoryImpl(fakeFavoriteDao, fakeProductDao, FakeProductApi())

        fakeProductDao.upsertAll(
            listOf(createDummyProductEntity(1, "Local"))
        )
        fakeFavoriteDao.upsertFavorite(FavoriteEntity(1, 123L))
        
        repository.removeFavorite(1)
        
        assertFalse(fakeFavoriteDao.favorites.value.any { it.productId == 1 })
        assertEquals("Local", fakeProductDao.products.value.find { it.id == 1 }?.title)
    }
}

class FakeFavoriteDao(private val productDao: FakeProductDao? = null) : FavoriteDao {
    val favorites = MutableStateFlow<List<FavoriteEntity>>(emptyList())
    
    override fun observeAllFavorites(): Flow<List<FavoriteEntity>> = favorites

    override suspend fun upsertFavorite(favorite: FavoriteEntity) {
        favorites.update { current ->
            val list = current.toMutableList()
            list.removeIf { it.productId == favorite.productId }
            list.add(favorite)
            list
        }
    }

    override suspend fun removeFavorite(productId: Int) {
        favorites.update { current -> current.filter { it.productId != productId } }
    }

    override fun observeFavoriteByProductId(productId: Int): Flow<FavoriteEntity?> {
        return favorites.map { list -> list.find { it.productId == productId } }
    }

    override fun observeFavoriteProducts(): Flow<List<ProductEntity>> {
        return favorites.map { favs ->
            val pDao = productDao ?: return@map emptyList()
            val prods = pDao.products.value
            prods.filter { p -> favs.any { it.productId == p.id } }
                .sortedByDescending { p -> favs.find { it.productId == p.id }?.favoritedAt ?: 0 }
        }
    }
}

class FakeProductDao : ProductDao {
    val products = MutableStateFlow<List<ProductEntity>>(emptyList())

    override suspend fun upsertAll(entities: List<ProductEntity>) {
        products.update { current ->
            val map = current.associateBy { it.id }.toMutableMap()
            entities.forEach { map[it.id] = it }
            map.values.toList()
        }
    }

    override fun observeProductsByContext(query: String): androidx.paging.PagingSource<Int, ProductEntity> {
        throw NotImplementedError()
    }

    override fun observeProductById(id: Int): Flow<ProductEntity?> {
        return products.map { list -> list.find { it.id == id } }
    }

    override suspend fun getProductByIdSync(id: Int): ProductEntity? {
        return products.value.find { it.id == id }
    }
}

class FakeProductApi : ProductApi {
    var getProductCallCount = 0
    var productToReturn: ProductDto? = null

    override suspend fun getProducts(limit: Int, skip: Int): ProductResponseDto = throw NotImplementedError()
    override suspend fun getProductsByCategory(categorySlug: String, limit: Int, skip: Int): ProductResponseDto = throw NotImplementedError()
    override suspend fun searchProducts(query: String, limit: Int, skip: Int): ProductResponseDto = throw NotImplementedError()
    override suspend fun getCategories(): List<CategoryDto> = throw NotImplementedError()

    override suspend fun getProduct(id: Int): ProductDto {
        getProductCallCount++
        return productToReturn ?: throw Exception("Not found")
    }
}

fun createDummyProductEntity(id: Int, title: String): ProductEntity {
    return ProductEntity(
        id = id, title = title, description = "", category = "", price = 0.0,
        discountPercentage = 0.0, rating = 0.0, stock = 0, tags = emptyList(), brand = null,
        sku = "", weight = 0, dimensionWidth = 0.0, dimensionHeight = 0.0, dimensionDepth = 0.0,
        warrantyInformation = "", shippingInformation = "", availabilityStatus = "",
        reviews = emptyList(), returnPolicy = "", minimumOrderQuantity = 0, images = emptyList(),
        thumbnail = "", cachedAt = 0L
    )
}

fun createDummyProductDto(id: Int, title: String): ProductDto {
    return ProductDto(
        id = id, title = title, description = "", category = "", price = 0.0,
        discountPercentage = 0.0, rating = 0.0, stock = 0, tags = emptyList(), brand = null,
        sku = "", weight = 0, dimensions = com.rktuhin.shopflow.data.remote.dto.DimensionsDto(0.0, 0.0, 0.0),
        warrantyInformation = "", shippingInformation = "", availabilityStatus = "",
        reviews = emptyList(), returnPolicy = "", minimumOrderQuantity = 0,
        meta = com.rktuhin.shopflow.data.remote.dto.MetaDto("", "", "", ""),
        images = emptyList(), thumbnail = ""
    )
}
