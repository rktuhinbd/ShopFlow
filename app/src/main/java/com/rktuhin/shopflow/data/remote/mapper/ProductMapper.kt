package com.rktuhin.shopflow.data.remote.mapper

import com.rktuhin.shopflow.data.local.entity.ProductEntity
import com.rktuhin.shopflow.data.remote.dto.ProductDto

fun ProductDto.toProductEntity(cachedAt: Long = System.currentTimeMillis()): ProductEntity {
    return ProductEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        category = this.category,
        price = this.price,
        discountPercentage = this.discountPercentage,
        rating = this.rating,
        stock = this.stock,
        tags = this.tags,
        brand = this.brand,
        sku = this.sku,
        weight = this.weight,
        dimensionWidth = this.dimensions.width,
        dimensionHeight = this.dimensions.height,
        dimensionDepth = this.dimensions.depth,
        warrantyInformation = this.warrantyInformation,
        shippingInformation = this.shippingInformation,
        availabilityStatus = this.availabilityStatus,
        reviews = this.reviews.map { reviewDto ->
            com.rktuhin.shopflow.data.local.entity.ProductReview(
                rating = reviewDto.rating,
                comment = reviewDto.comment,
                date = reviewDto.date,
                reviewerName = reviewDto.reviewerName
            )
        },
        returnPolicy = this.returnPolicy,
        minimumOrderQuantity = this.minimumOrderQuantity,
        images = this.images,
        thumbnail = this.thumbnail,
        cachedAt = cachedAt
    )
}
