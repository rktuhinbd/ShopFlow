# TASK-603: Create FavoritesViewModel

**Status**: DONE
**Date Completed**: 2026-08-28

## Objective
Implement the `FavoritesViewModel` to coordinate UI state for the Favorites screen, adhering strictly to the offline-first repository layer as the single source of truth.

## Scope
- Create `FavoritesViewModel.kt`
- Expand `FavoritesUiState.kt` and `FavoritesEvent.kt`
- Create `FavoritesViewModelTest.kt`

## Implementation Summary
- **Source of Truth**: The ViewModel uses `favoriteRepository.getFavoriteProducts()` as its absolute source of truth. The repository Flow is combined with transient `userMessage` state.
- **StateFlow Lifecycle**: The combined Flow is converted to a StateFlow using `stateIn` with `SharingStarted.WhileSubscribed(5000)`.
- **RemoveFavorite Behavior**: Delegated entirely to `favoriteRepository.removeFavorite(productId)` inside `viewModelScope.launch`. No optimistic UI updates are performed.
- **Error Handling**: Repository errors are safely caught via `.catch { emit(FavoritesUiState.Error(...)) }`. Transient removal failures update `userMessage` without destroying the existing list.

## Test Summary
- **Exact test count**: 9 tests
- Tests fully verify Loading states, populated lists, empty list, `.catch` mapping, exact repository invocation, reactive Flow updates, and resilient error recovery via UserMessage.
- **Full build result**: BUILD SUCCESSFUL (assembleDebug and testDebugUnitTest completed).
