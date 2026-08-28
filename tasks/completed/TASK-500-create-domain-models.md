# TASK-500: Create domain models

## Objective
Create pure Kotlin data classes for domain concepts (`Product`, `Category`, `Review`).

## Scope
- `com.rktuhin.shopflow.domain.model.Product`
- `com.rktuhin.shopflow.domain.model.Category`
- `com.rktuhin.shopflow.domain.model.Review`

## Model Decisions
- **Product**: Extracted core catalog properties. Excluded `cachedAt`, `prevKey`, `nextKey`, `query`, and `meta`. Favorited state is NOT included (kept separate).
- **Category**: Kept `slug`, `name`, `url`. Excluded `cachedAt`.
- **Review**: Kept `rating`, `comment`, `date`, `reviewerName`. Excluded email/PII.

## Acceptance Criteria
- [x] Product.kt exists
- [x] Review.kt exists
- [x] Category.kt exists
- [x] all are pure Kotlin domain models
- [x] no framework annotations/imports
- [x] no Room/network/paging fields
- [x] Product fields match authoritative requirements
- [x] Review contains only required domain fields
- [x] Category contains only required domain fields
- [x] favorite state is not incorrectly embedded in Product
- [x] no Repository created
- [x] no Mapper created
- [x] no SearchPagingSource
- [x] build passes
- [x] applicable tests pass
- [x] status documents are synchronized
- [x] ROADMAP is synchronized
- [x] task file contains verification evidence

## Verification Evidence
- Domain purity verified by manual inspection.
- Build and JVM tests pass: `.\gradlew clean assembleDebug testDebugUnitTest` exited with code 0 (BUILD SUCCESSFUL).
- Instrumented tests: NOT APPLICABLE / NOT REQUIRED (TASK-500 contains only pure Kotlin domain models).
