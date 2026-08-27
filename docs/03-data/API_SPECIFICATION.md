# ShopFlow — API Specification

**Version**: 1.0  
**Date**: 2026-08-27  
**Status**: VERIFIED against live API on 2026-08-27

---

## 1. Overview

| Field | Value |
|-------|-------|
| API | DummyJSON |
| Base URL | `https://dummyjson.com` |
| Protocol | HTTPS / REST / JSON |
| Authentication | None required for product endpoints |
| Total Products | 194 (verified) |
| Total Categories | 24 (verified) |

## 2. Endpoints

### 2.1 Get All Products

```
GET /products?limit={limit}&skip={skip}&select={fields}&sortBy={field}&order={asc|desc}
```

**Query Parameters**:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `limit` | int | 30 | Number of products to return |
| `skip` | int | 0 | Number of products to skip |
| `select` | string | (all) | Comma-separated field names to include |
| `sortBy` | string | — | Field name to sort by |
| `order` | string | `asc` | Sort order: `asc` or `desc` |

**Response** (verified):
```json
{
  "products": [ /* Product objects */ ],
  "total": 194,
  "skip": 0,
  "limit": 30
}
```

### 2.2 Get Single Product

```
GET /products/{id}
```

**Response**: Single Product object (see schema below).

### 2.3 Search Products

```
GET /products/search?q={query}&limit={limit}&skip={skip}
```

**Query Parameters**:

| Parameter | Type | Description |
|-----------|------|-------------|
| `q` | string | Search query (searches across title, description, category, etc.) |
| `limit` | int | Number of results to return |
| `skip` | int | Number of results to skip |

**Response**: Same pagination wrapper as Get All Products.

### 2.4 Get Categories

```
GET /products/categories
```

**Response** (verified): Array of category objects:
```json
[
  { "slug": "beauty", "name": "Beauty", "url": "https://dummyjson.com/products/category/beauty" },
  { "slug": "fragrances", "name": "Fragrances", "url": "https://dummyjson.com/products/category/fragrances" },
  ...
]
```

**Verified Categories (24)**: beauty, fragrances, furniture, groceries, home-decoration, kitchen-accessories, laptops, mens-shirts, mens-shoes, mens-watches, mobile-accessories, motorcycle, skin-care, smartphones, sports-accessories, sunglasses, tablets, tops, vehicle, womens-bags, womens-dresses, womens-jewellery, womens-shoes, womens-watches

### 2.5 Get Products by Category

```
GET /products/category/{categorySlug}?limit={limit}&skip={skip}
```

**Response**: Same pagination wrapper as Get All Products.

## 3. Product Schema (Verified)

```json
{
  "id": 1,                                          // int — unique ID
  "title": "Essence Mascara Lash Princess",          // string
  "description": "The Essence Mascara...",           // string
  "category": "beauty",                              // string — category slug
  "price": 9.99,                                     // double
  "discountPercentage": 10.48,                       // double
  "rating": 2.56,                                    // double (0-5)
  "stock": 99,                                       // int
  "tags": ["beauty", "mascara"],                     // string[]
  "brand": "Essence",                                // string (nullable in some products)
  "sku": "BEA-ESS-ESS-001",                         // string
  "weight": 4,                                       // int (grams)
  "dimensions": {                                    // object
    "width": 15.14,                                  // double
    "height": 13.08,                                 // double
    "depth": 22.99                                   // double
  },
  "warrantyInformation": "1 week warranty",          // string
  "shippingInformation": "Ships in 3-5 business days", // string
  "availabilityStatus": "In Stock",                  // string
  "reviews": [                                       // Review[]
    {
      "rating": 3,                                   // int (1-5)
      "comment": "Would not recommend!",             // string
      "date": "2025-04-30T09:41:02.053Z",           // ISO 8601 string
      "reviewerName": "Eleanor Collins",             // string
      "reviewerEmail": "eleanor.collins@x.dummyjson.com" // string
    }
  ],
  "returnPolicy": "No return policy",               // string
  "minimumOrderQuantity": 48,                        // int
  "meta": {                                          // object
    "createdAt": "2025-04-30T09:41:02.053Z",        // ISO 8601 string
    "updatedAt": "2025-04-30T09:41:02.053Z",        // ISO 8601 string
    "barcode": "5784719087687",                      // string
    "qrCode": "https://cdn.dummyjson.com/public/qr-code.png" // URL string
  },
  "images": [                                        // string[] (URLs)
    "https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/1.webp"
  ],
  "thumbnail": "https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/thumbnail.webp" // URL string
}
```

## 4. Category Schema (Verified)

```json
{
  "slug": "beauty",                                  // string — unique identifier
  "name": "Beauty",                                  // string — display name
  "url": "https://dummyjson.com/products/category/beauty" // string — full URL
}
```

## 5. Pagination Behavior

- **Mechanism**: `limit` + `skip` (offset-based)
- **Default limit**: 30
- **Maximum limit**: Not documented; tested up to 194 (all products)
- **Total field**: Response includes `total` count for calculating pages
- **Skip calculation**: `skip = page * pageSize`
- **Empty page**: When `skip >= total`, returns `{ "products": [], "total": 194, "skip": 200, "limit": 30 }`

### Pagination Strategy for Paging 3

```
Page 0: skip=0,  limit=20  → products[0..19]
Page 1: skip=20, limit=20  → products[20..39]
Page N: skip=N*20, limit=20 → products[N*20..(N+1)*20-1]
```

**End of data**: When response `products` array is empty or `skip + limit >= total`.

## 6. Error Scenarios

| Scenario | HTTP Status | Response |
|----------|-------------|----------|
| Invalid product ID | 404 | `{ "message": "Product with id '999' not found" }` |
| Invalid category | 404 | `{ "message": "Products of category '...' not found" }` |
| Server error | 500 | Generic error |
| Network timeout | — | No response (handle client-side) |

## 7. Mapping Strategy — API → Room Entity

| API Field | Room Column | Type | Notes |
|-----------|-------------|------|-------|
| `id` | `id` | Int (PK) | Primary key |
| `title` | `title` | String | |
| `description` | `description` | String | |
| `category` | `category` | String | Category slug |
| `price` | `price` | Double | |
| `discountPercentage` | `discountPercentage` | Double | |
| `rating` | `rating` | Double | |
| `stock` | `stock` | Int | |
| `tags` | `tags` | String (JSON) | TypeConverter: List<String> ↔ JSON |
| `brand` | `brand` | String? | Nullable |
| `sku` | `sku` | String | |
| `weight` | `weight` | Int | |
| `dimensions.width` | `dimensionWidth` | Double | Flattened |
| `dimensions.height` | `dimensionHeight` | Double | Flattened |
| `dimensions.depth` | `dimensionDepth` | Double | Flattened |
| `warrantyInformation` | `warrantyInformation` | String | |
| `shippingInformation` | `shippingInformation` | String | |
| `availabilityStatus` | `availabilityStatus` | String | |
| `reviews` | `reviews` | String (JSON) | TypeConverter: List<Review> ↔ JSON |
| `returnPolicy` | `returnPolicy` | String | |
| `minimumOrderQuantity` | `minimumOrderQuantity` | Int | |
| `meta.barcode` | `barcode` | String | Flattened |
| `images` | `images` | String (JSON) | TypeConverter: List<String> ↔ JSON |
| `thumbnail` | `thumbnail` | String | URL |

## 8. Limitations

- API is a mock service — data resets periodically
- POST/PUT/DELETE return simulated responses but don't persist
- No real-time updates or webhooks
- Rate limiting behavior is undocumented
- `brand` field is nullable on some products (observed in data)

---

**Verification Evidence**: All endpoints tested with `curl` on 2026-08-27. Response schemas documented from actual responses, not API documentation.
