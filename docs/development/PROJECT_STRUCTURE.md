# Project Structure

ShopFlow organizes its source code by feature and architectural layer, adhering to Clean Architecture principles.

## Package Layout (`app/src/main/java/com/rktuhin/shopflow/`)

```
com.rktuhin.shopflow/
│
├── ShopFlowApplication.kt      # Application class, Hilt entry point
│
├── data/                       # Data Layer (Implementations)
│   ├── local/                  # Room database, DAOs, Entities
│   ├── network/                # Retrofit API services, DTOs, Connectivity
│   ├── paging/                 # Paging3 RemoteMediators
│   └── repository/             # Repository implementations
│
├── di/                         # Dependency Injection
│   └── NetworkModule.kt        # Hilt modules (Network, Database, Repositories)
│
├── domain/                     # Domain Layer (Interfaces & Models)
│   ├── model/                  # Domain business models
│   └── repository/             # Repository interfaces
│
└── ui/                         # Presentation Layer (Compose)
    ├── components/             # Reusable UI components (Buttons, Cards, Loaders)
    ├── favorites/              # Favorites screen and ViewModel
    ├── navigation/             # Navigation graphs and routes
    ├── productdetail/          # Product Detail screen and ViewModel
    ├── productlist/            # Product List screen and ViewModel
    └── theme/                  # Material 3 Theme (Color, Type, Shape, Theme)
```

## Layer Responsibilities

- **`data`**: Responsible for fetching, saving, and caching data. It knows about network APIs and local databases. It implements the interfaces defined in the `domain` layer.
- **`domain`**: The core of the application. It contains business models and repository interfaces. It has no dependencies on Android framework classes, UI, or specific data implementations.
- **`ui`**: Contains Jetpack Compose screens, generic UI components, ViewModels, and navigation logic. It consumes data from the `domain`/`data` layers and translates it into UI state.
- **`di`**: Centralized configuration for Hilt dependency injection, wiring the implementation classes (`data`) to their abstract definitions (`domain`) for use in the `ui` layer.
