package com.rktuhin.shopflow.ui.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rktuhin.shopflow.domain.model.Product
import com.rktuhin.shopflow.domain.repository.FavoriteRepository
import com.rktuhin.shopflow.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private data class FilterState(
        val searchQuery: String = "",
        val categorySlug: String? = null
    )

    private val filterState = MutableStateFlow(FilterState())
    private val userMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProductListUiState> = combine(
        productRepository.getCategories(),
        filterState,
        favoriteRepository.getFavoriteProducts().map { list -> list.map { it.id }.toSet() },
        userMessage
    ) { categories, filter, favoriteIds, message ->
        ProductListUiState(
            categories = categories,
            selectedCategorySlug = filter.categorySlug,
            searchQuery = filter.searchQuery,
            favoriteProductIds = favoriteIds,
            userMessage = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductListUiState()
    )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val products: Flow<PagingData<Product>> = filterState
        .map { it.copy(searchQuery = it.searchQuery.trim()) }
        .debounce { state ->
            if (state.searchQuery.isNotBlank() && state.categorySlug == null) 300L else 0L
        }
        .distinctUntilChanged()
        .flatMapLatest { state ->
            when {
                state.searchQuery.isNotBlank() -> productRepository.searchProducts(state.searchQuery)
                state.categorySlug != null -> productRepository.getProductsByCategory(state.categorySlug)
                else -> productRepository.getProducts()
            }
        }
        .cachedIn(viewModelScope)

    init {
        fetchCategories()
    }

    fun onEvent(event: ProductListEvent) {
        when (event) {
            is ProductListEvent.OnSearchQueryChanged -> {
                filterState.update { it.copy(searchQuery = event.query, categorySlug = null) }
            }
            is ProductListEvent.OnClearSearch -> {
                filterState.update { it.copy(searchQuery = "", categorySlug = null) }
            }
            is ProductListEvent.OnCategorySelected -> {
                filterState.update { it.copy(searchQuery = "", categorySlug = event.categorySlug) }
            }
            is ProductListEvent.OnToggleFavorite -> {
                toggleFavorite(event.productId)
            }
            is ProductListEvent.OnUserMessageConsumed -> {
                userMessage.value = null
            }
            is ProductListEvent.OnRetryCategoryFetch -> {
                fetchCategories()
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                productRepository.fetchCategories()
            } catch (e: Exception) {
                userMessage.value = "Failed to load categories. Please try again."
            }
        }
    }

    private fun toggleFavorite(productId: Int) {
        viewModelScope.launch {
            try {
                val isFavorite = uiState.value.favoriteProductIds.contains(productId)
                if (isFavorite) {
                    favoriteRepository.removeFavorite(productId)
                } else {
                    favoriteRepository.addFavorite(productId)
                }
            } catch (e: Exception) {
                userMessage.value = "Failed to update favorite status."
            }
        }
    }
}
