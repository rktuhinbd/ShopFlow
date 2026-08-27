package com.rktuhin.shopflow.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ShopFlowDatabaseTest {
    private lateinit var db: ShopFlowDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ShopFlowDatabase::class.java
        ).build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun database_isInstantiatedAndProvidesDaos() {
        // Assert that the database is created successfully
        assertNotNull(db)

        // Assert that all DAOs are available
        assertNotNull(db.productDao())
        assertNotNull(db.favoriteDao())
        assertNotNull(db.categoryDao())
        assertNotNull(db.remoteKeyDao())
    }
}
