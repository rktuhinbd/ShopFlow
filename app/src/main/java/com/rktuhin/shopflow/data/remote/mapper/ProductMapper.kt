package com.rktuhin.shopflow.data.remote.mapper

import com.rktuhin.shopflow.data.local.entity.ProductEntity
import com.rktuhin.shopflow.data.local.entity.ProductReview
import com.rktuhin.shopflow.data.remote.dto.ProductDto
import com.rktuhin.shopflow.data.remote.dto.ReviewDto
import com.rktuhin.shopflow.domain.model.Product
import com.rktuhin.shopflow.domain.model.Review

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
        reviews = this.reviews.map { it.toProductReview() },
        returnPolicy = this.returnPolicy,
        minimumOrderQuantity = this.minimumOrderQuantity,
        images = this.images,
        thumbnail = this.thumbnail,
        cachedAt = cachedAt
    )
}

fun ReviewDto.toProductReview(): ProductReview {
    return ProductReview(
        rating = this.rating,
        comment = this.comment,
        date = this.date,
        reviewerName = this.reviewerName
    )
}

fun ProductEntity.toProduct(): Product {
    return Product(
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
        dimensionWidth = this.dimensionWidth,
        dimensionHeight = this.dimensionHeight,
        dimensionDepth = this.dimensionDepth,
        warrantyInformation = this.warrantyInformation,
        shippingInformation = this.shippingInformation,
        availabilityStatus = this.availabilityStatus,
        returnPolicy = this.returnPolicy,
        minimumOrderQuantity = this.minimumOrderQuantity,
        images = this.images,
        thumbnail = this.thumbnail,
        reviews = this.reviews.map { it.toReview() }
    )
}

fun ProductReview.toReview(): Review {
    return Review(
        rating = this.rating,
        comment = this.comment,
        date = this.date,
        reviewerName = this.reviewerName
    )
}

fun ProductDto.toProduct(): Product {
    return Product(
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
        returnPolicy = this.returnPolicy,
        minimumOrderQuantity = this.minimumOrderQuantity,
        images = this.images,
        thumbnail = this.thumbnail,
        reviews = this.reviews.map { it.toReview() }
    )
}

fun ReviewDto.toReview(): Review {
    return Review(
        rating = this.rating,
        comment = this.comment,
        date = this.date,
        reviewerName = this.reviewerName
    )
}
