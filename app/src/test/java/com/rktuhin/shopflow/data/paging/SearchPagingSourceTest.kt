package com.rktuhin.shopflow.data.paging

import androidx.paging.PagingSource
import com.rktuhin.shopflow.data.remote.api.ProductApi
import com.rktuhin.shopflow.data.remote.dto.CategoryDto
import com.rktuhin.shopflow.data.remote.dto.ProductDto
import com.rktuhin.shopflow.data.remote.dto.ProductResponseDto
import com.rktuhin.shopflow.domain.model.Product
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchPagingSourceTest {

    @Test
    fun `load refresh uses zero skip and returns correct nextKey`() = runTest {
        val fakeApi = FakeSearchApi(totalToReturn = 100)
        val source = SearchPagingSource(fakeApi, "phone")

        val result = source.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        
        assertEquals(20, page.data.size)
        assertEquals(null, page.prevKey)
        assertEquals(20, page.nextKey) // skip (0) + size (20)
        
        assertEquals(0, fakeApi.lastSkip)
        assertEquals(20, fakeApi.lastLimit)
    }

    @Test
    fun `load append uses key for skip and returns correct nextKey`() = runTest {
        val fakeApi = FakeSearchApi(totalToReturn = 100)
        val source = SearchPagingSource(fakeApi, "phone")

        val result = source.load(
            PagingSource.LoadParams.Append(
                key = 40,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        
        assertEquals(20, page.data.size)
        assertEquals(null, page.prevKey)
        assertEquals(60, page.nextKey) // skip (40) + size (20)
        
        assertEquals(40, fakeApi.lastSkip)
    }

    @Test
    fun `load terminal exact page returns null nextKey`() = runTest {
        val fakeApi = FakeSearchApi(totalToReturn = 60)
        val source = SearchPagingSource(fakeApi, "phone")

        val result = source.load(
            PagingSource.LoadParams.Append(
                key = 40,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        
        assertEquals(20, page.data.size)
        assertEquals(null, page.prevKey)
        assertEquals(null, page.nextKey) // 40 + 20 >= 60
    }

    @Test
    fun `load terminal partial page returns null nextKey`() = runTest {
        val fakeApi = FakeSearchApi(totalToReturn = 55)
        val source = SearchPagingSource(fakeApi, "phone")

        val result = source.load(
            PagingSource.LoadParams.Append(
                key = 40,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        
        assertEquals(15, page.data.size) // Only 15 left
        assertEquals(null, page.prevKey)
        assertEquals(null, page.nextKey) // 40 + 15 >= 55
    }

    @Test
    fun `load terminal empty page returns null nextKey`() = runTest {
        val fakeApi = FakeSearchApi(totalToReturn = 60)
        val source = SearchPagingSource(fakeApi, "phone")

        val result = source.load(
            PagingSource.LoadParams.Append(
                key = 60,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        
        assertEquals(0, page.data.size)
        assertEquals(null, page.prevKey)
        assertEquals(null, page.nextKey)
    }

    @Test
    fun `load network error returns Error`() = runTest {
        val fakeApi = FakeSearchApi(shouldThrow = true)
        val source = SearchPagingSource(fakeApi, "phone")

        val result = source.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Error)
    }
}

class FakeSearchApi(
    private val totalToReturn: Int = 100,
    private val shouldThrow: Boolean = false
) : ProductApi {
    var lastSkip = -1
    var lastLimit = -1

    override suspend fun searchProducts(query: String, limit: Int, skip: Int): ProductResponseDto {
        if (shouldThrow) throw Exception("Network Error")
        lastSkip = skip
        lastLimit = limit

        val remaining = totalToReturn - skip
        val returnCount = if (remaining < 0) 0 else if (remaining > limit) limit else remaining

        val products = (1..returnCount).map {
            createDummyProductDto(id = skip + it, title = "Item ${skip + it}")
        }
        
        return ProductResponseDto(
            products = products,
            total = totalToReturn,
            skip = skip,
            limit = limit
        )
    }

    override suspend fun getProducts(limit: Int, skip: Int): ProductResponseDto = throw NotImplementedError()
    override suspend fun getProductsByCategory(categorySlug: String, limit: Int, skip: Int): ProductResponseDto = throw NotImplementedError()
    override suspend fun getProduct(id: Int): ProductDto = throw NotImplementedError()
    override suspend fun getCategories(): List<CategoryDto> = throw NotImplementedError()
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
