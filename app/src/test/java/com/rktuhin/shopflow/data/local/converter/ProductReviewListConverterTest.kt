package com.rktuhin.shopflow.data.local.converter

import com.rktuhin.shopflow.data.local.entity.ProductReview
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ProductReviewListConverterTest {

    private val converter = ProductReviewListConverter()

    @Test
    fun `fromProductReviewList with empty list returns JSON empty array string`() {
        val list = emptyList<ProductReview>()
        val result = converter.fromProductReviewList(list)
        assertEquals("[]", result)
    }

    @Test
    fun `fromProductReviewList with single review returns correct JSON string`() {
        val review = ProductReview(
            reviewerName = "John Doe",
            rating = 5,
            comment = "Great product",
            date = "2023-01-01"
        )
        val list = listOf(review)
        val result = converter.fromProductReviewList(list)
        // Ensure it encodes to a JSON array containing the object
        val expected = """[{"reviewerName":"John Doe","rating":5,"comment":"Great product","date":"2023-01-01"}]"""
        assertEquals(expected, result)
    }

    @Test
    fun `round trip with multiple reviews and special characters maintains data integrity`() {
        val originalList = listOf(
            ProductReview(
                reviewerName = "Alice",
                rating = 4,
                comment = "Good, but has some issues.",
                date = "2023-01-02"
            ),
            ProductReview(
                reviewerName = "Bob \"The Builder\"",
                rating = 1,
                comment = "Terrible\nDon't buy it! \uD83D\uDE21",
                date = "2023-01-03"
            )
        )
        val json = converter.fromProductReviewList(originalList)
        val decodedList = converter.toProductReviewList(json)
        
        assertEquals(originalList, decodedList)
    }

    @Test
    fun `toProductReviewList ignores unknown keys`() {
        // Simulating JSON from DB that might have been saved by a future version of the app
        // with an extra field 'reviewerEmail' that is not in the current ProductReview data class.
        val jsonWithExtraField = """[{"reviewerName":"Jane","rating":5,"comment":"Nice","date":"2023-01-04","reviewerEmail":"jane@example.com"}]"""
        
        val decodedList = converter.toProductReviewList(jsonWithExtraField)
        
        assertEquals(1, decodedList.size)
        assertEquals("Jane", decodedList[0].reviewerName)
        assertEquals(5, decodedList[0].rating)
    }

    @Test
    fun `toProductReviewList with malformed JSON throws SerializationException`() {
        // Missing closing bracket
        val malformedJson = """[{"reviewerName":"John Doe","rating":5,"comment":"Great product","date":"2023-01-01"}"""
        assertThrows(SerializationException::class.java) {
            converter.toProductReviewList(malformedJson)
        }
    }
}
