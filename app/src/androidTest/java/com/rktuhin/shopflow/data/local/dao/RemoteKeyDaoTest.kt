package com.rktuhin.shopflow.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rktuhin.shopflow.data.local.ShopFlowDatabase
import com.rktuhin.shopflow.data.local.entity.RemoteKeyEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteKeyDaoTest {

    private lateinit var database: ShopFlowDatabase
    private lateinit var remoteKeyDao: RemoteKeyDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShopFlowDatabase::class.java
        ).allowMainThreadQueries().build()
        remoteKeyDao = database.remoteKeyDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun upsertAndGetRemoteKey_returnsCorrectKey() = runTest {
        val key = RemoteKeyEntity(productId = 1, query = "ALL", prevKey = null, currentPage = 1, nextKey = 2, createdAt = 1000L)
        remoteKeyDao.upsertAll(listOf(key))

        val retrieved = remoteKeyDao.getRemoteKey(1, "ALL")
        assertEquals(key, retrieved)
    }

    @Test
    fun remoteKeys_areIsolatedByCacheContext() = runTest {
        val keyAll = RemoteKeyEntity(productId = 1, query = "ALL", prevKey = null, currentPage = 1, nextKey = 2, createdAt = 1000L)
        val keyCat = RemoteKeyEntity(productId = 1, query = "CATEGORY:smartphones", prevKey = null, currentPage = 1, nextKey = 5, createdAt = 1000L)

        remoteKeyDao.upsertAll(listOf(keyAll, keyCat))

        val retrievedAll = remoteKeyDao.getRemoteKey(1, "ALL")
        val retrievedCat = remoteKeyDao.getRemoteKey(1, "CATEGORY:smartphones")

        assertEquals(2, retrievedAll?.nextKey)
        assertEquals(5, retrievedCat?.nextKey)
    }

    @Test
    fun clearRemoteKeys_removesOnlyRequestedContext() = runTest {
        val keyAll = RemoteKeyEntity(productId = 1, query = "ALL", prevKey = null, currentPage = 1, nextKey = 2, createdAt = 1000L)
        val keyCat = RemoteKeyEntity(productId = 1, query = "CATEGORY:smartphones", prevKey = null, currentPage = 1, nextKey = 5, createdAt = 1000L)
        val keyCat2 = RemoteKeyEntity(productId = 1, query = "CATEGORY:laptops", prevKey = null, currentPage = 1, nextKey = 3, createdAt = 1000L)

        remoteKeyDao.upsertAll(listOf(keyAll, keyCat, keyCat2))

        remoteKeyDao.clearRemoteKeys("CATEGORY:smartphones")

        assertNull(remoteKeyDao.getRemoteKey(1, "CATEGORY:smartphones"))
        assertEquals(2, remoteKeyDao.getRemoteKey(1, "ALL")?.nextKey)
        assertEquals(3, remoteKeyDao.getRemoteKey(1, "CATEGORY:laptops")?.nextKey)
    }

    @Test
    fun upsertAll_updatesExistingKey() = runTest {
        val key = RemoteKeyEntity(productId = 1, query = "ALL", prevKey = null, currentPage = 1, nextKey = 2, createdAt = 1000L)
        remoteKeyDao.upsertAll(listOf(key))

        val updatedKey = RemoteKeyEntity(productId = 1, query = "ALL", prevKey = null, currentPage = 1, nextKey = 3, createdAt = 2000L)
        remoteKeyDao.upsertAll(listOf(updatedKey))

        val retrieved = remoteKeyDao.getRemoteKey(1, "ALL")
        assertEquals(3, retrieved?.nextKey)
        assertEquals(2000L, retrieved?.createdAt)
    }

    @Test
    fun clearAll_removesAllKeys() = runTest {
        val keyAll = RemoteKeyEntity(productId = 1, query = "ALL", prevKey = null, currentPage = 1, nextKey = 2, createdAt = 1000L)
        val keyCat = RemoteKeyEntity(productId = 1, query = "CATEGORY:smartphones", prevKey = null, currentPage = 1, nextKey = 5, createdAt = 1000L)

        remoteKeyDao.upsertAll(listOf(keyAll, keyCat))
        remoteKeyDao.clearAll()

        assertNull(remoteKeyDao.getRemoteKey(1, "ALL"))
        assertNull(remoteKeyDao.getRemoteKey(1, "CATEGORY:smartphones"))
    }
}
