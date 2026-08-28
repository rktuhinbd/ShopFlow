package com.rktuhin.shopflow.ui.productlist

import com.rktuhin.shopflow.domain.model.Category

data class ProductListUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategorySlug: String? = null,
    val searchQuery: String = "",
    val favoriteProductIds: Set<Int> = emptySet(),
    val userMessage: String? = null
)
