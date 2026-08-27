package com.rktuhin.shopflow.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rktuhin.shopflow.data.local.ShopFlowDatabase
import com.rktuhin.shopflow.data.local.entity.CacheContextEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CacheContextDaoTest {

    private lateinit var database: ShopFlowDatabase
    private lateinit var cacheContextDao: CacheContextDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShopFlowDatabase::class.java
        ).allowMainThreadQueries().build()
        cacheContextDao = database.cacheContextDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun getContext_returnsNullIfNotFound() = runTest {
        val result = cacheContextDao.getContext("ALL")
        assertNull(result)
    }

    @Test
    fun upsertAndGetContext_returnsCorrectContext() = runTest {
        val context = CacheContextEntity(query = "ALL", lastUpdated = 1000L)
        cacheContextDao.upsert(context)

        val result = cacheContextDao.getContext("ALL")
        assertEquals(context, result)
    }

    @Test
    fun upsert_updatesExistingContext() = runTest {
        val context = CacheContextEntity(query = "CATEGORY:smartphones", lastUpdated = 1000L)
        cacheContextDao.upsert(context)

        val updated = CacheContextEntity(query = "CATEGORY:smartphones", lastUpdated = 2000L)
        cacheContextDao.upsert(updated)

        val result = cacheContextDao.getContext("CATEGORY:smartphones")
        assertEquals(2000L, result?.lastUpdated)
    }
}
