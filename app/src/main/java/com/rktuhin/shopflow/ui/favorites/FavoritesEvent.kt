package com.rktuhin.shopflow.ui.favorites

sealed interface FavoritesEvent {
    data class OnRemoveFavorite(val productId: Int) : FavoritesEvent
}
