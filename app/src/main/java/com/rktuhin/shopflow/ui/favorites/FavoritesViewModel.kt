package com.rktuhin.shopflow.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rktuhin.shopflow.domain.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val userMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<FavoritesUiState> = favoriteRepository.getFavoriteProducts()
        .combine(userMessage) { favorites, message ->
            FavoritesUiState.Success(favorites, message) as FavoritesUiState
        }
        .catch { e ->
            emit(FavoritesUiState.Error(e.message ?: "An unexpected error occurred."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoritesUiState.Loading
        )

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.OnRemoveFavorite -> removeFavorite(event.productId)
            is FavoritesEvent.OnUserMessageConsumed -> userMessage.value = null
        }
    }

    private fun removeFavorite(productId: Int) {
        viewModelScope.launch {
            try {
                favoriteRepository.removeFavorite(productId)
            } catch (e: Exception) {
                userMessage.value = "Failed to remove favorite. Please try again."
            }
        }
    }
}
