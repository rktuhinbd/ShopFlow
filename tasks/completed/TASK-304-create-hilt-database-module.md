# TASK-304: Create Hilt database module

Status: DONE

## Evidence
- **Hilt module**: Created `DatabaseModule` in `com.rktuhin.shopflow.di` package.
- **Database provider**: Created `@Provides @Singleton fun provideShopFlowDatabase` using `@ApplicationContext`. The provider is application-scoped to ensure the Room database is a singleton.
- **DAO providers**: Exposed `ProductDao`, `FavoriteDao`, `CategoryDao`, `RemoteKeyDao`. All obtain their instance from the provided `ShopFlowDatabase`.
- **Context**: Used `@ApplicationContext` to prevent memory leaks and ensure global availability.
- **Migration**: No destructive migration fallback exists. The explicit migration policy is preserved.
- **Hilt Graph**: 
```text
SingletonComponent
        │
        ▼
ShopFlowDatabase
        │
        ├── ProductDao
        ├── FavoriteDao
        ├── CategoryDao
        └── RemoteKeyDao
```
- **Tests**: Build verification (`assembleDebug` and `testDebugUnitTest`) acts as a test for the DI setup, confirming no KSP errors and successful compile time injection graph generation.
- **Build**: `./gradlew clean assembleDebug testDebugUnitTest` passed successfully.
