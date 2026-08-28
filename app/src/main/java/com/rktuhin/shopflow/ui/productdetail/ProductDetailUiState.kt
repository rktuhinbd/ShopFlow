package com.rktuhin.shopflow.ui.productdetail

import com.rktuhin.shopflow.domain.model.Product

sealed interface ProductDetailUiState {
    data object Loading : ProductDetailUiState
    
    data class Success(
        val product: Product,
        val isFavorite: Boolean,
        val userMessage: String? = null
    ) : ProductDetailUiState
    
    data class Error(val message: String) : ProductDetailUiState
}
