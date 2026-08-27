# TASK-400: Create RemoteMediator

## Status
IN_PROGRESS

## Description
Implement Paging 3 `RemoteMediator` with offline-first synchronization using Room.
This includes:
- `ProductMapper` to map from `ProductDto` to `ProductEntity`.
- `ProductRemoteMediator` handling `ALL` and `CATEGORY` contexts.
- Strict context membership replacement for `REFRESH`.
- Isolated context lookup for `APPEND`.
- Proper end-of-pagination offsets handling.
- Graceful exception translation (`MediatorResult.Error`).

## Sub-tasks
- [ ] Create `ProductMapper.kt`
- [ ] Create `ProductMapperTest.kt`
- [ ] Create `ProductRemoteMediator.kt`
- [ ] Create `ProductRemoteMediatorTest.kt`
- [ ] Ensure all required test variants pass.
