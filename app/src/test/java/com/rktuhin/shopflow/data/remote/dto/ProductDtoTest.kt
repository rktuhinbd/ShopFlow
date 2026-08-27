package com.rktuhin.shopflow.data.remote.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ProductDtoTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `test product response deserialization`() {
        val jsonString = """
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
                    "dimensions": {
                        "width": 15.14,
                        "height": 13.08,
                        "depth": 22.99
                    },
                    "warrantyInformation": "1 week warranty",
                    "shippingInformation": "Ships in 3-5 business days",
                    "availabilityStatus": "In Stock",
                    "reviews": [
                        {
                            "rating": 3,
                            "comment": "Would not recommend!",
                            "date": "2025-04-30T09:41:02.053Z",
                            "reviewerName": "Eleanor Collins",
                            "reviewerEmail": "eleanor.collins@x.dummyjson.com"
                        }
                    ],
                    "returnPolicy": "No return policy",
                    "minimumOrderQuantity": 48,
                    "meta": {
                        "createdAt": "2025-04-30T09:41:02.053Z",
                        "updatedAt": "2025-04-30T09:41:02.053Z",
                        "barcode": "5784719087687",
                        "qrCode": "https://cdn.dummyjson.com/public/qr-code.png"
                    },
                    "images": [
                        "https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/1.webp"
                    ],
                    "thumbnail": "https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/thumbnail.webp"
                }
            ],
            "total": 194,
            "skip": 0,
            "limit": 30
        }
        """.trimIndent()

        val response = json.decodeFromString<ProductResponseDto>(jsonString)

        assertEquals(194, response.total)
        assertEquals(0, response.skip)
        assertEquals(30, response.limit)
        assertEquals(1, response.products.size)
        
        val product = response.products[0]
        assertEquals(1, product.id)
        assertEquals("Essence Mascara Lash Princess", product.title)
        assertEquals(9.99, product.price, 0.0)
        assertEquals("Essence", product.brand)
        assertNotNull(product.dimensions)
        assertEquals(15.14, product.dimensions.width, 0.0)
        assertEquals(1, product.reviews.size)
        assertEquals("Eleanor Collins", product.reviews[0].reviewerName)
        assertEquals("5784719087687", product.meta.barcode)
    }

    @Test
    fun `test category deserialization`() {
        val jsonString = """
        {
            "slug": "beauty",
            "name": "Beauty",
            "url": "https://dummyjson.com/products/category/beauty"
        }
        """.trimIndent()

        val category = json.decodeFromString<CategoryDto>(jsonString)

        assertEquals("beauty", category.slug)
        assertEquals("Beauty", category.name)
        assertEquals("https://dummyjson.com/products/category/beauty", category.url)
    }
}
