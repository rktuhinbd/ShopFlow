# TASK-704: Implement pull-to-refresh

## Description
Implement pull-to-refresh behavior for the Product List.
The refresh behavior should work for ALL catalog and CATEGORY catalog.
Search is network-only and must remain governed by Paging.

## Implementation Steps
- [x] Investigate existing `PullToRefreshBox` implementation in `ProductListScreen.kt` or implement if missing.
- [x] Connect pull-to-refresh to `lazyPagingItems.refresh()`.
- [x] Determine refresh state using `lazyPagingItems.loadState.refresh`.
- [x] Ensure content remains visible during refresh.
- [x] Verify search and category preservation during refresh.
- [x] Test pull-to-refresh behavior.
