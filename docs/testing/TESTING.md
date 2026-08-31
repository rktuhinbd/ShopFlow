# Build & Testing

Quality in ShopFlow is maintained through automated testing across multiple layers of the application.

## Testing Strategy

The testing pyramid is divided into:

1. **UNIT TESTS**: Fast, local JVM tests for business logic, ViewModels, and state management.
2. **INSTRUMENTATION TESTS**: Slower, device-based tests primarily for Jetpack Compose UI validation and Room database migrations.
3. **MANUAL DEVICE QA**: Final verification by engineers and QA on physical devices to validate animations, offline transitions, and tactile feel.

## Unit Testing

Unit tests run on the local JVM and do not require an Android emulator. We use `kotlinx-coroutines-test` for deterministic testing of asynchronous flows.

**Run all unit tests:**
```powershell
.\gradlew testDebugUnitTest
```

### Conventions
- Use `Fake` repositories or MockK for isolating the subject under test.
- ViewModel tests should verify state emissions (`StateFlow`) in response to intent (method calls).
- Domain logic must have 100% path coverage.

## Instrumentation Testing

Instrumentation tests require a connected Android device or emulator. They are located in `app/src/androidTest`.

**Run all instrumentation tests:**
```powershell
.\gradlew connectedDebugAndroidTest
```

### Compose UI Testing
ShopFlow uses Compose UI testing utilities (`createComposeRule`) to verify UI behavior, navigation, and state rendering.
- UI components should be tested in isolation where possible.
- Full screen tests verify integration between the ViewModel (or a fake) and the UI.
- Use `testTags` or semantic content descriptions to identify nodes.

### Room Database Testing
Room DAOs are tested as instrumentation tests using an in-memory database (`Room.inMemoryDatabaseBuilder`) to verify complex queries and local persistence behavior.

## Test Naming

Tests should be descriptive, following the pattern: `MethodName_StateUnderTest_ExpectedBehavior`.

Example: `fetchProducts_networkError_emitsErrorState`
