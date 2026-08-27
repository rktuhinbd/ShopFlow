package com.rktuhin.shopflow.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rktuhin.shopflow.data.local.ShopFlowDatabase
import com.rktuhin.shopflow.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {

    private lateinit var database: ShopFlowDatabase
    private lateinit var categoryDao: CategoryDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShopFlowDatabase::class.java
        ).allowMainThreadQueries().build()
        categoryDao = database.categoryDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun upsertAll_insertsAndUpdatesCategories() = runTest {
        val cat1 = CategoryEntity(slug = "cat1", name = "Category 1", url = "url1", cachedAt = 1000L)
        categoryDao.upsertAll(listOf(cat1))
        
        val cat1Updated = CategoryEntity(slug = "cat1", name = "Updated Name", url = "url1", cachedAt = 1000L)
        categoryDao.upsertAll(listOf(cat1Updated))
        
        val list = categoryDao.observeAllCategories().first()
        assertEquals(1, list.size)
        assertEquals("Updated Name", list[0].name)
    }

    @Test
    fun observeAllCategories_returnsOrderedList() = runTest {
        val catB = CategoryEntity(slug = "b", name = "B", url = "", cachedAt = 1000L)
        val catC = CategoryEntity(slug = "c", name = "C", url = "", cachedAt = 1000L)
        val catA = CategoryEntity(slug = "a", name = "A", url = "", cachedAt = 1000L)
        
        categoryDao.upsertAll(listOf(catB, catC, catA))
        
        val list = categoryDao.observeAllCategories().first()
        assertEquals(listOf(catA, catB, catC), list) // ORDER BY name ASC
    }

    @Test
    fun clearAll_removesAllCategories() = runTest {
        val catA = CategoryEntity(slug = "a", name = "A", url = "", cachedAt = 1000L)
        categoryDao.upsertAll(listOf(catA))
        
        categoryDao.clearAll()
        
        val list = categoryDao.observeAllCategories().first()
        assertTrue(list.isEmpty())
    }
}
