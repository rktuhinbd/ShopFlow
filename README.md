# ShopFlow

**A modern, production-oriented native Android e-commerce application focused on product discovery and offline reliability.**

---

## Status
**Active Development** - Core architecture and fundamental browsing features (Catalog, Detail, Favorites) are currently implemented.

## Feature Highlights
- **Seamless Product Discovery**: Browse paginated product catalogs quickly and efficiently.
- **Robust Offline-First Experience**: Browse cached products even when network connectivity drops.
- **Favorites**: Curate items locally for later evaluation.
- **Adaptive UI**: Built entirely with Jetpack Compose to gracefully handle different Android devices.

## Architecture Overview
ShopFlow implements a strict **Clean Architecture** utilizing a **Unidirectional Data Flow (UDF)**. 

The application is inherently offline-first. The UI observes a local Room database, which acts as the single source of truth. A `RemoteMediator` quietly synchronizes this local database with a remote API via Retrofit in the background, ensuring the UI is never blocked by network latency.

## Technology Stack
- **Platform**: Android (API 24 minimum, targeting API 35)
- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Architecture**: ViewModel, Kotlin Flow / StateFlow
- **Dependency Injection**: Hilt
- **Local Database**: Room
- **Networking**: Retrofit, OkHttp, Kotlinx Serialization
- **Pagination**: Paging 3
- **Image Loading**: Coil
- **Testing**: JUnit, Espresso, kotlinx-coroutines-test

## Project Structure
The repository is structured by feature inside a clean architectural boundary:
- `data/`: Room entities, Retrofit services, Paging mediators, Repository implementations.
- `domain/`: Business models and repository interfaces.
- `ui/`: Compose screens, generic components, ViewModels, and Navigation.
- `di/`: Hilt modules.

## Getting Started

1. **Clone the repository**:
   `git clone <repo-url> ShopFlow`
2. **Open the project** in Android Studio (Ladybug or newer recommended).
3. Ensure you have **Java 17** configured in your IDE.
4. Allow Gradle to sync.

## Build and Test
Run from the root of the project using the Gradle wrapper:

- **Build Debug APK**: `.\gradlew assembleDebug`
- **Run Unit Tests**: `.\gradlew testDebugUnitTest`
- **Run Instrumentation Tests**: `.\gradlew connectedDebugAndroidTest`

## Offline-First Behavior
Network connectivity is actively monitored. If the device goes offline, the UI relies entirely on the Room cache, displaying a subtle indicator. There are no blocking network calls in the presentation layer.

## UI/UX Design System
ShopFlow intentionally subdues its UI to let product photography stand out. While built on Material 3, the design system favors clarity, restraint, and an Apple-inspired minimal aesthetic. We use semantic tokens instead of hardcoded colors, ensuring robust Light/Dark mode support.

## Engineering Principles
- **No GlobalScope**: Strict structured concurrency.
- **Immutable State**: StateFlow and read-only properties dominate the UI layer.
- **Testability**: Heavy use of dependency injection and interface-driven design.

## Documentation Map
Detailed documentation can be found in the `docs/` directory. Start at the [Documentation Index](docs/README.md).

## Contribution
Please see [CONTRIBUTING.md](CONTRIBUTING.md) for branching strategies, PR templates, and coding standards.

## Security
For vulnerability reporting and secure coding practices, refer to [SECURITY.md](SECURITY.md).

## License
*Licensing is not currently specified for this repository.*
