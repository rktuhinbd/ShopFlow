package com.rktuhin.shopflow.data.local.converter

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * TypeConverter for List<String> to be used in Room Database.
 * Converts between a Kotlin List<String> and a JSON String representation in SQLite TEXT.
 */
class StringListConverter {

    // Using the project's approved JSON configuration: ignoring unknown keys to prevent crashes
    // if future schema adds properties we don't care about (though less relevant for List<String>).
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        // If the JSON is malformed, decodeFromString will throw a SerializationException.
        // We do not catch it because we do not want to silently convert malformed data into valid but incorrect data (e.g., empty list).
        return json.decodeFromString(value)
    }
}
