# Engineering Standards

To ensure a maintainable, robust, and readable codebase, ShopFlow strictly adheres to the following engineering standards.

## 1. Kotlin Idioms

- Favor immutability (`val` over `var`).
- Use Kotlin standard library functions (`map`, `filter`, `let`, `apply`) where appropriate to reduce boilerplate.
- Avoid nulls where possible; use sealed classes or Result wrappers for state representation.

## 2. Architecture & State Management

- **Single Source of Truth**: The local database (Room) is the single source of truth. The UI observes the database.
- **Immutable UI State**: ViewModels must expose state as an immutable `StateFlow` (e.g., `StateFlow<ProductListUiState>`).
- **Unidirectional Data Flow (UDF)**: UI sends events to the ViewModel; ViewModel mutates internal state; UI observes exposed immutable state.
- **Repository Pattern**: ViewModels must interact with Repositories or Use Cases, never directly with Retrofit or Room DAOs.

## 3. Concurrency (Coroutines & Flow)

- **Structured Concurrency**: Launch coroutines within `viewModelScope` or `lifecycleScope`. 
- **No GlobalScope**: Do not use `GlobalScope` as it leads to memory leaks and untrackable background work.
- **No Blocking Calls**: Never block the main thread. All I/O operations (Room, Retrofit) must be dispatched to `Dispatchers.IO`.

## 4. UI & Jetpack Compose

- **Stateless Composables**: Break down complex screens into smaller, stateless composables that accept state and lambdas (for events) as parameters.
- **Design Tokens**: Never hardcode colors (`Color(0xFF...)`) or spacing (`13.dp`) in UI code. Use the provided Material 3 Theme tokens (`MaterialTheme.colorScheme`, `MaterialTheme.typography`).
- **Accessibility**: Provide meaningful `contentDescription` for actionable icons and images. Use semantic properties where necessary for screen readers.

## 5. Dependency Injection

- **Hilt**: All dependencies should be provided via Hilt.
- Do not pass heavy dependencies through Compose parameters; use `hiltViewModel()` to scope ViewModels to the navigation graph.

## 6. Testability

- Design classes to be easily testable. This means passing dependencies in via constructors (constructor injection).
- UI code should be decoupled from ViewModels for easier isolated Composable testing.
