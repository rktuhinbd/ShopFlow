# ShopFlow — System Architecture

**Version**: 1.0-DRAFT  
**Date**: 2026-08-27  
**Status**: DRAFT — PENDING HUMAN APPROVAL

---

## 1. Overview

ShopFlow follows a layered MVVM architecture with an offline-first data strategy. Room database serves as the single source of truth for the UI. The network layer (Retrofit) feeds data into Room via RemoteMediator, and the UI observes Room through Paging 3's PagingSource and Kotlin Flows.

## 2. Stakeholders & Concerns

| Stakeholder | Concern |
|-------------|---------|
| End User | Fast loading, smooth scrolling, works offline, looks great |
| Developer | Clear architecture, testable components, maintainable code |
| Reviewer | Correct patterns, no anti-patterns, verified behavior |

## 3. System Boundary

```mermaid
graph TB
    subgraph "Device"
        subgraph "ShopFlow App"
            UI["UI Layer<br/>(Compose + ViewModels)"]
            Domain["Domain Layer<br/>(Repository Interfaces + Models)"]
            Data["Data Layer<br/>(Repositories + DataSources)"]
        end
        DB[(Room Database)]
    end
    API["DummyJSON API<br/>https://dummyjson.com"]

    UI --> Domain
    Domain --> Data
    Data --> DB
    Data -->|HTTPS GET| API
    DB --> UI
```

## 4. Architecture Views

### 4.1 Layer View

```mermaid
graph TB
    subgraph "UI Layer"
        Screens["Compose Screens"]
        ViewModels["ViewModels"]
        UiState["UI State / Events"]
    end

    subgraph "Domain Layer"
        RepoInterface["Repository Interfaces"]
        DomainModels["Domain Models"]
        UseCases["Use Cases (where justified)"]
    end

    subgraph "Data Layer"
        RepoImpl["Repository Implementations"]
        RemoteMediator["RemoteMediator"]
        RemoteDS["Remote DataSource (Retrofit)"]
        LocalDS["Local DataSource (Room)"]
    end

    subgraph "Framework"
        Hilt["Hilt DI"]
        Navigation["Navigation Compose"]
        Paging["Paging 3"]
    end

    Screens --> ViewModels
    ViewModels --> UiState
    ViewModels --> RepoInterface
    ViewModels --> UseCases
    UseCases --> RepoInterface
    RepoInterface -.-> RepoImpl
    RepoImpl --> RemoteMediator
    RepoImpl --> RemoteDS
    RepoImpl --> LocalDS
    RemoteMediator --> RemoteDS
    RemoteMediator --> LocalDS
```

### 4.2 Data Flow — Paginated Product List

```mermaid
sequenceDiagram
    participant UI as Compose UI
    participant VM as ViewModel
    participant Pager as Pager
    participant RM as RemoteMediator
    participant API as DummyJSON API
    participant Room as Room DB
    participant PS as PagingSource

    UI->>VM: Observe PagingData
    VM->>Pager: Create Pager(RemoteMediator, PagingSource)
    Pager->>PS: Load from Room
    PS-->>UI: Emit cached pages

    Note over Pager,RM: On REFRESH or APPEND
    Pager->>RM: load(LoadType, PagingState)
    RM->>API: GET /products?limit=N&skip=M
    API-->>RM: ProductsResponse
    RM->>Room: Insert products + remote keys
    RM-->>Pager: MediatorResult.Success
    Room-->>PS: Room invalidation triggers reload
    PS-->>UI: Updated PagingData
```

### 4.3 Data Flow — Search

```mermaid
sequenceDiagram
    participant UI as Search Bar
    participant VM as ViewModel
    participant Flow as StateFlow Pipeline
    participant Repo as Repository
    participant API as DummyJSON API
    participant Room as Room DB

    UI->>VM: Update query text
    VM->>Flow: query StateFlow
    Note over Flow: debounce(300ms)
    Note over Flow: distinctUntilChanged
    Note over Flow: flatMapLatest
    Flow->>Repo: searchProducts(query)
    Repo->>API: GET /products/search?q=query
    API-->>Repo: SearchResponse
    Repo->>Room: Cache search results
    Room-->>VM: Flow<PagingData>
    VM-->>UI: UI State with results
```

### 4.4 Component Diagram

```mermaid
graph LR
    subgraph "UI Components"
        PLS[ProductListScreen]
        PDS[ProductDetailScreen]
        FS[FavoritesScreen]
        Nav[NavigationHost]
    end

    subgraph "ViewModels"
        PLVM[ProductListViewModel]
        PDVM[ProductDetailViewModel]
        FVM[FavoritesViewModel]
    end

    subgraph "Repositories"
        PR[ProductRepository]
        FR[FavoriteRepository]
    end

    subgraph "Data Sources"
        RDS[ProductRemoteDataSource]
        LDS[ProductLocalDataSource]
        FDS[FavoriteLocalDataSource]
    end

    subgraph "Database"
        PDao[ProductDao]
        FDao[FavoriteDao]
        RKDao[RemoteKeyDao]
    end

    subgraph "Network"
        PApi[ProductApiService]
        OkHttp[OkHttpClient]
    end

    PLS --> PLVM
    PDS --> PDVM
    FS --> FVM
    Nav --> PLS
    Nav --> PDS
    Nav --> FS

    PLVM --> PR
    PDVM --> PR
    PDVM --> FR
    FVM --> FR

    PR --> RDS
    PR --> LDS
    FR --> FDS

    RDS --> PApi
    PApi --> OkHttp
    LDS --> PDao
    LDS --> RKDao
    FDS --> FDao
```

### 4.5 Dependency Direction

```mermaid
graph TB
    UI["UI Layer"] -->|depends on| Domain["Domain Layer"]
    Domain -->|depends on| Nothing["(No outward dependencies)"]
    Data["Data Layer"] -->|implements| Domain
    Data -->|depends on| Framework["Framework (Room, Retrofit, etc.)"]

    style Domain fill:#4CAF50,color:#fff
    style UI fill:#2196F3,color:#fff
    style Data fill:#FF9800,color:#fff
```

**Key principle**: Domain layer has no dependencies on Android framework, Room, or Retrofit. It defines interfaces that the Data layer implements.

## 5. Major Components

### 5.1 UI Layer
- **Compose Screens**: Stateless composables receiving state from ViewModels
- **ViewModels**: Hold UI state as StateFlow; handle user events; delegate to repositories
- **Navigation**: Type-safe Navigation Compose with bottom navigation

### 5.2 Domain Layer
- **Repository Interfaces**: Define data contracts (not implementations)
- **Domain Models**: Pure Kotlin data classes representing business entities
- **Use Cases**: Only where they encapsulate non-trivial business logic (avoid ceremony)

### 5.3 Data Layer
- **Repository Implementations**: Coordinate between remote and local data sources
- **RemoteMediator**: Manages network → Room pipeline for Paging 3
- **Remote DataSource**: Retrofit API service for DummyJSON
- **Local DataSource**: Room DAOs for products, favorites, remote keys

## 6. Quality Attributes

| Attribute | Strategy |
|-----------|----------|
| **Testability** | Interfaces for repositories, constructor injection via Hilt, fakes for testing |
| **Offline Resilience** | Room as source of truth; UI never reads directly from network |
| **Performance** | Paging 3 for efficient loading; Coil for image caching; lazy composition |
| **Maintainability** | Clear layer boundaries; stable interfaces; documented ADRs |
| **Adaptability** | Window size classes for responsive layouts; no hardcoded breakpoints |

## 7. Key Architecture Decisions

See `docs/08-decisions/ADR-INDEX.md` for full list.

| ADR | Decision |
|-----|----------|
| ADR-001 | Room as Single Source of Truth |
| ADR-002 | RemoteMediator for Paging |
| ADR-003 | Material 3 Design Language |
| ADR-004 | Type-Safe Navigation |
| ADR-005 | Adaptive Window-Size-Based Layout |
| ADR-006 | Repository/Domain Boundary |

## 8. Trade-offs

| Trade-off | Choice | Rationale |
|-----------|--------|-----------|
| Single module vs. multi-module | Single module | Small project; multi-module adds complexity without proportional benefit |
| Use cases everywhere vs. selective | Selective | Avoid ceremonial pass-through use cases; use when business logic exists |
| Network-first vs. offline-first | Offline-first | Room as source of truth provides better UX, especially on flaky connections |
| Full Clean Architecture vs. pragmatic layers | Pragmatic | Three layers with clear boundaries; no unnecessary abstractions |

---

**Document Status**: DRAFT — Awaiting human review and approval.
