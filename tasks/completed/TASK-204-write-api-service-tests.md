# TASK-204: Write API service tests

**Status**: DONE
**Milestone**: M2

## Objective
Create focused integration tests for the Retrofit/ProductApi layer using MockWebServer.

## Acceptance Criteria
- [x] ProductApi is tested through a controlled HTTP server
- [x] `/products` is tested
- [x] `/products/search` is tested
- [x] `/products/categories` is tested
- [x] `/products/category/{category}` is tested
- [x] `/products/{id}` is tested
- [x] requests are verified
- [x] DTO deserialization is verified
- [x] at least representative HTTP failures are tested
- [x] tests do not call the real internet
- [x] no production networking behavior was weakened
- [x] no secrets were introduced
- [x] all tests pass
- [x] debug build passes
- [x] project state is accurate
- [x] task evidence is recorded
- [x] task is moved to completed

## Evidence
Tests created in `app/src/test/java/com/rktuhin/shopflow/data/remote/api/ProductApiTest.kt`.
Dependencies added:
- `com.squareup.okhttp3:mockwebserver:4.12.0`
- `org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1`

Test executed via `./gradlew assembleDebug testDebugUnitTest` and passed successfully in 48s.
