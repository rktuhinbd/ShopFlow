package com.rktuhin.shopflow.ui.productdetail

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.rktuhin.shopflow.domain.model.Category
import com.rktuhin.shopflow.domain.model.Product
import com.rktuhin.shopflow.domain.repository.FavoriteRepository
import com.rktuhin.shopflow.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    
    private lateinit var productRepository: FakeProductRepository
    private lateinit var favoriteRepository: FakeFavoriteRepository
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        productRepository = FakeProductRepository()
        favoriteRepository = FakeFavoriteRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(productId: Int = 1): ProductDetailViewModel {
        return ProductDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("productId" to productId)),
            productRepository = productRepository,
            favoriteRepository = favoriteRepository
        )
    }

    private fun createTestProduct(id: Int = 1) = Product(
        id = id, title = "Test", description = "", category = "", price = 0.0, 
        discountPercentage = 0.0, rating = 0.0, stock = 0, tags = emptyList(), brand = "", 
        sku = "", weight = 0, reviews = emptyList(), thumbnail = "", images = emptyList(), 
        dimensionWidth = 0.0, dimensionHeight = 0.0, dimensionDepth = 0.0, 
        warrantyInformation = "", shippingInformation = "", availabilityStatus = "", 
        returnPolicy = "", minimumOrderQuantity = 0
    )

    @Test
    fun `initial cache hit emits Success without fetching`() = testScope.runTest {
        val product = createTestProduct()
        productRepository.productFlow.value = product
        favoriteRepository.favoriteFlow.value = false

        val viewModel = createViewModel()
        
        val states = mutableListOf<ProductDetailUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }
        advanceUntilIdle()
        
        val state = states.last() as ProductDetailUiState.Success
        assertEquals(product, state.product)
        assertEquals(false, state.isFavorite)
        assertEquals(0, productRepository.fetchCount)
        
        job.cancel()
    }

    @Test
    fun `missing product triggers exactly one fetch and emits Loading then Success`() = testScope.runTest {
        val viewModel = createViewModel()
        
        val states = mutableListOf<ProductDetailUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }
        
        // Let initialization happen
        advanceUntilIdle()
        
        // Initial state is Loading
        assertTrue(states.first() is ProductDetailUiState.Loading)
        assertEquals(1, productRepository.fetchCount)
        
        // Simulate successful fetch updating Room
        val product = createTestProduct()
        productRepository.productFlow.value = product
        advanceUntilIdle()
        
        val successState = states.last() as ProductDetailUiState.Success
        assertEquals(product, successState.product)
        
        job.cancel()
    }
    
    @Test
    fun `repeated null emissions trigger exactly one fetch`() = testScope.runTest {
        val viewModel = createViewModel()
        val job = launch { viewModel.uiState.collect {} }
        
        advanceUntilIdle()
        assertEquals(1, productRepository.fetchCount)
        
        // Emit null again
        productRepository.productFlow.value = null
        advanceUntilIdle()
        
        // Should still be 1 fetch
        assertEquals(1, productRepository.fetchCount)
        
        job.cancel()
    }

    @Test
    fun `fetch failure emits Error state`() = testScope.runTest {
        productRepository.shouldThrowOnFetch = true
        val viewModel = createViewModel()
        val states = mutableListOf<ProductDetailUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }
        
        advanceUntilIdle()
        
        val errorState = states.last() as ProductDetailUiState.Error
        assertEquals("Network error", errorState.message)
        
        job.cancel()
    }
    
    @Test
    fun `repeated null after failure does not automatically retry`() = testScope.runTest {
        productRepository.shouldThrowOnFetch = true
        val viewModel = createViewModel()
        val job = launch { viewModel.uiState.collect {} }
        
        advanceUntilIdle()
        assertEquals(1, productRepository.fetchCount)
        
        // Emit null again
        productRepository.productFlow.value = null
        advanceUntilIdle()
        
        // Still 1 fetch
        assertEquals(1, productRepository.fetchCount)
        
        job.cancel()
    }

    @Test
    fun `explicit OnRetry triggers exactly one new fetch`() = testScope.runTest {
        productRepository.shouldThrowOnFetch = true
        val viewModel = createViewModel()
        val states = mutableListOf<ProductDetailUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }
        
        advanceUntilIdle()
        assertTrue(states.last() is ProductDetailUiState.Error)
        assertEquals(1, productRepository.fetchCount)
        
        // Retry
        productRepository.shouldThrowOnFetch = false
        viewModel.onEvent(ProductDetailEvent.OnRetry)
        advanceUntilIdle()
        
        assertEquals(2, productRepository.fetchCount)
        
        val product = createTestProduct()
        productRepository.productFlow.value = product
        advanceUntilIdle()
        
        assertTrue(states.last() is ProductDetailUiState.Success)
        
        job.cancel()
    }
    
    @Test
    fun `concurrent OnRetry drops duplicate fetch`() = testScope.runTest {
        val viewModel = createViewModel()
        val job = launch { viewModel.uiState.collect {} }
        
        viewModel.onEvent(ProductDetailEvent.OnRetry)
        viewModel.onEvent(ProductDetailEvent.OnRetry)
        
        advanceUntilIdle()
        
        // Initial missing product fetch + 0 retries (because it was already loading)
        assertEquals(1, productRepository.fetchCount)
        
        job.cancel()
    }

    @Test
    fun `favorite toggle success updates state without new fetch`() = testScope.runTest {
        val product = createTestProduct()
        productRepository.productFlow.value = product
        favoriteRepository.favoriteFlow.value = false
        
        val viewModel = createViewModel()
        val states = mutableListOf<ProductDetailUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }
        
        advanceUntilIdle()
        val initial = states.last() as ProductDetailUiState.Success
        assertEquals(false, initial.isFavorite)
        
        viewModel.onEvent(ProductDetailEvent.OnToggleFavorite)
        advanceUntilIdle()
        
        assertEquals(1, favoriteRepository.addCallCount)
        favoriteRepository.favoriteFlow.value = true
        advanceUntilIdle()
        
        val updated = states.last() as ProductDetailUiState.Success
        assertEquals(true, updated.isFavorite)
        assertEquals(0, productRepository.fetchCount)
        
        job.cancel()
    }
    
    @Test
    fun `favorite toggle failure populates userMessage and product remains visible`() = testScope.runTest {
        val product = createTestProduct()
        productRepository.productFlow.value = product
        favoriteRepository.favoriteFlow.value = false
        favoriteRepository.shouldThrowOnToggle = true
        
        val viewModel = createViewModel()
        val states = mutableListOf<ProductDetailUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }
        
        advanceUntilIdle()
        
        viewModel.onEvent(ProductDetailEvent.OnToggleFavorite)
        advanceUntilIdle()
        
        val errorState = states.last() as ProductDetailUiState.Success
        assertEquals("Failed to update favorite status.", errorState.userMessage)
        assertEquals(false, errorState.isFavorite)
        
        viewModel.onEvent(ProductDetailEvent.OnUserMessageConsumed)
        advanceUntilIdle()
        
        val clearedState = states.last() as ProductDetailUiState.Success
        assertEquals(null, clearedState.userMessage)
        
        job.cancel()
    }

    @Test(expected = IllegalStateException::class)
    fun `missing SavedStateHandle productId throws exception`() {
        ProductDetailViewModel(
            SavedStateHandle(),
            productRepository,
            favoriteRepository
        )
    }

    class FakeProductRepository : ProductRepository {
        val productFlow = MutableStateFlow<Product?>(null)
        var fetchCount = 0
        var shouldThrowOnFetch = false
        
        override fun getProducts(): Flow<PagingData<Product>> = throw NotImplementedError()
        override fun getProductsByCategory(categorySlug: String): Flow<PagingData<Product>> = throw NotImplementedError()
        override fun searchProducts(query: String): Flow<PagingData<Product>> = throw NotImplementedError()
        
        override fun getProductById(id: Int): Flow<Product?> = productFlow
        
        override fun getCategories(): Flow<List<Category>> = throw NotImplementedError()
        
        override suspend fun fetchProduct(id: Int) {
            fetchCount++
            if (shouldThrowOnFetch) throw Exception("Network error")
        }
        
        override suspend fun fetchCategories() = throw NotImplementedError()
    }
    
    class FakeFavoriteRepository : FavoriteRepository {
        val favoriteFlow = MutableStateFlow(false)
        var addCallCount = 0
        var removeCallCount = 0
        var shouldThrowOnToggle = false
        
        override fun observeFavoriteState(productId: Int): Flow<Boolean> = favoriteFlow
        override fun getFavoriteProducts(): Flow<List<Product>> = throw NotImplementedError()
        
        override suspend fun addFavorite(productId: Int) {
            if (shouldThrowOnToggle) throw Exception("DB error")
            addCallCount++
        }
        
        override suspend fun removeFavorite(productId: Int) {
            if (shouldThrowOnToggle) throw Exception("DB error")
            removeCallCount++
        }
    }
}
