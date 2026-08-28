# TASK-602: Create ProductDetailViewModel

## Scope
- Implement `ProductDetailUiState` modifications.
- Implement `ProductDetailEvent` modifications.
- Implement `ProductDetailViewModel`.
- Implement `ProductDetailViewModelTest`.

## Architectural Decisions
- Use `SavedStateHandle` directly for `productId`.
- `FetchState` state machine to guarantee mutual exclusion and single-fetch-per-null behavior.
- `userMessage` field in `Success` state to handle transient errors like favorite toggling.

## Implementation Steps
- [x] Update `ProductDetailUiState.kt`
- [x] Update `ProductDetailEvent.kt`
- [x] Create `ProductDetailViewModel.kt`
- [x] Create `ProductDetailViewModelTest.kt`
- [x] Verify with `./gradlew testDebugUnitTest`

## Verification
- `testDebugUnitTest` executed and BUILD SUCCESSFUL.
- All 10 targeted tests in `ProductDetailViewModelTest` pass.
- State machine protection against infinite fetch loops verified.

## Status
- DONE (2026-08-28)

## Remaining Issues
- None.
