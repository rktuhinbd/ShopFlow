# ShopFlow — UI/UX Specification

**Version**: 1.0-DRAFT  
**Date**: 2026-08-27  
**Status**: DRAFT — PENDING HUMAN APPROVAL

---

## 1. Design System

### 1.1 Foundation
- **Design Language**: Material 3 (Material You) — ADR-003
- **Platform**: Android-native patterns only. No iOS/HIG visual patterns.
- **Dynamic Color**: Enabled on Android 12+ (API 31+)
- **Dark Mode**: Full dark theme support via `isSystemInDarkTheme()`
- **Fallback Theme**: Custom color scheme for devices without dynamic color

### 1.2 Typography
Material 3 default type scale:
- **Display**: Product detail title
- **Headline**: Section headers, screen titles
- **Title**: Product card title, category names
- **Body**: Descriptions, reviews
- **Label**: Prices, ratings, metadata

### 1.3 Color

**Light Theme (fallback — non-dynamic-color devices)**:
- Primary: Deep teal/emerald — conveys trust and commerce
- Secondary: Warm amber/gold — accent for prices, ratings
- Tertiary: Soft coral — favorites, sale indicators
- Surface/Background: Neutral warm whites
- Error: Material default red

**Dark Theme**: Auto-generated dark variants from Material 3 color system.

**Dynamic Color**: When available, overrides the fallback scheme with system wallpaper-derived colors.

### 1.4 Spacing
Material 3 spacing scale:
- 4dp, 8dp, 12dp, 16dp, 24dp, 32dp
- Card padding: 12–16dp
- Screen edge padding: 16dp (compact), 24dp (medium/expanded)
- Item spacing in lists: 8–12dp

### 1.5 Shapes
- Small components (chips, badges): 8dp corner radius
- Medium components (cards, buttons): 12dp corner radius
- Large components (bottom sheets, dialogs): 16–28dp corner radius

### 1.6 Elevation
- Cards: Level 1 (1dp) in light, tonal elevation in dark
- Top bar: Level 2 (scrolled)
- Bottom navigation: Level 2
- FAB: Level 3

### 1.7 Icons
- Material Symbols (outlined or rounded style — choose one consistently)
- Filled variant for selected/active states
- 24dp default size; 20dp for compact contexts

### 1.8 Motion
- Material 3 motion system
- Shared element transitions between list → detail (if feasible)
- Fade/slide for navigation transitions
- No excessive animations — motion serves function

---

## 2. Screen Inventory

| Screen | Purpose | Navigation |
|--------|---------|------------|
| Product List | Browse paginated product catalog | Bottom nav tab 1 |
| Product Detail | View full product information | Push from list |
| Favorites | View saved products | Bottom nav tab 2 |

### 2.1 Bottom Navigation

Two destinations:
1. **Products** (icon: shopping bag / storefront)
2. **Favorites** (icon: heart / bookmark)

Material 3 `NavigationBar` component. Badge on Favorites showing count (optional).

---

## 3. Visual Identity

### Goal
Modern, premium, eye-catching, restrained, highly usable, clearly Android-native.

### Key Principles
- **Clarity**: Information hierarchy through typography and spacing, not decoration
- **Density**: Show useful information without clutter
- **Delight**: Subtle animations, dynamic color, smooth transitions
- **Trust**: Clean, professional appearance befitting a commerce app

---

## 4. Component Specifications

### 4.1 Product Card

Used in product list, search results, category results, and favorites.

```
┌─────────────────────────────────┐
│  ┌───────────┐                  │
│  │           │  Title           │
│  │ Thumbnail │  Brand           │
│  │  (1:1)    │  ★ 4.5  $29.99  │
│  │           │  -10% $26.99    │♥│
│  └───────────┘                  │
└─────────────────────────────────┘
```

- Thumbnail: Coil async loading, placeholder, error fallback
- Title: `titleMedium`, max 2 lines, ellipsis
- Brand: `bodySmall`, secondary color
- Rating: Star icon + numeric rating
- Price: `titleSmall`, primary; discount price with strikethrough original
- Favorite: Heart icon (outlined/filled) — toggleable
- Card: `ElevatedCard` or `OutlinedCard` with Material 3 shape

### 4.2 Search Bar
- Material 3 `SearchBar` or `DockedSearchBar`
- Placeholder text: "Search products..."
- Clear button when text present
- Keyboard action: Search
- Expandable from icon (compact) or always visible (TBD)

### 4.3 Category Chips
- Horizontal scrollable row of `FilterChip` or `SuggestionChip`
- "All" chip first, always present
- Selected state: filled; unselected: outlined
- Positioned below search bar, above product list

### 4.4 Loading States
- **Initial load**: Shimmer placeholder cards (skeleton loading)
- **Page append**: `CircularProgressIndicator` at list bottom
- **Refresh**: Material 3 pull-to-refresh indicator

### 4.5 Empty States
- **No results (search)**: Illustration + "No products found for '{query}'"
- **No favorites**: Illustration + "No favorites yet" + hint text
- **No products (category)**: "No products in this category"

### 4.6 Error States
- **Network error**: Icon + message + "Retry" button
- **API error**: Icon + message + "Retry" button
- **Offline with cache**: Snackbar/banner "You're offline — showing cached data"
- **Offline without cache**: Full-screen error with retry

---

## 5. Adaptive Layouts (ADR-005)

### 5.1 Window Size Classes

| Class | Width | Layout |
|-------|-------|--------|
| Compact | < 600dp | Single-column list; full-screen detail |
| Medium | 600–840dp | Single-column list (wider cards); full-screen detail |
| Expanded | > 840dp | List-detail split view |

### 5.2 Compact Layout (Phones Portrait)
- Single column product list
- Full-width cards
- Bottom navigation
- Search bar at top
- Category chips below search
- Detail: full screen, navigate with push

### 5.3 Medium Layout (Tablets Portrait, Large Phones Landscape)
- Single column, wider cards (more info visible)
- Or 2-column grid
- Bottom navigation or navigation rail
- Detail: full screen

### 5.4 Expanded Layout (Tablets Landscape, Desktop)
- List-detail split: list on left (~40%), detail on right (~60%)
- Navigation rail instead of bottom bar
- Selecting a product shows detail in the right pane
- No full-screen navigation for detail

### 5.5 Additional Considerations
- **Landscape phone**: TBD — potentially 2-column grid
- **Split-screen**: Graceful degradation; no crashes
- **Foldable**: Follow window size class; respond to fold state changes

---

## 6. Accessibility

### 6.1 Touch Targets
- All interactive elements: minimum 48dp × 48dp
- Favorite button, card tap, chip tap, navigation items

### 6.2 Semantics
- Product cards: semantic role, merged reading for TalkBack
- Images: `contentDescription` with product title
- Decorative images: `contentDescription = null`
- Buttons: labeled with action ("Add to favorites", "Remove from favorites")

### 6.3 Text Scaling
- All text uses sp units
- Layout must not clip at 200% font scale
- Test with system font size at maximum

### 6.4 Contrast
- Follow Material 3 color system (meets WCAG AA by default)
- Custom colors must meet 4.5:1 contrast ratio for text

### 6.5 TalkBack
- Logical reading order matches visual order
- Cards read as single units with all relevant info
- Navigation announces screen changes
- Loading/error states announced

---

## 7. Dark Mode

- Material 3 dark color scheme
- Dynamic color in dark mode on supported devices
- Surface colors use tonal elevation (not shadow elevation)
- Images not affected by dark mode
- Test all states in both light and dark

---

**Document Status**: DRAFT — Awaiting human review and approval.
