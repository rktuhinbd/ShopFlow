package com.rktuhin.shopflow.data.local.converter

import androidx.room.TypeConverter
import com.rktuhin.shopflow.data.local.entity.ProductReview
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * TypeConverter for List<ProductReview> to be used in Room Database.
 * Converts between a Kotlin List<ProductReview> and a JSON String representation in SQLite TEXT.
 */
class ProductReviewListConverter {

    // Using the project's approved JSON configuration.
    // ignoreUnknownKeys = true is crucial here if the API or future versions of the app add fields to the review JSON
    // that this version of the app does not know about.
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromProductReviewList(value: List<ProductReview>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toProductReviewList(value: String): List<ProductReview> {
        // We let SerializationException propagate on malformed JSON to avoid silently hiding database corruption.
        return json.decodeFromString(value)
    }
}
