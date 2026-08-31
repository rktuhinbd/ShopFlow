# Changelog

All notable changes to the ShopFlow project will be documented in this file.

## [Unreleased]

### Added (Currently Implemented Capabilities)
- **Product Discovery**: Paginated catalog of products fetched via remote API.
- **Offline-First Architecture**: Room database integration serving as the single source of truth, allowing seamless browsing without network connectivity.
- **Paging Integration**: Paging 3 and RemoteMediator implemented for efficient, infinite scrolling of the product list.
- **Product Details**: Detailed viewing screen for individual products with high-resolution imagery.
- **Favorites System**: Ability to save and view curated products locally.
- **Design System**: Restrained, clarity-focused UI implementation based on Material 3 with specific semantic colors and precise 4dp grid typography/spacing rules.
- **Connectivity Monitoring**: Real-time detection of network availability, surfaced via an unobtrusive offline banner in the UI.
- **Dependency Injection**: Complete Hilt setup for ViewModels, Repositories, Database, and Network dependencies.
