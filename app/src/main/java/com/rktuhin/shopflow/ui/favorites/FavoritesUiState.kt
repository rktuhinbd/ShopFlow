package com.rktuhin.shopflow.ui.favorites

import com.rktuhin.shopflow.domain.model.Product

sealed interface FavoritesUiState {
    data object Loading : FavoritesUiState
    
    data class Success(
        val favorites: List<Product>,
        val userMessage: String? = null
    ) : FavoritesUiState
    
    data class Error(val message: String) : FavoritesUiState
}
