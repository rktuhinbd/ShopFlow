# Screen Specification: Favorites

**Screen ID**: SCR-003  
**Related Requirements**: FR-501–FR-506, FR-601  
**Status**: DRAFT

---

## 1. Purpose
Display all products the user has saved as favorites.

## 2. User Goal
Review and manage saved products.

## 3. Components

| Component | Type | Description |
|-----------|------|-------------|
| Top App Bar | `TopAppBar` | "Favorites" title |
| Favorites List | `LazyColumn` / `LazyVerticalGrid` | Product cards (same as product list) |
| Product Card | `ElevatedCard` | Same card component as product list, favorite icon filled |
| Empty State | Custom composable | "No favorites yet" illustration + hint |
| Bottom Navigation | `NavigationBar` | Products / Favorites tabs (Favorites selected) |

## 4. States

| State | Condition | Display |
|-------|-----------|---------|
| **Content** | Favorites exist | Scrollable list of favorited products |
| **Empty** | No favorites | Centered illustration + "No favorites yet" + "Browse products to add favorites" |

## 5. Interactions

| Action | Behavior |
|--------|----------|
| Tap product card | Navigate to Product Detail (SCR-002) |
| Tap favorite icon (unfavorite) | Remove from favorites; item animates out |
| Scroll | Browse favorites |

## 6. Navigation
- **From**: Bottom nav "Favorites" tab, back from Product Detail
- **To**: Product Detail (tap card), Products (bottom nav)
- **Back**: System back returns to Products tab or exits

## 7. Loading Behavior
- Favorites load from Room — effectively instant (local query)
- No network loading needed

## 8. Error Behavior
- Room errors: Extremely unlikely; generic error state as fallback

## 9. Empty Behavior
- No favorites: Centered empty state with illustration
- Hint text: "Browse products to add favorites"
- Optional: CTA button to navigate to Products tab

## 10. Offline Behavior
- Fully functional offline — favorites are local-only
- Product data comes from Room cache
- If product was uncached (shouldn't happen), card shows whatever is in Room

## 11. Accessibility
- Same card accessibility as Product List
- Empty state: readable by TalkBack
- Unfavorite action: announced "Removed from favorites"

## 12. Responsive / Adaptive
- **Compact**: Single-column list
- **Medium**: Wider cards or 2-column grid
- **Expanded**: Favorites list pane with detail pane

## 13. Acceptance Criteria
- [ ] All favorited products displayed
- [ ] Ordered by most recently favorited (descending)
- [ ] Tapping card navigates to detail
- [ ] Unfavoriting removes item from list
- [ ] Empty state shown when no favorites
- [ ] Works fully offline
- [ ] Accessible to TalkBack

---

**Document Status**: DRAFT
