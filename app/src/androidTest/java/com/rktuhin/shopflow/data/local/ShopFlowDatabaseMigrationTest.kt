package com.rktuhin.shopflow.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ShopFlowDatabaseMigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        ShopFlowDatabase::class.java
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        // Create the database with version 1
        var db = helper.createDatabase(TEST_DB, 1)

        // Insert product data
        db.execSQL(
            "INSERT INTO products (id, title, description, category, price, discountPercentage, rating, stock, tags, brand, sku, weight, dimensionWidth, dimensionHeight, dimensionDepth, warrantyInformation, shippingInformation, availabilityStatus, reviews, returnPolicy, minimumOrderQuantity, images, thumbnail, cachedAt) " +
            "VALUES (1, 'Test Product', 'Desc', 'smartphones', 9.99, 1.0, 4.5, 10, '[]', 'Brand', 'SKU', 1, 1.0, 1.0, 1.0, 'Warranty', 'Shipping', 'Status', '[]', 'Return', 1, '[]', 'Thumb', 1000)"
        )

        // Insert remote_keys representing the v1 schema
        // query = "" (legacy ALL), prevKey = 1, currentPage = 2, nextKey = 3, createdAt = 1000
        db.execSQL(
            "INSERT INTO remote_keys (productId, query, prevKey, currentPage, nextKey, createdAt) " +
            "VALUES (1, '', 1, 2, 3, 1000)"
        )

        // Insert another remote_key
        // query = "CATEGORY:smartphones", prevKey = null, currentPage = 1, nextKey = 2, createdAt = 1000
        db.execSQL(
            "INSERT INTO remote_keys (productId, query, prevKey, currentPage, nextKey, createdAt) " +
            "VALUES (1, 'CATEGORY:smartphones', null, 1, 2, 1000)"
        )

        db.close()

        // Run the migration to version 2
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, ShopFlowDatabase.MIGRATION_1_2)

        // Validate products
        val productCursor = db.query("SELECT * FROM products WHERE id = 1")
        assertTrue(productCursor.moveToFirst())
        assertEquals("Test Product", productCursor.getString(productCursor.getColumnIndexOrThrow("title")))
        productCursor.close()

        // Validate remote_keys - "" converted to "ALL"
        val keysCursor1 = db.query("SELECT * FROM remote_keys WHERE query = 'ALL'")
        assertTrue(keysCursor1.moveToFirst())
        assertEquals(1, keysCursor1.getInt(keysCursor1.getColumnIndexOrThrow("productId")))
        assertTrue(keysCursor1.isNull(keysCursor1.getColumnIndexOrThrow("prevKey")))
        assertTrue(keysCursor1.isNull(keysCursor1.getColumnIndexOrThrow("nextKey")))
        
        // Assert obsolete columns are gone (should throw exception if they exist)
        assertTrue(keysCursor1.getColumnIndex("currentPage") == -1)
        assertTrue(keysCursor1.getColumnIndex("createdAt") == -1)
        keysCursor1.close()

        // Validate remote_keys - "CATEGORY:smartphones"
        val keysCursor2 = db.query("SELECT * FROM remote_keys WHERE query = 'CATEGORY:smartphones'")
        assertTrue(keysCursor2.moveToFirst())
        assertEquals(1, keysCursor2.getInt(keysCursor2.getColumnIndexOrThrow("productId")))
        assertTrue(keysCursor2.isNull(keysCursor2.getColumnIndexOrThrow("prevKey")))
        assertTrue(keysCursor2.isNull(keysCursor2.getColumnIndexOrThrow("nextKey")))
        keysCursor2.close()

        // Validate cache_context exists (should be empty since we didn't insert any and migration doesn't fabricate it)
        val contextCursor = db.query("SELECT * FROM cache_context")
        assertTrue(contextCursor.count == 0)
        contextCursor.close()

        db.close()
    }
}
