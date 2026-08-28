package com.rktuhin.shopflow.ui.favorites

import com.rktuhin.shopflow.domain.model.Product
import com.rktuhin.shopflow.domain.repository.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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
class FavoritesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var favoriteRepository: FakeFavoriteRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        favoriteRepository = FakeFavoriteRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = FavoritesViewModel(favoriteRepository)

    private fun createTestProduct(id: Int = 1) = Product(
        id = id, title = "Test $id", description = "", category = "", price = 0.0,
        discountPercentage = 0.0, rating = 0.0, stock = 0, tags = emptyList(), brand = "",
        sku = "", weight = 0, reviews = emptyList(), thumbnail = "", images = emptyList(),
        dimensionWidth = 0.0, dimensionHeight = 0.0, dimensionDepth = 0.0,
        warrantyInformation = "", shippingInformation = "", availabilityStatus = "",
        returnPolicy = "", minimumOrderQuantity = 0
    )

    @Test
    fun `initial state is Loading`() = testScope.runTest {
        val viewModel = createViewModel()
        
        // StateFlow initial value is available immediately without collecting
        assertTrue(viewModel.uiState.value is FavoritesUiState.Loading)
    }

    @Test
    fun `populated favorites emits Success with list`() = testScope.runTest {
        val productList = listOf(createTestProduct(1), createTestProduct(2))
        favoriteRepository.favoriteProductsFlow.value = productList

        val viewModel = createViewModel()
        val states = mutableListOf<FavoritesUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }

        advanceUntilIdle()

        val successState = states.last() as FavoritesUiState.Success
        assertEquals(productList, successState.favorites)

        job.cancel()
    }

    @Test
    fun `empty favorites emits Success with emptyList`() = testScope.runTest {
        favoriteRepository.favoriteProductsFlow.value = emptyList()

        val viewModel = createViewModel()
        val states = mutableListOf<FavoritesUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }

        advanceUntilIdle()

        val successState = states.last() as FavoritesUiState.Success
        assertEquals(emptyList<Product>(), successState.favorites)

        job.cancel()
    }

    @Test
    fun `repository Flow error emits Error state`() = testScope.runTest {
        // We simulate a flow that throws an exception
        favoriteRepository.useErrorFlow = true

        val viewModel = createViewModel()
        val states = mutableListOf<FavoritesUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }

        advanceUntilIdle()

        val errorState = states.last() as FavoritesUiState.Error
        assertEquals("Simulated DB error", errorState.message)

        job.cancel()
    }

    @Test
    fun `OnRemoveFavorite calls repository removeFavorite`() = testScope.runTest {
        val productList = listOf(createTestProduct(1), createTestProduct(2))
        favoriteRepository.favoriteProductsFlow.value = productList

        val viewModel = createViewModel()
        val states = mutableListOf<FavoritesUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }
        advanceUntilIdle()

        viewModel.onEvent(FavoritesEvent.OnRemoveFavorite(1))
        advanceUntilIdle()

        assertEquals(1, favoriteRepository.removeCallCount)
        assertEquals(1, favoriteRepository.lastRemovedProductId)

        job.cancel()
    }

    @Test
    fun `successful removal drives reactive UI update`() = testScope.runTest {
        val product1 = createTestProduct(1)
        val product2 = createTestProduct(2)
        favoriteRepository.favoriteProductsFlow.value = listOf(product1, product2)

        val viewModel = createViewModel()
        val states = mutableListOf<FavoritesUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }
        advanceUntilIdle()

        // Verify initial state
        assertEquals(2, (states.last() as FavoritesUiState.Success).favorites.size)

        // Trigger remove
        viewModel.onEvent(FavoritesEvent.OnRemoveFavorite(1))
        advanceUntilIdle()

        // Fake repo simulates the DB update by changing the flow value
        assertEquals(1, (states.last() as FavoritesUiState.Success).favorites.size)
        assertEquals(product2, (states.last() as FavoritesUiState.Success).favorites.first())

        job.cancel()
    }

    @Test
    fun `removal failure preserves existing list and sets userMessage`() = testScope.runTest {
        val productList = listOf(createTestProduct(1), createTestProduct(2))
        favoriteRepository.favoriteProductsFlow.value = productList
        favoriteRepository.shouldThrowOnRemove = true

        val viewModel = createViewModel()
        val states = mutableListOf<FavoritesUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }
        advanceUntilIdle()

        viewModel.onEvent(FavoritesEvent.OnRemoveFavorite(1))
        advanceUntilIdle()

        val lastState = states.last() as FavoritesUiState.Success
        // The list must still be the same
        assertEquals(productList, lastState.favorites)
        // A userMessage must be set
        assertEquals("Failed to remove favorite. Please try again.", lastState.userMessage)

        job.cancel()
    }

    @Test
    fun `OnUserMessageConsumed clears userMessage`() = testScope.runTest {
        val productList = listOf(createTestProduct(1))
        favoriteRepository.favoriteProductsFlow.value = productList
        favoriteRepository.shouldThrowOnRemove = true

        val viewModel = createViewModel()
        val states = mutableListOf<FavoritesUiState>()
        val job = launch { viewModel.uiState.collect { states.add(it) } }
        advanceUntilIdle()

        viewModel.onEvent(FavoritesEvent.OnRemoveFavorite(1))
        advanceUntilIdle()

        // Message should be set
        assertTrue((states.last() as FavoritesUiState.Success).userMessage != null)

        // Consume message
        viewModel.onEvent(FavoritesEvent.OnUserMessageConsumed)
        advanceUntilIdle()

        // Message should be cleared
        assertEquals(null, (states.last() as FavoritesUiState.Success).userMessage)

        job.cancel()
    }

    @Test
    fun `repeated remove events result in exact repository invocation count`() = testScope.runTest {
        val productList = listOf(createTestProduct(1))
        favoriteRepository.favoriteProductsFlow.value = productList

        val viewModel = createViewModel()
        val job = launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        viewModel.onEvent(FavoritesEvent.OnRemoveFavorite(1))
        viewModel.onEvent(FavoritesEvent.OnRemoveFavorite(2))
        viewModel.onEvent(FavoritesEvent.OnRemoveFavorite(1))
        advanceUntilIdle()

        assertEquals(3, favoriteRepository.removeCallCount)

        job.cancel()
    }

    class FakeFavoriteRepository : FavoriteRepository {
        val favoriteProductsFlow = MutableStateFlow<List<Product>>(emptyList())
        var useErrorFlow = false

        var removeCallCount = 0
        var lastRemovedProductId: Int? = null
        var shouldThrowOnRemove = false

        override fun observeFavoriteState(productId: Int): Flow<Boolean> = throw NotImplementedError()

        override fun getFavoriteProducts(): Flow<List<Product>> {
            if (useErrorFlow) {
                return kotlinx.coroutines.flow.flow {
                    throw Exception("Simulated DB error")
                }
            }
            return favoriteProductsFlow
        }

        override suspend fun addFavorite(productId: Int) = throw NotImplementedError()

        override suspend fun removeFavorite(productId: Int) {
            removeCallCount++
            lastRemovedProductId = productId
            if (shouldThrowOnRemove) {
                throw Exception("DB error")
            }
            // Simulate the DB update reacting to removal
            favoriteProductsFlow.value = favoriteProductsFlow.value.filter { it.id != productId }
        }
    }
}
