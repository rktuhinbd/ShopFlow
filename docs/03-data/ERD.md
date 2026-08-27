# ShopFlow — Entity Relationship Diagram

**Version**: 1.0-DRAFT  
**Date**: 2026-08-27

---

## ERD

```mermaid
erDiagram
    ProductEntity {
        Int id PK "API product ID"
        String title "NOT NULL"
        String description "NOT NULL"
        String category "NOT NULL, indexed"
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
        String barcode "NOT NULL"
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
        String query "NOT NULL, Cache identity"
        Int prevKey "NULLABLE"
        Int currentPage "NOT NULL"
        Int nextKey "NULLABLE"
        Long createdAt "NOT NULL, epoch ms"
    }

    CategoryEntity {
        String slug PK "Category slug"
        String name "NOT NULL"
        String url "NOT NULL"
        Long cachedAt "NOT NULL, epoch ms"
    }

    ProductEntity ||--o| FavoriteEntity : "favorited by"
    ProductEntity ||--|| RemoteKeyEntity : "pagination key"
```

## Relationship Notes

- **ProductEntity → FavoriteEntity**: One-to-zero-or-one. A product may or may not be favorited. FavoriteEntity uses `productId` as PK, referencing `ProductEntity.id`.
- **ProductEntity → RemoteKeyEntity**: One-to-one during pagination. Each cached product has a corresponding remote key for pagination tracking. Remote keys are cleared alongside products on REFRESH.
- **CategoryEntity**: Independent entity. No foreign key to ProductEntity — category matching is done by string comparison on `ProductEntity.category` = `CategoryEntity.slug`.

## Index Strategy

| Table | Index | Columns | Purpose |
|-------|-------|---------|---------|
| products | `index_products_category` | `category` | Category filter queries |
| products | `index_products_title` | `title` | Search optimization |
| favorites | (PK) | `productId` | Favorite lookup |
| remote_keys | (PK) | `productId` | Pagination key lookup |

---

**Document Status**: DRAFT — Awaiting human review and approval.
