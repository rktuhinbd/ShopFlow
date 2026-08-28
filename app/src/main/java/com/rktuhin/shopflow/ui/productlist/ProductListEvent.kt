package com.rktuhin.shopflow.ui.productlist

sealed interface ProductListEvent {
    data class OnSearchQueryChanged(val query: String) : ProductListEvent
    data object OnClearSearch : ProductListEvent
    data class OnCategorySelected(val categorySlug: String?) : ProductListEvent
    data class OnToggleFavorite(val productId: Int) : ProductListEvent
    data object OnUserMessageConsumed : ProductListEvent
    data object OnRetryCategoryFetch : ProductListEvent
}
