# ShopFlow — Task Backlog

**Version**: 1.0
**Date**: 2026-08-27  
**Status**: APPROVED

---

## Task Status Legend

| Status | Meaning |
|--------|---------|
| PROPOSED | Identified but not yet refined |
| PLANNED | Refined with acceptance criteria |
| READY | Dependencies met, can be started |
| IN_PROGRESS | Actively being worked on |
| BLOCKED | Cannot proceed |
| IN_REVIEW | Complete, awaiting review |
| VERIFIED | Review passed, acceptance criteria met |
| DONE | Verified and closed |
| DEFERRED | Postponed |
| CANCELLED | Will not be done |

---

## M0 — Agentic Development Foundation

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-001 | Create project documentation system | P0 | None | Medium | DONE |
| TASK-002 | Create AI agent instruction files | P0 | None | Medium | DONE |
| TASK-003 | Create Master SRS | P0 | None | High | DONE |
| TASK-004 | Create PRD | P0 | None | Medium | DONE |
| TASK-005 | Create System Architecture | P0 | None | High | DONE |
| TASK-006 | Verify API and create API Specification | P0 | None | Medium | DONE |
| TASK-007 | Create Data Model and ERD | P0 | TASK-006 | High | DONE |
| TASK-008 | Create UI/UX Specification | P0 | None | High | DONE |
| TASK-009 | Create Screen Specifications | P0 | TASK-008 | Medium | DONE |
| TASK-010 | Create Engineering Docs (Tech Stack, Build, Test, Perf, Security) | P0 | None | Medium | DONE |
| TASK-011 | Create ADRs | P0 | TASK-005 | Medium | DONE |
| TASK-012 | Create Roadmap and Task Backlog | P0 | All above | Medium | DONE |
| TASK-013 | Create Status Tracking System | P0 | None | Low | DONE |
| TASK-014 | Create Traceability Matrix | P0 | TASK-003 | Medium | DONE |
| TASK-015 | Create Master Implementation Plan | P0 | All above | High | DONE |
| TASK-016 | Cross-document consistency review | P0 | TASK-015 | Medium | DONE |
| TASK-017 | Present plan for human approval | P0 | TASK-016 | Low | IN_PROGRESS |

## M1 — Project Foundation

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-100 | Add all dependencies to Version Catalog | P0 | M0 approved | Medium | DONE |
| TASK-101 | Architecture / Package Foundation | P0 | TASK-100 | Low | DONE |
| TASK-102 | Hilt / Application DI Setup | P0 | TASK-101 | Medium | DONE |
| TASK-103 | Create base Application class with Hilt | P0 | TASK-101 | Low | DONE |
| TASK-104 | Verify build succeeds | P0 | TASK-103 | Low | DONE |

## M2 — Network Layer

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-200 | Create API response DTOs | P0 | TASK-104 | Medium | DONE |
| TASK-201 | Create Retrofit API service interface | P0 | TASK-200 | Medium | DONE |
| TASK-202 | Create OkHttp client configuration | P0 | TASK-200 | Low | DONE |
| TASK-203 | Provide Retrofit and ProductApi through Hilt | P0 | TASK-201, TASK-202 | Low | DONE |
| TASK-204 | Write API service tests | P1 | TASK-203 | Medium | DONE |

## M3 — Room / Local Data

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-300 | Create Room entities | P0 | TASK-104 | Medium | DONE |
| TASK-301 | Create TypeConverters | P0 | TASK-300 | Low | DONE |
| TASK-302 | Create DAOs | P0 | TASK-300 | Medium | DONE |
| TASK-303 | Create database class | P0 | TASK-302 | Low | DONE |
| TASK-304 | Create Hilt database module | P0 | TASK-303 | Low | DONE |
| TASK-305 | Write DAO tests | P1 | TASK-304 | Medium | DONE |
| TASK-306 | Implement Context-Aware Cache Schema | P0 | TASK-305 | Medium | DONE |

## M4 — Paging + RemoteMediator

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-400 | Create RemoteMediator | P0 | TASK-203, TASK-304 | High | DONE |
| TASK-401 | Implement remote key management | P0 | TASK-400 | Medium | DONE |
| TASK-402 | Configure Pager | P0 | TASK-400 | Medium | DONE |
| TASK-403 | Write RemoteMediator tests | P1 | TASK-402 | High | DONE |

## M5 — Repository + Domain

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-500 | Create domain models | P0 | TASK-104 | Low | DONE |
| TASK-501 | Create repository interfaces | P0 | TASK-500 | Low | DONE |
| TASK-502 | Create mapper functions | P0 | TASK-500, TASK-200, TASK-300 | Medium | DONE |
| TASK-503 | Create repository implementations | P0 | TASK-501, TASK-402 | High | DONE |
| TASK-504 | Create Hilt repository module | P0 | TASK-503 | Low | DONE |
| TASK-505 | Write repository tests | P1 | TASK-504 | Medium | DONE |

## M6 — MVVM / State

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-600 | Create UI state classes | P0 | TASK-500 | Low | DONE |
| TASK-601 | Create ProductListViewModel | P0 | TASK-600, TASK-504 | High | DONE |
| TASK-602 | Create ProductDetailViewModel | P0 | TASK-600, TASK-504 | Medium | READY |
| TASK-603 | Create FavoritesViewModel | P0 | TASK-600, TASK-504 | Medium | READY |
| TASK-604 | Write ViewModel tests | P1 | TASK-601, TASK-602, TASK-603 | High | PLANNED |

## M7 — Product List Screen

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-700 | Create product card composable | P0 | TASK-601 | Medium | PLANNED |
| TASK-701 | Create product list screen | P0 | TASK-700 | High | PLANNED |
| TASK-702 | Implement loading/error/empty states | P0 | TASK-701 | Medium | PLANNED |
| TASK-703 | Create bottom navigation shell | P0 | TASK-701 | Medium | PLANNED |
| TASK-704 | Implement pull-to-refresh | P1 | TASK-701 | Low | PLANNED |

## M8 — Search / Categories

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-800 | Implement search bar UI | P0 | TASK-701 | Medium | PLANNED |
| TASK-801 | Implement search debounce pipeline | P0 | TASK-800, TASK-601 | High | PLANNED |
| TASK-802 | Implement category chips UI | P0 | TASK-701 | Medium | PLANNED |
| TASK-803 | Implement category filtering | P0 | TASK-802, TASK-601 | Medium | PLANNED |
| TASK-804 | Handle search/category interaction | P1 | TASK-801, TASK-803 | Medium | PLANNED |

## M9 — Product Detail Screen

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-900 | Create detail screen layout | P0 | TASK-602 | High | PLANNED |
| TASK-901 | Create image gallery | P1 | TASK-900 | Medium | PLANNED |
| TASK-902 | Create reviews section | P1 | TASK-900 | Medium | PLANNED |
| TASK-903 | Implement navigation list→detail | P0 | TASK-900, TASK-703 | Medium | PLANNED |

## M10 — Favorites

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-1000 | Create favorites screen | P0 | TASK-603 | Medium | PLANNED |
| TASK-1001 | Implement favorite toggle on cards | P0 | TASK-700 | Medium | PLANNED |
| TASK-1002 | Implement favorite toggle on detail | P0 | TASK-900 | Low | PLANNED |
| TASK-1003 | Favorites empty state | P0 | TASK-1000 | Low | PLANNED |

## M11 — Offline UX

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-1100 | Implement offline detection | P0 | TASK-1003 | Medium | PLANNED |
| TASK-1101 | Offline banner/indicator | P1 | TASK-1100 | Low | PLANNED |
| TASK-1102 | Error states with retry | P0 | TASK-1100 | Medium | PLANNED |
| TASK-1103 | First launch without network | P0 | TASK-1100 | Medium | PLANNED |

## M12 — Adaptive UI

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-1200 | Window size class detection | P1 | TASK-1103 | Medium | PLANNED |
| TASK-1201 | Expanded list-detail layout | P1 | TASK-1200 | High | PLANNED |
| TASK-1202 | Navigation rail for expanded | P1 | TASK-1201 | Medium | PLANNED |
| TASK-1203 | Medium layout adjustments | P2 | TASK-1200 | Medium | PLANNED |

## M13 — Accessibility

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-1300 | Touch target verification | P1 | TASK-1203 | Low | PLANNED |
| TASK-1301 | Content descriptions | P1 | TASK-1300 | Medium | PLANNED |
| TASK-1302 | TalkBack traversal | P1 | TASK-1301 | Medium | PLANNED |
| TASK-1303 | Font scaling verification | P1 | TASK-1302 | Low | PLANNED |

## M14 — Testing

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-1400 | Complete unit test coverage | P1 | TASK-1303 | High | PLANNED |
| TASK-1401 | Compose UI tests | P1 | TASK-1400 | High | PLANNED |
| TASK-1402 | Integration tests | P1 | TASK-1401 | High | PLANNED |
| TASK-1403 | Update traceability matrix | P1 | TASK-1402 | Medium | PLANNED |

## M15 — Performance / Release

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-1500 | Enable R8 for release | P1 | TASK-1403 | Medium | PLANNED |
| TASK-1501 | 16 KB page-size verification | P1 | TASK-1500 | Medium | PLANNED |
| TASK-1502 | Performance profiling | P2 | TASK-1500 | Medium | PLANNED |
| TASK-1503 | Release build verification | P1 | TASK-1501 | Low | PLANNED |

## M16 — Final Review

| ID | Title | Priority | Dependencies | Complexity | Status |
|----|-------|----------|-------------|-----------|--------|
| TASK-1600 | Final consistency review | P0 | TASK-1503 | Medium | PLANNED |
| TASK-1601 | Final documentation update | P0 | TASK-1600 | Medium | PLANNED |
| TASK-1602 | Final handoff | P0 | TASK-1601 | Low | PLANNED |

**Document Status**: APPROVED — Implementation in progress.
