# TASK-403 — Write RemoteMediator tests

## Acceptance Criteria
- [x] ALL refresh inserts products and keys
- [x] APPEND non-final sets nextKey
- [x] APPEND final returns endOfPaginationReached
- [x] IOException results in MediatorResult.Error and preserves cache
- [x] SerializationException results in MediatorResult.Error and preserves cache
- [x] Existing coverage retained
- [x] No unnecessary architecture expansion
- [x] All tests run and pass on device/emulator

## Completion
- Completed: 2026-08-28
- Verification: `./gradlew connectedAndroidTest` passed successfully.
