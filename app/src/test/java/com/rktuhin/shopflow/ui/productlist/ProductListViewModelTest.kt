package com.rktuhin.shopflow.ui.productlist

import androidx.paging.PagingData
import com.rktuhin.shopflow.domain.model.Category
import com.rktuhin.shopflow.domain.model.Product
import com.rktuhin.shopflow.domain.repository.FavoriteRepository
import com.rktuhin.shopflow.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var fakeProductRepository: FakeProductRepository
    private lateinit var fakeFavoriteRepository: FakeFavoriteRepository
    private lateinit var viewModel: ProductListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeProductRepository = FakeProductRepository()
        fakeFavoriteRepository = FakeFavoriteRepository()
        viewModel = ProductListViewModel(fakeProductRepository, fakeFavoriteRepository)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct and categories are fetched`() = testScope.runTest {
        val states = mutableListOf<ProductListUiState>()
        val job = launch {
            viewModel.uiState.collect { states.add(it) }
        }

        // Wait for init block coroutines
        advanceTimeBy(1)

        val currentState = viewModel.uiState.value
        assertEquals("", currentState.searchQuery)
        assertNull(currentState.selectedCategorySlug)
        assertTrue(currentState.categories.isNotEmpty()) // Fake emits categories
        assertEquals("electronics", currentState.categories[0].slug)
        assertEquals(1, fakeProductRepository.fetchCategoriesCallCount)

        job.cancel()
    }

    @Test
    fun `category selection updates state and clears search`() = testScope.runTest {
        val job = launch { viewModel.uiState.collect {} }
        viewModel.onEvent(ProductListEvent.OnSearchQueryChanged("phone"))
        advanceTimeBy(1)
        assertEquals("phone", viewModel.uiState.value.searchQuery)

        viewModel.onEvent(ProductListEvent.OnCategorySelected("smartphones"))
        advanceTimeBy(1)

        assertEquals("smartphones", viewModel.uiState.value.selectedCategorySlug)
        assertEquals("", viewModel.uiState.value.searchQuery)
        job.cancel()
    }

    @Test
    fun `search query updates state and clears category`() = testScope.runTest {
        val job = launch { viewModel.uiState.collect {} }
        viewModel.onEvent(ProductListEvent.OnCategorySelected("smartphones"))
        advanceTimeBy(1)
        assertEquals("smartphones", viewModel.uiState.value.selectedCategorySlug)

        viewModel.onEvent(ProductListEvent.OnSearchQueryChanged("laptop"))
        advanceTimeBy(1)

        assertEquals("laptop", viewModel.uiState.value.searchQuery)
        assertNull(viewModel.uiState.value.selectedCategorySlug)
        job.cancel()
    }

    @Test
    fun `search debounce waits 300ms before calling searchProducts`() = testScope.runTest {
        val pagingStates = mutableListOf<PagingData<Product>>()
        val job = launch {
            viewModel.products.collect { pagingStates.add(it) }
        }

        advanceTimeBy(1)
        // Initial ALL catalog
        assertEquals(1, fakeProductRepository.getProductsCallCount)

        viewModel.onEvent(ProductListEvent.OnSearchQueryChanged("app"))
        advanceTimeBy(150)
        
        // At 150ms, searchProducts shouldn't be called yet
        assertEquals(0, fakeProductRepository.searchProductsCallCount)

        viewModel.onEvent(ProductListEvent.OnSearchQueryChanged("apple"))
        advanceTimeBy(301)
        
        // After 301ms from the last keystroke, it should be called once with "apple"
        assertEquals(1, fakeProductRepository.searchProductsCallCount)
        assertEquals("apple", fakeProductRepository.lastSearchQuery)

        job.cancel()
    }

    @Test
    fun `category switch bypasses search debounce`() = testScope.runTest {
        val pagingStates = mutableListOf<PagingData<Product>>()
        val job = launch {
            viewModel.products.collect { pagingStates.add(it) }
        }

        advanceTimeBy(1)
        assertEquals(1, fakeProductRepository.getProductsCallCount)

        viewModel.onEvent(ProductListEvent.OnCategorySelected("laptops"))
        advanceTimeBy(1) // Immediate
        
        assertEquals(1, fakeProductRepository.getProductsByCategoryCallCount)
        assertEquals("laptops", fakeProductRepository.lastCategorySlug)

        job.cancel()
    }

    @Test
    fun `toggle favorite updates state`() = testScope.runTest {
        val states = mutableListOf<ProductListUiState>()
        val job = launch {
            viewModel.uiState.collect { states.add(it) }
        }

        advanceTimeBy(1)
        assertTrue(viewModel.uiState.value.favoriteProductIds.isEmpty())

        viewModel.onEvent(ProductListEvent.OnToggleFavorite(1))
        advanceTimeBy(1)
        
        assertTrue(viewModel.uiState.value.favoriteProductIds.contains(1))

        viewModel.onEvent(ProductListEvent.OnToggleFavorite(1))
        advanceTimeBy(1)
        
        assertTrue(viewModel.uiState.value.favoriteProductIds.isEmpty())

        job.cancel()
    }

    @Test
    fun `category fetch failure sets userMessage`() = testScope.runTest {
        val job = launch { viewModel.uiState.collect {} }
        fakeProductRepository.shouldFailCategories = true
        
        viewModel.onEvent(ProductListEvent.OnRetryCategoryFetch)
        advanceTimeBy(1)
        
        assertEquals("Failed to load categories. Please try again.", viewModel.uiState.value.userMessage)
        
        viewModel.onEvent(ProductListEvent.OnUserMessageConsumed)
        advanceTimeBy(1)
        assertNull(viewModel.uiState.value.userMessage)
        job.cancel()
    }
}

class FakeProductRepository : ProductRepository {
    var fetchCategoriesCallCount = 0
    var getProductsCallCount = 0
    var searchProductsCallCount = 0
    var getProductsByCategoryCallCount = 0
    
    var lastSearchQuery = ""
    var lastCategorySlug = ""
    var shouldFailCategories = false
    
    override fun getProducts(): Flow<PagingData<Product>> {
        getProductsCallCount++
        return flowOf(PagingData.empty())
    }

    override fun getProductsByCategory(categorySlug: String): Flow<PagingData<Product>> {
        getProductsByCategoryCallCount++
        lastCategorySlug = categorySlug
        return flowOf(PagingData.empty())
    }

    override fun searchProducts(query: String): Flow<PagingData<Product>> {
        searchProductsCallCount++
        lastSearchQuery = query
        return flowOf(PagingData.empty())
    }

    override fun getProductById(id: Int): Flow<Product?> {
        return flowOf(null)
    }

    override fun getCategories(): Flow<List<Category>> {
        return flowOf(listOf(Category(slug = "electronics", name = "Electronics", url = "")))
    }

    override suspend fun fetchProduct(id: Int) {}

    override suspend fun fetchCategories() {
        fetchCategoriesCallCount++
        if (shouldFailCategories) {
            throw Exception("Network Error")
        }
    }
}

class FakeFavoriteRepository : FavoriteRepository {
    private val favorites = MutableStateFlow<List<Product>>(emptyList())

    override fun observeFavoriteState(productId: Int): Flow<Boolean> = flowOf(false)

    override fun getFavoriteProducts(): Flow<List<Product>> = favorites

    override suspend fun addFavorite(productId: Int) {
        val current = favorites.value.toMutableList()
        current.add(Product(id = productId, title = "Test", description = "", category = "", price = 0.0, discountPercentage = 0.0, rating = 0.0, stock = 0, tags = emptyList(), brand = "", sku = "", weight = 0, reviews = emptyList(), thumbnail = "", images = emptyList(), dimensionWidth = 0.0, dimensionHeight = 0.0, dimensionDepth = 0.0, warrantyInformation = "", shippingInformation = "", availabilityStatus = "", returnPolicy = "", minimumOrderQuantity = 0))
        favorites.value = current
    }

    override suspend fun removeFavorite(productId: Int) {
        val current = favorites.value.toMutableList()
        current.removeIf { it.id == productId }
        favorites.value = current
    }
}
