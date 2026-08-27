package com.rktuhin.shopflow.data.remote.api

import com.rktuhin.shopflow.data.remote.dto.CategoryDto
import com.rktuhin.shopflow.data.remote.dto.ProductResponseDto
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class ProductApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var productApi: ProductApi

    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val contentType = "application/json".toMediaType()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        productApi = retrofit.create(ProductApi::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getProducts requests correct url and parses successfully`() = runTest {
        val responseJson = """
            {
              "products": [
                {
                  "id": 1,
                  "title": "Essence Mascara Lash Princess",
                  "description": "The Essence Mascara...",
                  "category": "beauty",
                  "price": 9.99,
                  "discountPercentage": 10.48,
                  "rating": 2.56,
                  "stock": 99,
                  "tags": ["beauty", "mascara"],
                  "brand": "Essence",
                  "sku": "BEA-ESS-ESS-001",
                  "weight": 4,
                  "dimensions": { "width": 15.14, "height": 13.08, "depth": 22.99 },
                  "warrantyInformation": "1 week warranty",
                  "shippingInformation": "Ships in 3-5 business days",
                  "availabilityStatus": "In Stock",
                  "reviews": [
                    { "rating": 3, "comment": "Would not recommend!", "date": "2025-04-30T09:41:02.053Z", "reviewerName": "Eleanor Collins", "reviewerEmail": "eleanor.collins@x.dummyjson.com" }
                  ],
                  "returnPolicy": "No return policy",
                  "minimumOrderQuantity": 48,
                  "meta": { "createdAt": "2025-04-30T09:41:02.053Z", "updatedAt": "2025-04-30T09:41:02.053Z", "barcode": "5784719087687", "qrCode": "https://cdn.dummyjson.com/public/qr-code.png" },
                  "images": ["https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/1.webp"],
                  "thumbnail": "https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/thumbnail.webp"
                }
              ],
              "total": 194,
              "skip": 0,
              "limit": 10
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(responseJson))

        val result = productApi.getProducts(limit = 10, skip = 0)

        // Verify Request
        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/products?limit=10&skip=0", request.path)

        // Verify Parsing
        assertNotNull(result)
        assertEquals(194, result.total)
        assertEquals(10, result.limit)
        assertEquals(0, result.skip)
        
        val product = result.products.first()
        assertEquals(1, product.id)
        assertEquals("Essence Mascara Lash Princess", product.title)
        assertEquals("beauty", product.category)
        assertEquals(9.99, product.price, 0.0)
        assertEquals(15.14, product.dimensions.width, 0.0)
        assertEquals("Eleanor Collins", product.reviews.first().reviewerName)
    }

    @Test
    fun `searchProducts requests correct url with encoded query and parses successfully`() = runTest {
        val responseJson = """
            {
              "products": [],
              "total": 0,
              "skip": 0,
              "limit": 10
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(responseJson))

        val result = productApi.searchProducts(query = "smart phone", limit = 10, skip = 0)

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        // Retrofit encodes spaces as `%20` or `+` based on query rules. Typically encoded.
        assertTrue(request.path?.startsWith("/products/search?q=smart%20phone") == true || request.path?.startsWith("/products/search?q=smart+phone") == true)
        assertTrue(request.path?.contains("limit=10") == true)
        assertTrue(request.path?.contains("skip=0") == true)

        assertEquals(0, result.total)
    }

    @Test
    fun `getCategories requests correct url and parses list successfully`() = runTest {
        val responseJson = """
            [
              { "slug": "beauty", "name": "Beauty", "url": "https://dummyjson.com/products/category/beauty" },
              { "slug": "fragrances", "name": "Fragrances", "url": "https://dummyjson.com/products/category/fragrances" }
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(responseJson))

        val result = productApi.getCategories()

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/products/categories", request.path)

        assertEquals(2, result.size)
        assertEquals("beauty", result[0].slug)
        assertEquals("Beauty", result[0].name)
    }

    @Test
    fun `getProductsByCategory requests correct path and query parameters`() = runTest {
        val responseJson = """
            {
              "products": [],
              "total": 5,
              "skip": 0,
              "limit": 10
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(responseJson))

        productApi.getProductsByCategory(category = "smartphones", limit = 10, skip = 0)

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/products/category/smartphones?limit=10&skip=0", request.path)
    }

    @Test
    fun `getProduct requests correct path and parses product successfully`() = runTest {
        val responseJson = """
            {
              "id": 5,
              "title": "Red Lipstick",
              "description": "Nice lipstick",
              "category": "beauty",
              "price": 12.5,
              "discountPercentage": 5.0,
              "rating": 4.2,
              "stock": 50,
              "tags": ["beauty"],
              "sku": "BEA-005",
              "weight": 2,
              "dimensions": { "width": 2.0, "height": 8.0, "depth": 2.0 },
              "warrantyInformation": "No warranty",
              "shippingInformation": "Ships in 1 day",
              "availabilityStatus": "In Stock",
              "reviews": [],
              "returnPolicy": "14 days",
              "minimumOrderQuantity": 1,
              "meta": { "createdAt": "2025-04-30", "updatedAt": "2025-04-30", "barcode": "111", "qrCode": "url" },
              "images": [],
              "thumbnail": "thumb.jpg"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(responseJson))

        val product = productApi.getProduct(id = 5)

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/products/5", request.path)

        assertEquals(5, product.id)
        assertEquals("Red Lipstick", product.title)
        assertEquals(12.5, product.price, 0.0)
    }

    @Test
    fun `HTTP 404 response throws HttpException`() = runTest {
        val responseJson = """{ "message": "Product with id '999' not found" }"""
        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody(responseJson))

        var exception: HttpException? = null
        try {
            productApi.getProduct(id = 999)
        } catch (e: HttpException) {
            exception = e
        }

        assertNotNull(exception)
        assertEquals(404, exception?.code())
    }

    @Test
    fun `HTTP 500 response throws HttpException`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody("Internal Server Error"))

        var exception: HttpException? = null
        try {
            productApi.getProducts(limit = 10, skip = 0)
        } catch (e: HttpException) {
            exception = e
        }

        assertNotNull(exception)
        assertEquals(500, exception?.code())
    }

    @Test
    fun `Malformed JSON throws SerializationException`() = runTest {
        val malformedJson = """ { "products": [ { "id": "INVALID_ID_TYPE_STRING" } ] } """
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(malformedJson))

        var exception: Exception? = null
        try {
            productApi.getProducts(limit = 10, skip = 0)
        } catch (e: Exception) {
            exception = e
        }

        assertNotNull(exception)
        assertTrue(exception is SerializationException)
    }
}
