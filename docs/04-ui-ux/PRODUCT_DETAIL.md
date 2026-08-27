# Screen Specification: Product Detail

**Screen ID**: SCR-002  
**Related Requirements**: FR-401–FR-405, FR-501  
**Status**: DRAFT

---

## 1. Purpose
Display comprehensive information about a single product.

## 2. User Goal
View all details about a product and decide to favorite it.

## 3. Components

| Component | Type | Description |
|-----------|------|-------------|
| Top App Bar | `TopAppBar` | Back arrow + product title |
| Image Gallery | `HorizontalPager` | Swipeable product images |
| Page Indicator | Dots / `HorizontalPagerIndicator` | Current image position |
| Title | `headlineMedium` | Product title |
| Brand | `bodyMedium` | Brand name |
| Price Section | Custom | Original price, discount %, final price |
| Rating | Stars + numeric | Rating display |
| Availability | Chip/badge | "In Stock" / "Low Stock" etc. |
| Favorite Button | `IconButton` or `FloatingActionButton` | Toggle favorite |
| Description | `bodyLarge` | Full product description |
| Details Section | Card or section | SKU, weight, dimensions, warranty, shipping, return policy, minimum order |
| Tags | Row of `SuggestionChip` | Product tags |
| Reviews Section | `LazyColumn` items | List of reviews with name, rating, comment, date |

## 4. States

| State | Condition | Display |
|-------|-----------|---------|
| **Loading** | Product data loading | Shimmer or progress indicator |
| **Content** | Product loaded | Full detail layout |
| **Error** | Failed to load product | Error message + retry |
| **Offline** | No network, product in cache | Cached data (full content) |

## 5. Interactions

| Action | Behavior |
|--------|----------|
| Swipe image | Navigate image gallery |
| Tap favorite | Toggle favorite state |
| Tap back | Return to previous screen |
| Scroll | Scroll through product details |
| Tap tag | TBD — could filter by tag |

## 6. Navigation
- **From**: Product List (tap card), Favorites (tap card)
- **To**: Back to previous screen
- **Arguments**: Product ID (type-safe navigation)

## 7. Loading Behavior
- Product loads from Room (fast — already cached by list)
- If not in cache (deep link scenario): fetch from API, cache, display

## 8. Error Behavior
- Load error: Error state with retry
- Image load error: Placeholder image shown

## 9. Offline Behavior
- Product in cache: Full content displayed normally
- Product not in cache: Error state with offline message

## 10. Accessibility
- Images: `contentDescription` with product name + image index
- Prices: read both original and discounted price
- Rating: "Rating: X out of 5 stars"
- Favorite button: "Add to favorites" / "Remove from favorites"
- Reviews: each review as a semantic group
- All text scalable

## 11. Responsive / Adaptive
- **Compact**: Single-column scrollable layout
- **Medium**: Wider layout, potentially side-by-side image + info
- **Expanded**: Detail pane in list-detail split (no standalone screen)

## 12. Acceptance Criteria
- [ ] All product fields display correctly
- [ ] Image gallery swipes between multiple images
- [ ] Favorite toggle works and persists
- [ ] Back navigation returns to list
- [ ] Reviews section shows all reviews
- [ ] Loading, error, offline states handled
- [ ] Accessible to TalkBack

---

**Document Status**: DRAFT
