# ShopFlow — Data Model

**Version**: 1.0-DRAFT  
**Date**: 2026-08-27  
**Status**: DRAFT — PENDING HUMAN APPROVAL

---

## 1. Overview

Room is the single source of truth for the UI (ADR-001). All data displayed to the user flows from Room, never directly from the network. The network layer writes into Room, and the UI observes Room through Paging 3's PagingSource and Kotlin Flows.

## 2. Entities

### 2.1 ProductEntity

The primary entity representing a cached product from the DummyJSON API.

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

**Indexes**:
- `index_products_category` on `category` — for category filtering queries

### 2.2 FavoriteEntity

Tracks which products the user has favorited. Lightweight — stores only the product ID and timestamp.

| Column | Type | Constraints | Source |
|--------|------|-------------|--------|
| `productId` | Int | PRIMARY KEY | References ProductEntity.id |
| `favoritedAt` | Long | NOT NULL | System.currentTimeMillis() |

### 2.3 RemoteKeyEntity

Tracks pagination state for RemoteMediator. Maps each product to its pagination position for REFRESH/APPEND behavior.

| Column | Type | Constraints | Source |
|--------|------|-------------|--------|
| `productId` | Int | PRIMARY KEY | References ProductEntity.id |
| `query` | String | PRIMARY KEY | Cache identity (e.g. "", "cat_smartphones") |
| `prevKey` | Int? | NULLABLE | Previous page number (null for first page) |
| `currentPage` | Int | NOT NULL | Current page number |
| `nextKey` | Int? | NULLABLE | Next page number (null for last page) |
| `createdAt` | Long | NOT NULL | System.currentTimeMillis() |

### 2.4 CategoryEntity (Optional Cache)

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
| `getProducts()` | `SELECT * FROM products ORDER BY id ASC` | PagingSource for Pager |
| `getProductsByCategory(cat)` | `SELECT * FROM products WHERE category = :cat ORDER BY id ASC` | Category filter |
| `getProductById(id)` | `SELECT * FROM products WHERE id = :id` | Detail screen |
| `insertAll(products)` | `@Insert(onConflict = REPLACE)` | RemoteMediator inserts |
| `clearAll()` | `DELETE FROM products` | REFRESH clears cache |
| `count()` | `SELECT COUNT(*) FROM products` | Check if cache exists |

### 4.2 FavoriteDao

| Method | Query | Purpose |
|--------|-------|---------|
| `getAllFavorites()` | `SELECT p.* FROM products p INNER JOIN favorites f ON p.id = f.productId ORDER BY f.favoritedAt DESC` | Favorites screen |
| `isFavorite(productId)` | `SELECT EXISTS(SELECT 1 FROM favorites WHERE productId = :productId)` | Favorite toggle state |
| `insert(favorite)` | `@Insert(onConflict = REPLACE)` | Add favorite |
| `delete(productId)` | `DELETE FROM favorites WHERE productId = :productId` | Remove favorite |
| `getAllFavoriteIds()` | `SELECT productId FROM favorites` | Bulk favorite state check |

### 4.3 RemoteKeyDao

| Method | Query | Purpose |
|--------|-------|---------|
| `getRemoteKeyByProductId(id)` | `SELECT * FROM remote_keys WHERE productId = :id` | RemoteMediator lookup |
| `insertAll(keys)` | `@Insert(onConflict = REPLACE)` | Store pagination state |
| `clearAll()` | `DELETE FROM remote_keys` | REFRESH clears keys |
| `getCreationTime()` | `SELECT createdAt FROM remote_keys ORDER BY createdAt DESC LIMIT 1` | Cache age check |

## 5. Database

```kotlin
@Database(
    entities = [ProductEntity::class, FavoriteEntity::class, RemoteKeyEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ShopFlowDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}
```

## 6. Synchronization Behavior

| Scenario | Behavior |
|----------|----------|
| **First launch (online)** | RemoteMediator fetches page 0, inserts into Room, PagingSource emits |
| **Cached launch (online)** | PagingSource emits cache immediately; RemoteMediator refreshes in background |
| **Offline with cache** | PagingSource emits cache; RemoteMediator returns `MediatorResult.Error` |
| **Offline without cache** | PagingSource emits empty; UI shows error/retry state |
| **Refresh** | RemoteMediator clears products + remote keys in transaction, fetches fresh |
| **Network failure** | RemoteMediator returns error; existing cache remains; retry available |
| **Stale data** | Check `cachedAt` or `RemoteKey.createdAt`; auto-refresh after threshold (TBD) |

## 7. Cache Invalidation Strategy

- **On REFRESH**: Clear all products and remote keys in a single transaction, then refetch
- **Stale threshold**: 15 minutes (ADR-009). Stale data is shown immediately, but triggers a background sync to refresh the cache.
- **Network unavailable**: Cached data remains usable indefinitely. Stale cache is never deleted just because the network is unavailable.
- **Favorites are NOT cleared on refresh** — they are independent of product cache
- **Category cache**: Refreshed independently; staleness threshold same as products

---

**Document Status**: DRAFT — Awaiting human review and approval.
