# Screen Specification: Product List

**Screen ID**: SCR-001  
**Related Requirements**: FR-101–FR-107, FR-201–FR-207, FR-301–FR-304, FR-601  
**Status**: DRAFT

---

## 1. Purpose
Primary screen for browsing the product catalog. Serves as the app's main content surface.

## 2. User Goal
Browse, search, and filter products to find items of interest.

## 3. Components

| Component | Type | Description |
|-----------|------|-------------|
| Top App Bar | `TopAppBar` | App title "ShopFlow" |
| Search Bar | `SearchBar` | Query input with debounce |
| Category Chips | `LazyRow` of `FilterChip` | Horizontal scrollable category filter |
| Product List | `LazyColumn` / `LazyVerticalGrid` | Paginated product cards |
| Product Card | `ElevatedCard` | Thumbnail, title, brand, price, rating, favorite toggle |
| Bottom Navigation | `NavigationBar` | Products / Favorites tabs |
| Pull-to-Refresh | `PullToRefreshBox` | Refresh indicator |
| Loading Indicator | Shimmer / `CircularProgressIndicator` | Initial load and page append |
| Empty State | Custom composable | No results message |
| Error State | Custom composable | Error message + retry button |
| Offline Banner | `Snackbar` or banner | Offline notification |

## 4. States

| State | Condition | Display |
|-------|-----------|---------|
| **Loading (initial)** | First data fetch, no cache | Shimmer skeleton cards |
| **Content** | Products loaded | Scrollable product cards |
| **Appending** | Loading next page | Progress indicator at bottom |
| **Empty (search)** | Search returns 0 results | "No products found" + illustration |
| **Empty (category)** | Category has 0 products | "No products in this category" |
| **Error (initial)** | Network error, no cache | Full-screen error + retry |
| **Error (append)** | Page load failed | Error item + retry at bottom |
| **Offline with cache** | No network, cache exists | Cached data + offline banner |
| **Offline no cache** | No network, no cache | Full-screen error + retry |
| **Refreshing** | Pull-to-refresh active | Refresh indicator |

## 5. Interactions

| Action | Behavior |
|--------|----------|
| Scroll down | Load more products (Paging 3 append) |
| Tap product card | Navigate to Product Detail (SCR-002) |
| Tap favorite icon | Toggle favorite state |
| Type in search bar | Debounce 300ms → search API → display results |
| Clear search | Return to full catalog |
| Tap category chip | Filter by category; deselect previous |
| Tap "All" chip | Clear category filter |
| Pull to refresh | Clear cache, refetch from API |
| Tap retry | Retry failed load |

## 6. Navigation
- **From**: App launch, back from Product Detail, bottom nav "Products" tab
- **To**: Product Detail (tap card), Favorites (bottom nav)
- **Back**: System back exits app (root destination)

## 7. Loading Behavior
- Initial load: Shimmer placeholders while PagingSource is empty and RemoteMediator is loading
- Page append: Small progress indicator at the bottom of the list
- Search: Progress indicator while search results load
- Category switch: May show brief loading while new data loads

## 8. Error Behavior
- Network error during initial load (no cache): Full-screen error state with retry button
- Network error during append: Error item at end of list with retry
- Network error during refresh: Snackbar with retry, keep existing data

## 9. Empty Behavior
- Search with no results: Centered message with search query
- Category with no products: Centered message

## 10. Offline Behavior
- With cache: Show cached products for browse/category; offline banner informs user
- Without cache: Full-screen offline state with retry
- Favorites toggle works offline
- Search: Network required. Searching offline produces an intentional UX: "Search requires an internet connection." It does not erase cached browse data.

## 11. Accessibility
- Product cards: merged semantic node; reads title, brand, price, rating
- Search bar: labeled "Search products"
- Category chips: selected state announced
- Loading: announced to screen reader
- Error/empty: text readable by TalkBack
- All interactive elements ≥ 48dp

## 12. Responsive / Adaptive
- **Compact**: Single-column list, full-width cards
- **Medium**: Potentially 2-column grid
- **Expanded**: List pane in list-detail layout

## 13. Acceptance Criteria
- [ ] Products display with thumbnail, title, brand, price, discount, rating
- [ ] Scrolling loads more products (pagination works)
- [ ] Search debounces and returns results
- [ ] Category chips filter products
- [ ] "All" clears category filter
- [ ] Favorite toggle works on cards
- [ ] Loading, error, empty, offline states all display correctly
- [ ] Pull-to-refresh reloads data
- [ ] Navigation to detail works
- [ ] TalkBack can navigate all elements

---

**Document Status**: DRAFT
