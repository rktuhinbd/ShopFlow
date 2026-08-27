package com.rktuhin.shopflow.data.remote.mapper

import com.rktuhin.shopflow.data.remote.dto.DimensionsDto
import com.rktuhin.shopflow.data.remote.dto.MetaDto
import com.rktuhin.shopflow.data.remote.dto.ProductDto
import com.rktuhin.shopflow.data.remote.dto.ReviewDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProductMapperTest {

    @Test
    fun `toProductEntity maps standard fields correctly`() {
        val dto = ProductDto(
            id = 1,
            title = "Test Product",
            description = "Description",
            category = "test-category",
            price = 9.99,
            discountPercentage = 10.0,
            rating = 4.5,
            stock = 100,
            tags = listOf("tag1", "tag2"),
            brand = "TestBrand",
            sku = "SKU-123",
            weight = 500,
            dimensions = DimensionsDto(10.0, 20.0, 30.0),
            warrantyInformation = "1 Year",
            shippingInformation = "Fast",
            availabilityStatus = "In Stock",
            reviews = listOf(
                ReviewDto(
                    rating = 5,
                    comment = "Great",
                    date = "2023-01-01T00:00:00Z",
                    reviewerName = "John Doe",
                    reviewerEmail = "john@example.com"
                )
            ),
            returnPolicy = "30 Days",
            minimumOrderQuantity = 1,
            meta = MetaDto("2023", "2023", "12345", "http://qr"),
            images = listOf("image1.jpg", "image2.jpg"),
            thumbnail = "thumb.jpg"
        )

        val cachedAt = 123456789L
        val entity = dto.toProductEntity(cachedAt)

        assertEquals(1, entity.id)
        assertEquals("Test Product", entity.title)
        assertEquals("Description", entity.description)
        assertEquals("test-category", entity.category)
        assertEquals(9.99, entity.price, 0.0)
        assertEquals(10.0, entity.discountPercentage, 0.0)
        assertEquals(4.5, entity.rating, 0.0)
        assertEquals(100, entity.stock)
        assertEquals(listOf("tag1", "tag2"), entity.tags)
        assertEquals("TestBrand", entity.brand)
        assertEquals("SKU-123", entity.sku)
        assertEquals(500, entity.weight)
        
        assertEquals(10.0, entity.dimensionWidth, 0.0)
        assertEquals(20.0, entity.dimensionHeight, 0.0)
        assertEquals(30.0, entity.dimensionDepth, 0.0)
        
        assertEquals("1 Year", entity.warrantyInformation)
        assertEquals("Fast", entity.shippingInformation)
        assertEquals("In Stock", entity.availabilityStatus)
        assertEquals("30 Days", entity.returnPolicy)
        assertEquals(1, entity.minimumOrderQuantity)
        assertEquals(listOf("image1.jpg", "image2.jpg"), entity.images)
        assertEquals("thumb.jpg", entity.thumbnail)
        assertEquals(123456789L, entity.cachedAt)

        assertEquals(1, entity.reviews.size)
        val review = entity.reviews[0]
        assertEquals(5, review.rating)
        assertEquals("Great", review.comment)
        assertEquals("2023-01-01T00:00:00Z", review.date)
        assertEquals("John Doe", review.reviewerName)
    }

    @Test
    fun `toProductEntity handles nullable brand correctly`() {
        val dto = createBaseDto().copy(brand = null)
        val entity = dto.toProductEntity()
        assertNull(entity.brand)
    }

    private fun createBaseDto() = ProductDto(
        id = 1, title = "", description = "", category = "", price = 0.0,
        discountPercentage = 0.0, rating = 0.0, stock = 0, tags = emptyList(),
        brand = "Brand", sku = "", weight = 0, dimensions = DimensionsDto(0.0, 0.0, 0.0),
        warrantyInformation = "", shippingInformation = "", availabilityStatus = "",
        reviews = emptyList(), returnPolicy = "", minimumOrderQuantity = 0,
        meta = MetaDto("", "", "", ""), images = emptyList(), thumbnail = ""
    )
}
