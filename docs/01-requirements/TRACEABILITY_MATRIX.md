# ShopFlow — Requirements Traceability Matrix

**Version**: 1.0-DRAFT  
**Date**: 2026-08-27  
**Status**: DRAFT — PENDING HUMAN APPROVAL

---

## Functional Requirements

| Req ID | Requirement | Architecture | Task(s) | Test | Status |
|--------|-------------|-------------|---------|------|--------|
| FR-101 | Product list display | ProductListScreen, ProductCard | TASK-700, TASK-701 | UI test | PLANNED |
| FR-102 | Paginated loading | Pager, RemoteMediator, PagingSource | TASK-400, TASK-402 | Integration | PLANNED |
| FR-103 | Product card fields | ProductCard composable | TASK-700 | UI test | PLANNED |
| FR-104 | Network → Room caching | RemoteMediator | TASK-400 | Unit test | PLANNED |
| FR-105 | RemoteMediator pattern | RemoteMediator | TASK-400 | Unit test | PLANNED |
| FR-106 | Pull-to-refresh | ProductListScreen | TASK-704 | UI test | PLANNED |
| FR-107 | Loading indicators | ProductListScreen | TASK-702 | UI test | PLANNED |
| FR-201 | Search bar | SearchBar composable | TASK-800 | UI test | PLANNED |
| FR-202 | Search API | ProductApiService, Repository | TASK-801 | Unit test | PLANNED |
| FR-203 | Search debounce | ViewModel Flow pipeline | TASK-801 | Unit test | PLANNED |
| FR-204 | Search results format | ProductListScreen | TASK-801 | UI test | PLANNED |
| FR-205 | Empty search → catalog | ViewModel | TASK-801 | Unit test | PLANNED |
| FR-206 | No results state | ProductListScreen | TASK-702, TASK-801 | UI test | PLANNED |
| FR-207 | Search cancellation | SearchBar | TASK-800 | UI test | PLANNED |
| FR-301 | Category display | CategoryChips | TASK-802 | UI test | PLANNED |
| FR-302 | Category filtering | ViewModel, Repository | TASK-803 | Unit + UI | PLANNED |
| FR-303 | "All" category option | CategoryChips | TASK-802 | UI test | PLANNED |
| FR-304 | Search/category interaction | ViewModel | TASK-804 | Unit test | PLANNED |
| FR-401 | Navigation to detail | NavHost, routes | TASK-903 | Navigation test | PLANNED |
| FR-402 | Detail displays all fields | ProductDetailScreen | TASK-900 | UI test | PLANNED |
| FR-403 | Image gallery | HorizontalPager | TASK-901 | UI test | PLANNED |
| FR-404 | Reviews display | ReviewsSection | TASK-902 | UI test | PLANNED |
| FR-405 | Favorites toggle on detail | ProductDetailScreen | TASK-1002 | UI test | PLANNED |
| FR-501 | Toggle favorite | FavoriteRepository | TASK-1001, TASK-1002 | Unit + Room | PLANNED |
| FR-502 | Favorites persist | Room FavoriteEntity | TASK-300, TASK-302 | Room test | PLANNED |
| FR-503 | Favorites screen | FavoritesScreen | TASK-1000 | UI test | PLANNED |
| FR-504 | Favorites empty state | FavoritesScreen | TASK-1003 | UI test | PLANNED |
| FR-505 | Unfavorite from list | FavoritesScreen | TASK-1000 | UI test | PLANNED |
| FR-506 | Favorite state on cards | ProductCard | TASK-1001 | UI test | PLANNED |
| FR-601 | Bottom navigation | NavigationBar | TASK-703 | UI test | PLANNED |
| FR-602 | Type-safe navigation | NavHost, route classes | TASK-903 | Navigation test | PLANNED |
| FR-603 | Back navigation | NavHost | TASK-903 | Navigation test | PLANNED |

## Non-Functional Requirements

| Req ID | Requirement | Task(s) | Test | Status |
|--------|-------------|---------|------|--------|
| NFR-101 | Cold start <2s | TASK-1502 | Profiling | PLANNED |
| NFR-102 | 60fps scrolling | TASK-1502 | Profiling | PLANNED |
| NFR-201 | No crash on network fail | TASK-1102 | Unit + Integration | PLANNED |
| NFR-301 | Offline cached display | TASK-1100 | Integration | PLANNED |
| NFR-302 | Offline indicator | TASK-1101 | UI test | PLANNED |
| NFR-303 | First launch no network | TASK-1103 | Integration | PLANNED |
| NFR-304 | Favorites offline | TASK-1001 | Unit test | PLANNED |
| NFR-401 | 48dp touch targets | TASK-1300 | UI test | PLANNED |
| NFR-402 | Content descriptions | TASK-1301 | UI test | PLANNED |
| NFR-403 | Text scaling | TASK-1303 | Manual | PLANNED |
| NFR-501 | No sensitive logging | TASK-1500 | Code review | PLANNED |
| NFR-502 | HTTPS only | TASK-202 | Config review | PLANNED |
| NFR-601 | API 24+ support | TASK-104 | Build + test | PLANNED |
| NFR-602 | 16KB page-size | TASK-1501 | APK analysis | PLANNED |
| NFR-604 | Dark mode | TASK-700 | Visual test | PLANNED |
| NFR-701 | Window size classes | TASK-1200 | UI test | PLANNED |
| NFR-702 | List-detail expanded | TASK-1201 | UI test | PLANNED |

## Architectural & Tooling Traceability

| Concept | Location in Plan/Architecture | Task(s) | Status |
|---------|-------------------------------|---------|--------|
| MVVM & Clean/layered | Architecture Overview, ViewModels | M5, M6 | PLANNED |
| SOLID / Dependency Inversion | Repository Layer, Hilt | M1, M5 | PLANNED |
| Hilt | Dependency Injection setup | TASK-100, TASK-105 | PLANNED |
| Coroutines, Flow, StateFlow | ViewModels, UI State | M6, M8 | PLANNED |
| Retrofit, REST | API Contract, Network Layer | M2 | PLANNED |
| Room, Caching, Offline-first | Room Entities, Cache Policy (ADR-009) | M3, M11 | PLANNED |
| Paging 3, RemoteMediator | Paging Strategy, Remote Keys | M4 | PLANNED |
| Compose, Material 3 | UI Architecture (ADR-003) | M7, M9, M10 | PLANNED |
| Navigation, type-safe nav | Navigation (ADR-004) | M1, M9 | PLANNED |
| Sealed types | ViewModel UI States | M6 | PLANNED |
| Testing | Test Strategy (Unit, UI, Integration) | M14 | PLANNED |
| Adaptive UI | Window Size Classes (ADR-005) | M12 | PLANNED |
| Accessibility | Accessibility Strategy | M13 | PLANNED |
| Performance | Profiling, R8, Coil | M15 | PLANNED |
| 16 KB compatibility | Build/Release config | TASK-1501 | PLANNED |
| Release validation | R8, Zipalign, Release Mode | M15 | PLANNED |

---

**Document Status**: REVIEW_READY — Awaiting human review and approval.
