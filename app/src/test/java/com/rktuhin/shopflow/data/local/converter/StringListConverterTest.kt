package com.rktuhin.shopflow.data.local.converter

import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class StringListConverterTest {

    private val converter = StringListConverter()

    @Test
    fun `fromStringList with empty list returns JSON empty array string`() {
        val list = emptyList<String>()
        val result = converter.fromStringList(list)
        assertEquals("[]", result)
    }

    @Test
    fun `fromStringList with single element returns correct JSON string`() {
        val list = listOf("test")
        val result = converter.fromStringList(list)
        assertEquals("""["test"]""", result)
    }

    @Test
    fun `fromStringList with multiple elements returns correct JSON string`() {
        val list = listOf("test1", "test2", "test3")
        val result = converter.fromStringList(list)
        assertEquals("""["test1","test2","test3"]""", result)
    }

    @Test
    fun `toStringList with valid JSON array parses to correct list`() {
        val json = """["test1","test2"]"""
        val result = converter.toStringList(json)
        assertEquals(listOf("test1", "test2"), result)
    }

    @Test
    fun `round trip with special characters maintains data integrity`() {
        // Strings with commas, quotes, and newlines
        val originalList = listOf(
            "comma,separated",
            "\"quoted text\"",
            "new\nline",
            "emoji \uD83D\uDE00"
        )
        val json = converter.fromStringList(originalList)
        val decodedList = converter.toStringList(json)
        
        assertEquals(originalList, decodedList)
    }

    @Test
    fun `toStringList with malformed JSON throws SerializationException`() {
        // Missing closing bracket
        val malformedJson = """["test""""
        assertThrows(SerializationException::class.java) {
            converter.toStringList(malformedJson)
        }
    }
}
