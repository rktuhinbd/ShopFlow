package com.rktuhin.shopflow.ui.productdetail

sealed interface ProductDetailEvent {
    data object OnToggleFavorite : ProductDetailEvent
    data object OnRetry : ProductDetailEvent
    data object OnUserMessageConsumed : ProductDetailEvent
}
