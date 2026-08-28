# TASK-700: Create ProductCard Composable

## Overview
Implemented the reusable `ProductCard` composable for the ShopFlow application. This component is used in product lists and favorites to display a product's thumbnail, title, brand, rating, price (with discount), and favorite state.

## Implementation Details
- **Component File**: `app/src/main/java/com/rktuhin/shopflow/ui/components/ProductCard.kt`
- **Test File**: `app/src/androidTest/java/com/rktuhin/shopflow/ui/components/ProductCardTest.kt`
- **Dependencies Added**: `androidx.compose.material:material-icons-extended` (for `Icons.Filled.FavoriteBorder`).
- **Features**:
  - `ElevatedCard` with standard Material 3 design and 12dp padding.
  - Image loading using `coil.compose.AsyncImage` with a 1:1 aspect ratio.
  - Semantic integration for screen readers.
  - Discounted price logic (calculated from `price` and `discountPercentage`) with strikethrough for the original price.
  - Interactive favorites icon utilizing the caller-provided `isFavorite` state and `onFavoriteClick` callback.

## Verification
- Previews added for default state and favorited state.
- Compose UI tests run successfully covering product information display, favorite icon state, and click callbacks.
- Verified no business logic, ViewModels, or Repositories were altered.

## Status
DONE
