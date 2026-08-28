package com.rktuhin.shopflow.ui.productdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rktuhin.shopflow.domain.repository.FavoriteRepository
import com.rktuhin.shopflow.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val productId: Int = checkNotNull(savedStateHandle.get<Int>("productId")) {
        "productId is required"
    }

    private sealed interface FetchState {
        data object Idle : FetchState
        data object Loading : FetchState
        data object Success : FetchState
        data class Error(val message: String) : FetchState
    }

    private val fetchState = MutableStateFlow<FetchState>(FetchState.Idle)
    private val userMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProductDetailUiState> = combine(
        productRepository.getProductById(productId)
            .onEach { product ->
                if (product == null && fetchState.value == FetchState.Idle) {
                    attemptFetch()
                } else if (product != null && fetchState.value == FetchState.Loading) {
                    fetchState.value = FetchState.Success
                }
            },
        favoriteRepository.observeFavoriteState(productId),
        fetchState,
        userMessage
    ) { product, isFavorite, fetch, message ->
        if (product != null) {
            ProductDetailUiState.Success(product, isFavorite, message)
        } else {
            when (fetch) {
                is FetchState.Error -> ProductDetailUiState.Error(fetch.message)
                else -> ProductDetailUiState.Loading
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductDetailUiState.Loading
    )

    fun onEvent(event: ProductDetailEvent) {
        when (event) {
            is ProductDetailEvent.OnToggleFavorite -> toggleFavorite()
            is ProductDetailEvent.OnRetry -> attemptFetch()
            is ProductDetailEvent.OnUserMessageConsumed -> userMessage.value = null
        }
    }

    private fun attemptFetch() {
        if (fetchState.value == FetchState.Loading) return
        
        fetchState.value = FetchState.Loading
        viewModelScope.launch {
            try {
                productRepository.fetchProduct(productId)
            } catch (e: Exception) {
                fetchState.value = FetchState.Error(e.message ?: "Failed to load product")
            }
        }
    }

    private fun toggleFavorite() {
        val currentState = uiState.value
        if (currentState is ProductDetailUiState.Success) {
            viewModelScope.launch {
                try {
                    if (currentState.isFavorite) {
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
}
