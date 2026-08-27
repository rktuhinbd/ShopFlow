# ShopFlow — Test Strategy

**Version**: 1.0-DRAFT  
**Date**: 2026-08-27  
**Status**: DRAFT — PENDING HUMAN APPROVAL

---

## 1. Philosophy

- **Behavioral testing** over structural testing
- **Fakes** over mocks where appropriate
- Test what the code does, not how it's implemented
- Every requirement should have at least one test
- Test the contract, not the implementation detail

## 2. Test Levels

### 2.1 Unit Tests (`app/src/test/`)

| Component | What to Test | Approach |
|-----------|-------------|----------|
| **Repository** | Data coordination logic | Fake data sources |
| **ViewModel** | State management, events, error handling | Fake repository, TestDispatcher |
| **RemoteMediator** | Load types (REFRESH, APPEND), error handling | Fake API + in-memory Room |
| **Domain models** | Mapping, validation | Pure unit tests |
| **TypeConverters** | JSON ↔ Kotlin serialization roundtrip | Pure unit tests |

### 2.2 Room Tests (`app/src/androidTest/`)

| Component | What to Test | Approach |
|-----------|-------------|----------|
| **ProductDao** | CRUD, queries, pagination source | In-memory Room database |
| **FavoriteDao** | Insert, delete, join queries | In-memory Room database |
| **RemoteKeyDao** | Key storage, retrieval, clearing | In-memory Room database |
| **Migrations** | Schema migrations (when applicable) | MigrationTestHelper |

### 2.3 Compose UI Tests (`app/src/androidTest/`)

| Component | What to Test | Approach |
|-----------|-------------|----------|
| **Product Card** | Content display, click, favorite toggle | ComposeTestRule |
| **Product List** | Loading, content, error, empty states | ComposeTestRule + fake data |
| **Product Detail** | Content display, image gallery, favorite | ComposeTestRule |
| **Favorites** | List display, empty state, unfavorite | ComposeTestRule |
| **Search** | Input, results, clear | ComposeTestRule |
| **Navigation** | Tab switching, forward/back | NavHost test |

### 2.4 Integration Tests

| Test | Scope | Approach |
|------|-------|----------|
| **Paging pipeline** | Pager + RemoteMediator + Room + PagingSource | In-memory Room + fake API |
| **Offline flow** | Repository behavior without network | Fake API returning errors |
| **Search flow** | Query → debounce → API → Room → UI | Turbine + fake API |

## 3. Test Infrastructure

### Fakes Needed
- `FakeProductApiService` — returns predefined responses or errors
- `FakeProductRepository` — for ViewModel tests
- `FakeFavoriteRepository` — for ViewModel tests

### Test Utilities
- `TestDispatcherRule` — JUnit rule for coroutine test dispatchers
- `TestProductFactory` — creates test Product instances
- `ComposeTestRule` — for UI tests

## 4. Requirement ↔ Test Mapping

See `docs/01-requirements/TRACEABILITY_MATRIX.md` for full mapping.

Key requirement coverage:

| Requirement | Test Level | Test Description |
|-------------|-----------|-----------------|
| FR-101 | UI | Product list renders with correct fields |
| FR-102 | Integration | Scrolling triggers pagination |
| FR-104 | Unit + Room | Products cached in Room after network fetch |
| FR-105 | Unit | RemoteMediator handles REFRESH and APPEND |
| FR-201 | UI | Search bar visible and functional |
| FR-203 | Unit | Search debounces correctly |
| FR-301 | Unit + UI | Categories load and display |
| FR-401 | UI + Navigation | Tap navigates to detail |
| FR-501 | Unit + Room | Favorite toggle persists |
| NFR-201 | Unit | No crash on network failure |
| NFR-301 | Integration | Cached products display offline |

## 5. Test Commands

```bash
# All unit tests
./gradlew test

# Specific test class
./gradlew test --tests "com.rktuhin.shopflow.*"

# All instrumented tests
./gradlew connectedAndroidTest

# With coverage report
./gradlew testDebugUnitTest jacocoTestReport
```

## 6. Quality Gate

A task is verified only when:
- All existing tests pass
- New tests cover the implemented requirements
- No regressions in existing functionality
- Build succeeds without errors

---

**Document Status**: DRAFT — Awaiting human review and approval.
