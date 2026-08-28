# TASK-601: Product List ViewModel

## Status
DONE

## Description
Implemented ProductListViewModel for the ShopFlow application. It coordinates ProductRepository and FavoriteRepository, exposing `ProductListUiState` and a `PagingData<Product>` flow.

## Implementation Details
- Used a single internal `FilterState` to atomically manage search query and selected category slug.
- Implemented a dynamic debounce that delays active search queries by 300ms but bypasses the delay for category changes or clears.
- Guaranteed mutual exclusion: searching clears the category, and selecting a category clears the search query.
- Exposed transient user messages for non-Paging errors via `ProductListUiState.userMessage`.
- Used `cachedIn(viewModelScope)` to preserve the Paging stream across configuration changes.
- Tested extensively with `kotlinx-coroutines-test`.
