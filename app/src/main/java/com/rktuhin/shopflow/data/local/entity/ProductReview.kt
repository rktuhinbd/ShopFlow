package com.rktuhin.shopflow.data.local.entity

import kotlinx.serialization.Serializable

/**
 * Represents a review for a product.
 * Stored as a JSON string in the Room database.
 * Only fields required by the UI are included; reviewerEmail is intentionally omitted.
 */
@Serializable
data class ProductReview(
    val reviewerName: String,
    val rating: Int,
    val comment: String,
    val date: String
)
