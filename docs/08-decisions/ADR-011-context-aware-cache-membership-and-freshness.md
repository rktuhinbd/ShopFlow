# ADR 011: Context-Aware Cache Membership and Freshness

## Status
Accepted

## Context
Our offline-first architecture requires caching product catalogs for different browsing contexts, specifically the main product list (`ALL`) and individual categories (`CATEGORY:<slug>`). 
Previously, the `ProductEntity` table stored products, but `ProductDao` queried this canonical table directly without knowing which product belonged to which cached context. If a user refreshed the "smartphones" category, there was no safe way to clear old "smartphones" products without accidentally deleting products that also belonged to the "laptops" category or the "ALL" list, because `ProductEntity` is a shared canonical record.

Furthermore, cache freshness (`cachedAt`) was tracked per-product rather than per-context, making it impossible to determine when an empty category was last refreshed. 
Additionally, pagination state was tracked using page indices (1, 2, 3), but the DummyJSON API uses `skip` and `limit` offsets.

## Decision
We have redesigned the persistence model to cleanly separate canonical product data, context membership, and context freshness:

1. **Shared Canonical `ProductEntity`**: `ProductEntity` remains the single source of truth for product data. It is never aggressively deleted during context refreshes; instead, it is upserted.
2. **Dual-Purpose `RemoteKeyEntity`**: `RemoteKeyEntity` serves as both pagination state AND context membership. The composite `PRIMARY KEY(productId, query)` guarantees at most one membership record per product per context. 
3. **Context-Aware `ProductDao`**: The local data source now uses an `INNER JOIN` on `remote_keys` to fetch products for a specific context (`observeProductsByContext`).
4. **Independent Context Freshness (`CacheContextEntity`)**: A new entity tracks the `lastUpdated` timestamp per `query`. This allows empty contexts to be evaluated for freshness.
5. **Canonical Identifiers**: We standardized cache context identifiers to `"ALL"` and `"CATEGORY:<slug>"`. Search results remain strictly network-only and are excluded from this cache model.
6. **API Skip Offsets**: `RemoteKeyEntity` was updated to store `prevKey` and `nextKey` as literal API `skip` offsets, completely removing obsolete page indices.
7. **Safe v1→v2 Migration**: We implemented an atomic `MIGRATION_1_2` that safely creates `cache_context`, preserves legacy product data, canonicalizes legacy `query = ""` to `"ALL"`, and safely resets incompatible page-index paging keys (`prevKey`, `nextKey`) to `null`.
8. **Missing Legacy Freshness Forces Refresh**: Because legacy v1 data had no reliable context freshness timestamp, we deliberately do not fabricate a `CacheContextEntity` during migration. This safely forces `initialize()` to return `LAUNCH_INITIAL_REFRESH`.

## Consequences
- **Positive**: Complete isolation of cache contexts. A failure during a network refresh will not destroy the previously cached data, satisfying offline-first requirements.
- **Positive**: Zero data duplication for products belonging to multiple categories.
- **Positive**: Empty categories properly track their freshness.
- **Negative**: Adds a slight `INNER JOIN` overhead to `ProductDao` queries, which is acceptable and standard in Room/Paging architectures.
- **Negative**: Required a destructive reset of `prevKey`/`nextKey` during the v1→v2 migration to prevent incompatible pagination state from corrupting the UI.

## Notes
- Refresh safety is maintained by performing network requests *before* clearing old `RemoteKeyEntity` rows in a single atomic transaction.
