# TASK-504: Create Hilt Repository Module

**Status**: DONE

## Description
Implement Hilt dependency injection for the repository layer by creating `RepositoryModule`. Bind `ProductRepository` and `FavoriteRepository` to their respective implementations.

## Acceptance Criteria
- [x] `RepositoryModule.kt` created.
- [x] Bindings for `ProductRepository` and `FavoriteRepository` added using `@Binds`.
- [x] No `@Provides` or `@Singleton` used.
- [x] Project compiles successfully.
