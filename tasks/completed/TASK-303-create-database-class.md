# TASK-303: Create database class

Status: DONE

## Evidence
- **Room version**: Room 2.8.4
- **KSP2**: Enabled
- **DAO Methods**: `suspend` modifier restored for all one-shot DAO write operations.
- **Compatibility**: Original Room 2.6.1 / KSP2 compatibility issue causing generic erasure exceptions with suspend/Unit methods was resolved entirely through the dependency upgrade to Room 2.8.4.
- **room-ktx**: Room's current core API provides native Kotlin coroutine `suspend` and Flow support. `room-ktx` is not strictly required for these features. It remains included only if the project has another concrete reason for using it.
- **Database**: Created `ShopFlowDatabase.kt`. Registered `ProductEntity`, `FavoriteEntity`, `CategoryEntity`, `RemoteKeyEntity`. Registered `StringListConverter`, `ProductReviewListConverter`. Exposed `ProductDao`, `FavoriteDao`, `CategoryDao`, `RemoteKeyDao`.
- **Migrations**: No destructive migration fallback is used.
- **Schema Export**: Configured KSP Room schema export to `app/schemas` via `ksp { arg("room.schemaLocation", "$projectDir/schemas") }`. Schema export is enabled.
- **Schema Tracking**: Schema snapshots are explicitly intended for tracking via Git repository policy to validate future auto-migrations.
- **Verification**: Passed `assembleDebug` and `testDebugUnitTest` with the restored coroutine DAO contracts.