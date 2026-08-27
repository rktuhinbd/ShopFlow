# ShopFlow — Master Implementation Plan

**Version**: 1.0  
**Date**: 2026-08-27  
**Status**: APPROVED
**Plan State**: `DRAFT → REVIEW_READY → HUMAN_REVIEW → [APPROVED] → IMPLEMENTATION`

---

## 1. Executive Summary

ShopFlow is an offline-first Android product catalog application built with Kotlin, Jetpack Compose, Material 3, Room, Retrofit, Paging 3, and Hilt. It consumes the public DummyJSON Products API to display a paginated product catalog with search, category filtering, and local favorites.

This plan covers the complete implementation from project foundation through final release verification, organized into 17 milestones (M0–M16) with 60+ trackable tasks.

**Key architecture decisions** (all ACCEPTED or PROPOSED — awaiting approval):
- Room as single source of truth (ADR-001)
- RemoteMediator for network-backed paging (ADR-002)
- Material 3 design language with dynamic color (ADR-003)
- Type-safe Navigation Compose (ADR-004)
- Adaptive window-size-based layouts (ADR-005)
- Pragmatic repository/domain boundary (ADR-006)
- Search/Category Interaction: Mutually Exclusive (ADR-007)
- JSON Serialization: kotlinx.serialization (ADR-008)
- Cache Freshness Policy: 15-minute window (ADR-009)
- Search Paging Strategy: Network-only for search, Room for catalog/category (ADR-010)

## 2. Confirmed Requirements

- **32 functional requirements** (FR-101 through FR-603) — see `docs/00-project/MASTER_SRS.md`
- **21 non-functional requirements** (NFR-101 through NFR-702) — see `docs/00-project/MASTER_SRS.md`
- Full traceability in `docs/01-requirements/TRACEABILITY_MATRIX.md`

## 3. Assumptions

- DummyJSON API remains publicly available (verified 2026-08-27)
- API response schema remains stable (verified with live requests)
- Total products: ~194; dataset is small but pagination must be implemented correctly
- No authentication required
- Read-only API usage (GET only)

## 4. Architecture Overview

```
UI Layer (Compose + ViewModels + Sealed Types for State)
    ↕ StateFlow / PagingData
Domain Layer (Repository Interfaces + Domain Models)  <-- Dependency Inversion (SOLID)
    ↕ Interfaces
Data Layer (Repositories + RemoteMediator + DataSources)
    ↕ Room / Retrofit (REST)
Framework (Room DB ← → DummyJSON API)
```

Room is the single source of truth. The UI never reads directly from network responses. RemoteMediator coordinates network→Room writes. PagingSource delivers Room data to the UI.

Full architecture in `docs/02-architecture/SYSTEM_ARCHITECTURE.md`.

## 5. API Contract

Verified endpoints (tested with `curl` on 2026-08-27):

| Endpoint | Purpose | Response |
|----------|---------|----------|
| `GET /products?limit=N&skip=M` | Paginated product list | `{ products: [...], total, skip, limit }` |
| `GET /products/{id}` | Single product | Product object |
| `GET /products/search?q=X` | Search | Same pagination wrapper |
| `GET /products/categories` | Category list | `[{ slug, name, url }]` |
| `GET /products/category/{slug}` | Products by category | Same pagination wrapper |

Full contract in `docs/03-data/API_SPECIFICATION.md`.

## 6. Data Model

### Room Entities

| Entity | Purpose | PK |
|--------|---------|-----|
| `ProductEntity` | Cached product data (25 columns) | `id` (API) |
| `FavoriteEntity` | User favorites | `productId` |
| `RemoteKeyEntity` | Pagination state tracking | `productId` |
| `CategoryEntity` | Cached category list | `slug` |

### Synchronization Strategy

| Scenario | Behavior |
|----------|----------|
| First launch (online) | RemoteMediator fetches → Room → UI |
| Cached launch | PagingSource emits cache; RemoteMediator refreshes background |
| Offline with cache | PagingSource emits cache; error suppressed |
| Offline without cache | Error state with retry |
| Refresh | Clear products + keys in transaction, refetch |

Full model in `docs/03-data/DATA_MODEL.md` and `docs/03-data/ERD.md`.

## 7. Paging Strategy

- **PagingConfig**: pageSize=20, prefetchDistance=5, initialLoadSize=20
- **RemoteMediator**: Handles REFRESH (clear + refetch) and APPEND (next page) for Catalog and Category.
- **PREPEND**: Returns `MediatorResult.Success(endOfPaginationReached=true)`
- **Remote keys**: Per-product key with prevKey, currentPage, nextKey, query, createdAt
- **Skip calculation**: `skip = page * pageSize`
- **End detection**: `response.products.isEmpty()` or `skip + limit >= total`
- **Search**: Uses network-only PagingSource to avoid cache pollution (ADR-010)
- **Category filter**: Filter via separate RemoteMediator/PagingSource combo tied to query identity (ADR-010)
- **Cache Freshness**: 15-minute window. Stale data displays immediately but triggers background sync (ADR-009)

## 8. Search Strategy

```
User types query
    → StateFlow<String>
    → debounce(300ms)
    → distinctUntilChanged()
    → flatMapLatest { query →
        if (query.isBlank()) defaultCatalogPager
        else searchPager(query)
      }
    → PagingData<Product>
    → UI
```

- Blank query returns to default paginated catalog
- Search cancels previous request via flatMapLatest
- Search/category interaction: **Mutually Exclusive** — selecting one clears the other (ADR-007)

## 9. Repository Layer

| Repository | Responsibilities |
|-----------|------------------|
| `ProductRepository` | Products (paginated), search, categories, single product. Maps DTOs ↔ Entities ↔ Domain Models. |
| `FavoriteRepository` | Add/remove/query favorites |

Interfaces in domain layer; implementations in data layer. Hilt provides **dependency inversion**, adhering to **SOLID** principles. Explicit separation is maintained between API DTOs, Room Entities, and UI Domain Models.

## 10. ViewModel Layer

| ViewModel | State | Data Sources |
|-----------|-------|-------------|
| `ProductListViewModel` | `StateFlow` of sealed UI state (Products, search query, loading/error) | ProductRepository |
| `ProductDetailViewModel` | `StateFlow` of sealed UI state (Product detail, favorite state) | ProductRepository, FavoriteRepository |
| `FavoritesViewModel` | `StateFlow` of sealed UI state (Favorites list) | FavoriteRepository |

## 11. UI Architecture

### Screens
- **ProductListScreen**: Paginated list + search + categories + bottom nav
- **ProductDetailScreen**: Full product info + image gallery + reviews + favorite
- **FavoritesScreen**: Favorites list + empty state + bottom nav

### Navigation
- Bottom navigation: Products / Favorites
- Push navigation: List → Detail
- Type-safe routes with kotlinx.serialization (ADR-004)

### Adaptive Layouts (ADR-005)
- Compact (<600dp): Single column, full-screen detail
- Medium (600–840dp): Wider cards, full-screen detail
- Expanded (>840dp): List-detail split, navigation rail

Full UI spec in `docs/04-ui-ux/UI_UX_SPECIFICATION.md`.

## 12. Accessibility

- 48dp minimum touch targets
- Content descriptions on all images
- TalkBack traversal order
- Font scaling support (200%)
- Contrast ratios per Material 3
- Non-color-only information indicators

## 13. Performance Strategy

- Measure first, optimize evidence-based
- Key areas: startup, list scrolling, image loading, Room queries, search latency
- R8 enabled for release
- Baseline Profile considered if startup is slow
- Targets: cold start <2s, 60fps scroll, search <500ms

## 14. Security

- No API keys needed (public API)
- HTTPS only
- No logging in production
- Debug features stripped in release
- App-private Room database

## 15. Build / Release / 16KB

- compileSdk 37, targetSdk 37, minSdk 24
- Gradle Kotlin DSL + Version Catalog
- 16KB page-size verification via `zipalign -c -P 16 4`
- R8 with proper keep rules for Retrofit, Room, Hilt, serialization

## 16. Test Strategy

| Level | Scope | Tools |
|-------|-------|-------|
| Unit | Repository, ViewModel, RemoteMediator, Mappers | JUnit, Coroutines Test, Fakes |
| Room | DAOs, queries, transactions | In-memory Room DB |
| UI | Compose screens, states, interactions | ComposeTestRule |
| Integration | Paging pipeline, offline flow, search pipeline | Combined |

Full strategy in `docs/05-engineering/TEST_STRATEGY.md`.

## 17. Milestones

| Milestone | Title | Dependencies | Key Deliverables |
|-----------|-------|-------------|------------------|
| M0 | Agentic Foundation | None | ✅ Documentation, planning, agent system |
| M1 | Project Foundation | M0 | Dependencies, Hilt, package structure |
| M2 | Network Layer | M1 | Retrofit, DTOs, API service |
| M3 | Room / Local Data | M1 | Entities, DAOs, database |
| M4 | Paging + RemoteMediator | M2, M3 | Paging pipeline |
| M5 | Repository + Domain | M4 | Domain models, repositories |
| M6 | MVVM / State | M5 | ViewModels, UI state |
| M7 | Product List | M6 | List screen with pagination |
| M8 | Search / Categories | M7 | Search + category filtering |
| M9 | Product Detail | M7 | Detail screen |
| M10 | Favorites | M7, M9 | Favorites screen + toggle |
| M11 | Offline UX | M10 | Offline states + indicators |
| M12 | Adaptive UI | M11 | Window size classes + list-detail |
| M13 | Accessibility | M12 | Touch targets, TalkBack, scaling |
| M14 | Testing | M13 | Full test coverage |
| M15 | Performance / Release | M14 | R8, 16KB, profiling |
| M16 | Final Review | M15 | Consistency review, handoff |

Full roadmap in `docs/06-planning/ROADMAP.md`.

## 18. Open Decisions

| Decision | Options | Impact | Status |
|----------|---------|--------|--------|
| KSP vs. KAPT for Hilt | KSP (preferred) vs. KAPT (if KSP incompatible) | Build performance | TBD (Wait for implementation test) |

## 19. Acceptance Criteria (Definition of Done)

The project is DONE when:
- [ ] All FR requirements have passing tests
- [ ] All NFR requirements are verified
- [ ] App builds in release mode without errors
- [ ] 16KB page-size compatibility verified
- [ ] All ADRs are finalized (ACCEPTED)
- [ ] Traceability matrix fully populated
- [ ] All status documents updated
- [ ] Handoff document complete

## 20. ADR References

| ADR | Title | Status |
|-----|-------|--------|
| ADR-001 | Room as Single Source of Truth | ACCEPTED |
| ADR-002 | RemoteMediator for Paging | ACCEPTED |
| ADR-003 | Material 3 Design Language | ACCEPTED |
| ADR-004 | Type-Safe Navigation | ACCEPTED |
| ADR-005 | Adaptive Window-Size-Based Layout | ACCEPTED |
| ADR-006 | Repository/Domain Boundary | ACCEPTED |
| ADR-007 | Search/Category Interaction | ACCEPTED |
| ADR-008 | JSON Serialization | ACCEPTED |
| ADR-009 | Cache Freshness Policy | ACCEPTED |
| ADR-010 | Search Paging Strategy | ACCEPTED |

---

## APPROVAL

| Field | Value |
|-------|-------|
| Plan Version | 1.0 |
| Plan Status | APPROVED |
| Approval Date | 2026-08-27 |
| Approval Statement | "Implementation may begin. Implementation is now authorized." |
| Approved Scope | Master SRS, PRD, Architecture, Data Model, API Spec, Offline-first, Paging, UI/UX, Testing, ADRs, Traceability |
| Approved ADR Set | ADR-001 through ADR-010 |

---

**Document Status**: APPROVED — Implementation in progress.
