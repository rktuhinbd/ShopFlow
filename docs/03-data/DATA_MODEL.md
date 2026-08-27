# ShopFlow — Data Model

**Version**: 2.0
**Date**: 2026-08-27
**Status**: APPROVED

---

## 1. Overview

Room is the single source of truth for the UI (ADR-001). All data displayed to the user flows from Room, never directly from the network.
The schema supports context-aware offline caching (ADR-011) to isolate paging state and freshness for different contexts (`ALL` vs `CATEGORY:<slug>`).

## 2. Entities

### 2.1 ProductEntity

The primary entity representing a cached product from the DummyJSON API. It is the shared canonical record for products.

| Column | Type | Constraints | Source |
|--------|------|-------------|--------|
| `id` | Int | PRIMARY KEY | API `id` |
| `title` | String | NOT NULL | API `title` |
| `description` | String | NOT NULL | API `description` |
| `category` | String | NOT NULL | API `category` |
| `price` | Double | NOT NULL | API `price` |
| `discountPercentage` | Double | NOT NULL | API `discountPercentage` |
| `rating` | Double | NOT NULL | API `rating` |
| `stock` | Int | NOT NULL | API `stock` |
| `tags` | String | NOT NULL | JSON array → TypeConverter |
| `brand` | String? | NULLABLE | API `brand` (nullable) |
| `sku` | String | NOT NULL | API `sku` |
| `weight` | Int | NOT NULL | API `weight` |
| `dimensionWidth` | Double | NOT NULL | API `dimensions.width` |
| `dimensionHeight` | Double | NOT NULL | API `dimensions.height` |
| `dimensionDepth` | Double | NOT NULL | API `dimensions.depth` |
| `warrantyInformation` | String | NOT NULL | API `warrantyInformation` |
| `shippingInformation` | String | NOT NULL | API `shippingInformation` |
| `availabilityStatus` | String | NOT NULL | API `availabilityStatus` |
| `reviews` | String | NOT NULL | JSON array → TypeConverter |
| `returnPolicy` | String | NOT NULL | API `returnPolicy` |
| `minimumOrderQuantity` | Int | NOT NULL | API `minimumOrderQuantity` |
| `images` | String | NOT NULL | JSON array → TypeConverter |
| `thumbnail` | String | NOT NULL | API `thumbnail` |
| `cachedAt` | Long | NOT NULL | System.currentTimeMillis() |

### 2.2 CacheContextEntity

Tracks the freshness timestamp for each cached context independently.

| Column | Type | Constraints | Source |
|--------|------|-------------|--------|
| `query` | String | PRIMARY KEY | Cache identity (e.g. "ALL", "CATEGORY:smartphones") |
| `lastUpdated` | Long | NOT NULL | System.currentTimeMillis() |

### 2.3 RemoteKeyEntity

Tracks pagination state (`skip` offsets) AND context membership for RemoteMediator. Maps each product to its pagination position for REFRESH/APPEND behavior within a specific context.

| Column | Type | Constraints | Source |
|--------|------|-------------|--------|
| `productId` | Int | PRIMARY KEY | References ProductEntity.id |
| `query` | String | PRIMARY KEY | Cache identity (e.g. "ALL", "CATEGORY:smartphones") |
| `prevKey` | Int? | NULLABLE | Previous skip offset (null for first page) |
| `nextKey` | Int? | NULLABLE | Next skip offset (null for last page) |

### 2.4 FavoriteEntity

Tracks which products the user has favorited. Lightweight — stores only the product ID and timestamp.

| Column | Type | Constraints | Source |
|--------|------|-------------|--------|
| `productId` | Int | PRIMARY KEY | References ProductEntity.id |
| `favoritedAt` | Long | NOT NULL | System.currentTimeMillis() |

### 2.5 CategoryEntity (Optional Cache)

Caches category list for offline access. May be implemented as a simple cache rather than a Room entity.

| Column | Type | Constraints | Source |
|--------|------|-------------|--------|
| `slug` | String | PRIMARY KEY | API `slug` |
| `name` | String | NOT NULL | API `name` |
| `url` | String | NOT NULL | API `url` |
| `cachedAt` | Long | NOT NULL | System.currentTimeMillis() |

## 3. TypeConverters

| Conversion | Strategy |
|------------|----------|
| `List<String>` ↔ Room | JSON serialization via kotlinx.serialization |
| `List<Review>` ↔ Room | JSON serialization via kotlinx.serialization |

## 4. DAOs

### 4.1 ProductDao

| Method | Query | Purpose |
|--------|-------|---------|
| `observeProductsByContext(query)` | `SELECT p.* FROM products p INNER JOIN remote_keys k ON p.id = k.productId WHERE k.query = :query ORDER BY p.id ASC` | PagingSource for Pager |
| `getProductById(id)` | `SELECT * FROM products WHERE id = :id` | Detail screen |
| `upsertAll(products)` | `@Upsert` | RemoteMediator inserts |
| `clearAll()` | `DELETE FROM products` | Clears orphan products (future optimization) |

### 4.2 CacheContextDao

| Method | Query | Purpose |
|--------|-------|---------|
| `getContext(query)` | `SELECT * FROM cache_context WHERE query = :query` | Freshness check |
| `upsert(context)` | `@Upsert` | Update freshness |

### 4.3 RemoteKeyDao

| Method | Query | Purpose |
|--------|-------|---------|
| `getRemoteKey(id, query)` | `SELECT * FROM remote_keys WHERE productId = :id AND query = :query` | RemoteMediator lookup |
| `upsertAll(keys)` | `@Upsert` | Store pagination state |
| `clearRemoteKeys(query)` | `DELETE FROM remote_keys WHERE query = :query` | REFRESH clears keys for context |
| `clearAll()` | `DELETE FROM remote_keys` | Complete reset |

### 4.4 FavoriteDao

| Method | Query | Purpose |
|--------|-------|---------|
| `getAllFavorites()` | `SELECT p.* FROM products p INNER JOIN favorites f ON p.id = f.productId ORDER BY f.favoritedAt DESC` | Favorites screen |
| `isFavorite(productId)` | `SELECT EXISTS(SELECT 1 FROM favorites WHERE productId = :productId)` | Favorite toggle state |
| `insert(favorite)` | `@Insert(onConflict = REPLACE)` | Add favorite |
| `delete(productId)` | `DELETE FROM favorites WHERE productId = :productId` | Remove favorite |
| `getAllFavoriteIds()` | `SELECT productId FROM favorites` | Bulk favorite state check |

## 5. Database

```kotlin
@Database(
    entities = [ProductEntity::class, FavoriteEntity::class, CategoryEntity::class, RemoteKeyEntity::class, CacheContextEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(StringListConverter::class, ProductReviewListConverter::class)
abstract class ShopFlowDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun categoryDao(): CategoryDao
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun cacheContextDao(): CacheContextDao
}
```

## 6. Synchronization Behavior

| Scenario | Behavior |
|----------|----------|
| **First launch (online)** | RemoteMediator fetches page, upserts Products + RemoteKeys, updates CacheContext. |
| **Cached launch (online)** | PagingSource emits cache immediately; RemoteMediator refreshes in background if CacheContext is stale. |
| **Offline with cache** | PagingSource emits cache; RemoteMediator returns `MediatorResult.Error` |
| **Offline without cache** | PagingSource emits empty; UI shows error/retry state |
| **Refresh** | RemoteMediator clears remote keys for that query, fetches fresh, upserts Products/Keys. |
| **Network failure** | RemoteMediator returns error; existing cache remains; retry available |

## 7. Cache Invalidation Strategy

- **On REFRESH**: Clear `RemoteKeyEntity` rows for the specific context in a single transaction, then refetch.
- **Stale threshold**: 15 minutes (ADR-009). Stale data is shown immediately, but triggers a background sync to refresh the cache via `CacheContextEntity.lastUpdated`.
- **Network unavailable**: Cached data remains usable indefinitely. Stale cache is never deleted just because the network is unavailable.
- **Favorites are NOT cleared on refresh** — they are independent of product cache.
- **Category cache**: Refreshed independently; staleness threshold same as products.
