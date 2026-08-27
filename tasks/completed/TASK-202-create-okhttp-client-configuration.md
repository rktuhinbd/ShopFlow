# TASK-202: Create OkHttp client configuration

## Objective
Create the foundational OkHttp HTTP client configuration required by ShopFlow, including sensible timeouts and environment-appropriate logging. Provide the client via a Hilt module (`NetworkModule`).

## Status
DONE

## Implementation Details
- Created `NetworkModule.kt` in `di` package.
- Configured 30 seconds connection, read, and write timeouts as a reasonable default for mobile clients.
- Configured `HttpLoggingInterceptor` to use `BODY` logging only when `BuildConfig.DEBUG` is true, keeping production (release builds) secure by setting it to `NONE`.
- Added explicit redaction for sensitive headers (`Authorization`, `Cookie`, etc.) in `HttpLoggingInterceptor`.
- Created `AuthTokenProvider` domain interface and `NoOpAuthTokenProvider` implementation.
- Added `AuthInterceptor` to dynamically attach `Authorization: Bearer <token>` if a token is present.
- Provided the `OkHttpClient` as a `@Singleton` dependency via Dagger Hilt.
- Did not configure any caching because the offline-first architecture uses Room for application caching.
- Did not implement authentication infrastructure since DummyJSON requires none.

## Verification Evidence
- Successfully ran `$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"; .\gradlew assembleDebug testDebugUnitTest` and all tests passed.
- Wrote `NetworkModuleTest.kt` unit test to verify timeout values (30s) and the correct addition of the `HttpLoggingInterceptor` conditionally.
- Security constraints were preserved (no disabled TLS).

## Next Steps
- Human Review
- Git Checkpoint
- TASK-203 — Create Hilt network module (Though part of this is already bootstrapped for OkHttp, Retrofit needs it next)
