# TASK-600: Create UI state classes

- **Objective**: Create presentation contract files (UiState and Event classes) for the three main screens.
- **Scope**: ProductList, ProductDetail, and Favorites. No ViewModels or business logic.
- **Files**:
  - `ProductListUiState.kt`
  - `ProductListEvent.kt`
  - `ProductDetailUiState.kt`
  - `ProductDetailEvent.kt`
  - `FavoritesUiState.kt`
  - `FavoritesEvent.kt`
- **Architectural decisions**: MVVM-oriented presentation architecture, pure Kotlin domain models used, Paging concerns strictly decoupled from UiState, and intent-driven Event interfaces.
- **Verification**: Compilation and existing test suite run successfully.
- **Completion criteria**: All 6 files created, no unrelated modifications, tests pass.
