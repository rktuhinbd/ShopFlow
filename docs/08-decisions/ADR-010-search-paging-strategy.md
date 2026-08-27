# ADR-010 — Search Paging Strategy

**Status**: ACCEPTED  
**Date**: 2026-08-27  
**Deciders**: Principal Android Architect (Agentic)

## Context
Search queries produce different product subsets. If we use `RemoteMediator` to cache search results, we must ensure that paging keys (e.g., for "phone") do not collide with paging keys for another search (e.g., "laptop") or the general catalog.

## Decision
Define an exact **cache identity** for paged data using a composite identifier in the `RemoteKeyEntity` and cache invalidation logic.

- **Query Identity**: Every search query string, category slug, or the empty string (for "all") represents a distinct source mode.
- **RemoteKeyEntity**: Will include a `query` field to isolate paging state per source mode.
    - `productId` (PK)
    - `query` (e.g., "" for all, "cat_smartphones", "search_phone")
    - `prevKey`, `currentPage`, `nextKey`, `createdAt`
- **Cache Invalidation (REFRESH)**:
    - When searching or changing categories, a REFRESH load is triggered.
    - The `RemoteMediator` clears ONLY the remote keys and products associated with the *current* query identity. Actually, since products are shared, searching might overwrite standard products. To avoid destroying the main cache, the standard `RemoteMediator` for the main catalog will cache to Room.
    - **Revised Decision for Search/Category**: Due to Room caching complexity, standard catalog and category queries will be cached in Room via `RemoteMediator`. Text Search results will use a **Network-only PagingSource** (no Room caching for search).
    - Why? Caching arbitrary text search results clutters the database and complicates invalidation, while categories and the main catalog are stable enough to cache.

## Consequences
**Positive**:
- Avoids complex composite keys and cache pollution from typos in search.
- Solves the search paging identity problem safely.

**Negative**:
- Search results are not available offline. (Acceptable, as you can't query an API offline anyway).
