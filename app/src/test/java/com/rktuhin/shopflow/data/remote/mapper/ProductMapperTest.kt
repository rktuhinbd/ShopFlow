package com.rktuhin.shopflow.data.remote.mapper

import com.rktuhin.shopflow.data.local.entity.ProductEntity
import com.rktuhin.shopflow.data.local.entity.ProductReview
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
        val dto = createBaseDto()
        val cachedAt = 123456789L
        val entity = dto.toProductEntity(cachedAt)

        assertDtoEntityMapping(dto, entity)
        assertEquals(cachedAt, entity.cachedAt)
    }

    @Test
    fun `toProductEntity handles nullable brand correctly`() {
        val dto = createBaseDto().copy(brand = null)
        val entity = dto.toProductEntity()
        assertNull(entity.brand)
    }

    @Test
    fun `toProduct maps ProductEntity to Domain Product correctly`() {
        val entity = createBaseEntity()
        val domain = entity.toProduct()

        assertEquals(entity.id, domain.id)
        assertEquals(entity.title, domain.title)
        assertEquals(entity.description, domain.description)
        assertEquals(entity.category, domain.category)
        assertEquals(entity.price, domain.price, 0.0)
        assertEquals(entity.discountPercentage, domain.discountPercentage, 0.0)
        assertEquals(entity.rating, domain.rating, 0.0)
        assertEquals(entity.stock, domain.stock)
        assertEquals(entity.tags, domain.tags)
        assertEquals(entity.brand, domain.brand)
        assertEquals(entity.sku, domain.sku)
        assertEquals(entity.weight, domain.weight)
        
        assertEquals(entity.dimensionWidth, domain.dimensionWidth, 0.0)
        assertEquals(entity.dimensionHeight, domain.dimensionHeight, 0.0)
        assertEquals(entity.dimensionDepth, domain.dimensionDepth, 0.0)
        
        assertEquals(entity.warrantyInformation, domain.warrantyInformation)
        assertEquals(entity.shippingInformation, domain.shippingInformation)
        assertEquals(entity.availabilityStatus, domain.availabilityStatus)
        assertEquals(entity.returnPolicy, domain.returnPolicy)
        assertEquals(entity.minimumOrderQuantity, domain.minimumOrderQuantity)
        assertEquals(entity.images, domain.images)
        assertEquals(entity.thumbnail, domain.thumbnail)

        assertEquals(1, domain.reviews.size)
        val review = domain.reviews[0]
        val entityReview = entity.reviews[0]
        assertEquals(entityReview.rating, review.rating)
        assertEquals(entityReview.comment, review.comment)
        assertEquals(entityReview.date, review.date)
        assertEquals(entityReview.reviewerName, review.reviewerName)
    }
    
    @Test
    fun `toProduct maps ProductEntity with nullable brand correctly`() {
        val entity = createBaseEntity().copy(brand = null)
        val domain = entity.toProduct()
        assertNull(domain.brand)
    }

    @Test
    fun `toProduct maps ProductDto to Domain Product correctly`() {
        val dto = createBaseDto()
        val domain = dto.toProduct()

        assertEquals(dto.id, domain.id)
        assertEquals(dto.title, domain.title)
        assertEquals(dto.description, domain.description)
        assertEquals(dto.category, domain.category)
        assertEquals(dto.price, domain.price, 0.0)
        assertEquals(dto.discountPercentage, domain.discountPercentage, 0.0)
        assertEquals(dto.rating, domain.rating, 0.0)
        assertEquals(dto.stock, domain.stock)
        assertEquals(dto.tags, domain.tags)
        assertEquals(dto.brand, domain.brand)
        assertEquals(dto.sku, domain.sku)
        assertEquals(dto.weight, domain.weight)
        
        assertEquals(dto.dimensions.width, domain.dimensionWidth, 0.0)
        assertEquals(dto.dimensions.height, domain.dimensionHeight, 0.0)
        assertEquals(dto.dimensions.depth, domain.dimensionDepth, 0.0)
        
        assertEquals(dto.warrantyInformation, domain.warrantyInformation)
        assertEquals(dto.shippingInformation, domain.shippingInformation)
        assertEquals(dto.availabilityStatus, domain.availabilityStatus)
        assertEquals(dto.returnPolicy, domain.returnPolicy)
        assertEquals(dto.minimumOrderQuantity, domain.minimumOrderQuantity)
        assertEquals(dto.images, domain.images)
        assertEquals(dto.thumbnail, domain.thumbnail)

        assertEquals(1, domain.reviews.size)
        val review = domain.reviews[0]
        val dtoReview = dto.reviews[0]
        assertEquals(dtoReview.rating, review.rating)
        assertEquals(dtoReview.comment, review.comment)
        assertEquals(dtoReview.date, review.date)
        assertEquals(dtoReview.reviewerName, review.reviewerName)
    }
    
    @Test
    fun `toProduct maps ProductDto with nullable brand correctly`() {
        val dto = createBaseDto().copy(brand = null)
        val domain = dto.toProduct()
        assertNull(domain.brand)
    }
    
    @Test
    fun `ReviewDto toProductReview maps fields correctly`() {
        val dto = createBaseReviewDto()
        val entity = dto.toProductReview()
        
        assertEquals(dto.rating, entity.rating)
        assertEquals(dto.comment, entity.comment)
        assertEquals(dto.date, entity.date)
        assertEquals(dto.reviewerName, entity.reviewerName)
    }

    @Test
    fun `ProductReview toReview maps fields correctly`() {
        val entityReview = createBaseProductReview()
        val domainReview = entityReview.toReview()
        
        assertEquals(entityReview.rating, domainReview.rating)
        assertEquals(entityReview.comment, domainReview.comment)
        assertEquals(entityReview.date, domainReview.date)
        assertEquals(entityReview.reviewerName, domainReview.reviewerName)
    }
    
    @Test
    fun `ReviewDto toReview maps fields correctly`() {
        val dto = createBaseReviewDto()
        val domainReview = dto.toReview()
        
        assertEquals(dto.rating, domainReview.rating)
        assertEquals(dto.comment, domainReview.comment)
        assertEquals(dto.date, domainReview.date)
        assertEquals(dto.reviewerName, domainReview.reviewerName)
    }

    private fun assertDtoEntityMapping(dto: ProductDto, entity: ProductEntity) {
        assertEquals(dto.id, entity.id)
        assertEquals(dto.title, entity.title)
        assertEquals(dto.description, entity.description)
        assertEquals(dto.category, entity.category)
        assertEquals(dto.price, entity.price, 0.0)
        assertEquals(dto.discountPercentage, entity.discountPercentage, 0.0)
        assertEquals(dto.rating, entity.rating, 0.0)
        assertEquals(dto.stock, entity.stock)
        assertEquals(dto.tags, entity.tags)
        assertEquals(dto.brand, entity.brand)
        assertEquals(dto.sku, entity.sku)
        assertEquals(dto.weight, entity.weight)
        
        assertEquals(dto.dimensions.width, entity.dimensionWidth, 0.0)
        assertEquals(dto.dimensions.height, entity.dimensionHeight, 0.0)
        assertEquals(dto.dimensions.depth, entity.dimensionDepth, 0.0)
        
        assertEquals(dto.warrantyInformation, entity.warrantyInformation)
        assertEquals(dto.shippingInformation, entity.shippingInformation)
        assertEquals(dto.availabilityStatus, entity.availabilityStatus)
        assertEquals(dto.returnPolicy, entity.returnPolicy)
        assertEquals(dto.minimumOrderQuantity, entity.minimumOrderQuantity)
        assertEquals(dto.images, entity.images)
        assertEquals(dto.thumbnail, entity.thumbnail)

        assertEquals(1, entity.reviews.size)
        val review = entity.reviews[0]
        val dtoReview = dto.reviews[0]
        assertEquals(dtoReview.rating, review.rating)
        assertEquals(dtoReview.comment, review.comment)
        assertEquals(dtoReview.date, review.date)
        assertEquals(dtoReview.reviewerName, review.reviewerName)
    }

    private fun createBaseDto() = ProductDto(
        id = 1, title = "Test Product", description = "Description", category = "test-category", 
        price = 9.99, discountPercentage = 10.0, rating = 4.5, stock = 100, 
        tags = listOf("tag1", "tag2"), brand = "TestBrand", sku = "SKU-123", weight = 500, 
        dimensions = DimensionsDto(10.0, 20.0, 30.0),
        warrantyInformation = "1 Year", shippingInformation = "Fast", availabilityStatus = "In Stock",
        reviews = listOf(createBaseReviewDto()), returnPolicy = "30 Days", minimumOrderQuantity = 1,
        meta = MetaDto("2023", "2023", "12345", "http://qr"), 
        images = listOf("image1.jpg", "image2.jpg"), thumbnail = "thumb.jpg"
    )

    private fun createBaseEntity() = ProductEntity(
        id = 1, title = "Test Product", description = "Description", category = "test-category", 
        price = 9.99, discountPercentage = 10.0, rating = 4.5, stock = 100, 
        tags = listOf("tag1", "tag2"), brand = "TestBrand", sku = "SKU-123", weight = 500, 
        dimensionWidth = 10.0, dimensionHeight = 20.0, dimensionDepth = 30.0,
        warrantyInformation = "1 Year", shippingInformation = "Fast", availabilityStatus = "In Stock",
        reviews = listOf(createBaseProductReview()), returnPolicy = "30 Days", minimumOrderQuantity = 1,
        images = listOf("image1.jpg", "image2.jpg"), thumbnail = "thumb.jpg", cachedAt = 123456789L
    )

    private fun createBaseReviewDto() = ReviewDto(
        rating = 5, comment = "Great", date = "2023-01-01T00:00:00Z", 
        reviewerName = "John Doe", reviewerEmail = "john@example.com"
    )
    
    private fun createBaseProductReview() = ProductReview(
        rating = 5, comment = "Great", date = "2023-01-01T00:00:00Z", reviewerName = "John Doe"
    )
}
