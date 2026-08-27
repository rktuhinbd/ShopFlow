package com.rktuhin.shopflow.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a cached product.
 * Nested objects like dimensions and metadata are flattened.
 * Lists like tags, images, and reviews require a TypeConverter for JSON serialization.
 */
@Entity(
    tableName = "products",
    indices = [
        Index(value = ["category"], name = "index_products_category")
    ]
)
data class ProductEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val tags: List<String>,
    val brand: String?,
    val sku: String,
    val weight: Int,
    val dimensionWidth: Double,
    val dimensionHeight: Double,
    val dimensionDepth: Double,
    val warrantyInformation: String,
    val shippingInformation: String,
    val availabilityStatus: String,
    val reviews: List<ProductReview>,
    val returnPolicy: String,
    val minimumOrderQuantity: Int,
    val images: List<String>,
    val thumbnail: String,
    val cachedAt: Long
)
