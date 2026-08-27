# ShopFlow — Entity Relationship Diagram
**Version**: 2.0
**Date**: 2026-08-27
**Status**: APPROVED
---
## ERD
```mermaid
erDiagram
    ProductEntity {
        Int id PK "API product ID"
        String title "NOT NULL"
        String description "NOT NULL"
        String category "NOT NULL"
        Double price "NOT NULL"
        Double discountPercentage "NOT NULL"
        Double rating "NOT NULL"
        Int stock "NOT NULL"
        String tags "NOT NULL, JSON array"
        String brand "NULLABLE"
        String sku "NOT NULL"
        Int weight "NOT NULL"
        Double dimensionWidth "NOT NULL"
        Double dimensionHeight "NOT NULL"
        Double dimensionDepth "NOT NULL"
        String warrantyInformation "NOT NULL"
        String shippingInformation "NOT NULL"
        String availabilityStatus "NOT NULL"
        String reviews "NOT NULL, JSON array"
        String returnPolicy "NOT NULL"
        Int minimumOrderQuantity "NOT NULL"
        String images "NOT NULL, JSON array"
        String thumbnail "NOT NULL, URL"
        Long cachedAt "NOT NULL, epoch ms"
    }
    FavoriteEntity {
        Int productId PK "References ProductEntity.id"
        Long favoritedAt "NOT NULL, epoch ms"
    }
    RemoteKeyEntity {
        Int productId PK "References ProductEntity.id"
        String query PK "Cache identity (ALL, CATEGORY:slug)"
        Int prevKey "NULLABLE (skip offset)"
        Int nextKey "NULLABLE (skip offset)"
    }
    CacheContextEntity {
        String query PK "Cache identity"
        Long lastUpdated "NOT NULL, epoch ms"
    }
    CategoryEntity {
        String slug PK "Category slug"
        String name "NOT NULL"
        String url "NOT NULL"
        Long cachedAt "NOT NULL, epoch ms"
    }
    ProductEntity ||--o| FavoriteEntity : "favorited by"
    ProductEntity ||--o{ RemoteKeyEntity : "belongs to context"
    CacheContextEntity ||--o{ RemoteKeyEntity : "defines freshness for"
```
## Relationship Notes
- **ProductEntity → FavoriteEntity**: One-to-zero-or-one. A product may or may not be favorited. FavoriteEntity uses `productId` as PK, referencing `ProductEntity.id`.
- **ProductEntity → RemoteKeyEntity**: One-to-many. A single canonical product can belong to multiple caching contexts (e.g. "ALL" and "CATEGORY:smartphones"). The `RemoteKeyEntity` maps products to contexts and stores their pagination skip offsets.
- **CacheContextEntity → RemoteKeyEntity**: One-to-many. A cache context dictates the freshness for all remote keys tied to that `query`.
- **CategoryEntity**: Independent entity. No foreign key to ProductEntity — category matching is done by string comparison on `ProductEntity.category` = `CategoryEntity.slug`.
## Index Strategy
| Table | Index | Columns | Purpose |
|-------|-------|---------|---------|
| favorites | (PK) | `productId` | Favorite lookup |
| remote_keys | (PK) | `productId, query` | Context membership & pagination |
| cache_context| (PK) | `query` | Freshness check |
