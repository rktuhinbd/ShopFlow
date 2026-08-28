# TASK-401 — Implement remote key management

**Status**: DONE
**Date**: 2026-08-28

## Objective
Implement remote key management to support Paging 3's `RemoteMediator` for the ShopFlow application, tracking API skip offsets and ensuring context isolation.

## Acceptance Criteria
- [x] Remote keys track API skip offsets.
- [x] Remote keys support ALL/category context isolation.
- [x] Search has no remote-key cache context.
- [x] REFRESH clears only current-context keys.
- [x] APPEND looks up keys using productId + current context.
- [x] Cache freshness is owned by CacheContextEntity.

## Audited Implementation
The existing V2 architecture established in prior tasks perfectly aligns with the requirements of TASK-401:
- `RemoteKeyEntity` uses a composite primary key `(productId, query)`.
- It tracks `prevKey` and `nextKey` representing API skip offsets.
- It omits `currentPage` and `createdAt` (legacy V1 fields).
- Cache freshness is delegated entirely to `CacheContextEntity.lastUpdated`.
- `ProductRemoteMediator` calculates `nextKey` directly from `response.skip + response.products.size`.
- `ProductRemoteMediator` explicitly rejects search queries via `require` block checking `ALL_CONTEXT` and `CATEGORY_PREFIX`.

## Test Evidence
`ProductRemoteMediatorTest.kt` proves the acceptance criteria:
- `append_usesContextAwareRemoteKeyLookup`
- `refresh_replacesMembershipForCurrentContextOnly`
- `refresh_updatesSharedProductEntityWithoutLosingOtherContextMembership`

## Verification Evidence
- JVM tests passed.
- Android instrumented tests passed.
- Build assembled correctly.

## Source Changes
**NONE**. The correct implementation was already achieved concurrently with TASK-400 and TASK-306.

## Known Issues
- None.
