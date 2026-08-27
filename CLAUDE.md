# CLAUDE.md — Claude Code Instructions for ShopFlow

## First Action

**Read `AGENTS.md` before doing anything.** It contains the mandatory startup sequence.

## Project

ShopFlow is an offline-first Android product catalog app using Kotlin, Jetpack Compose, Material 3, Room, Retrofit, Paging 3, and Hilt. API: DummyJSON (`https://dummyjson.com/`).

## Canonical Documentation

All project knowledge lives in `docs/`. Do not duplicate it here.

- **Requirements**: `docs/00-project/MASTER_SRS.md`
- **Architecture**: `docs/02-architecture/SYSTEM_ARCHITECTURE.md`
- **API Contract**: `docs/03-data/API_SPECIFICATION.md`
- **Data Model**: `docs/03-data/DATA_MODEL.md`
- **UI/UX**: `docs/04-ui-ux/UI_UX_SPECIFICATION.md`
- **Tech Stack**: `docs/05-engineering/TECH_STACK.md`
- **Implementation Plan**: `docs/06-planning/MASTER_IMPLEMENTATION_PLAN.md`
- **Current Status**: `docs/07-status/PROJECT_STATUS.md`
- **Active Work**: `docs/07-status/CURRENT_WORK.md`
- **Decisions (ADRs)**: `docs/08-decisions/ADR-INDEX.md`

## Rules

1. **Inspect before modifying**: Check Git status, read current task, read relevant docs
2. **Respect ADRs**: Do not silently change approved architecture decisions
3. **Verify before claiming**: Run builds, run tests, check API responses
4. **Update state**: After work, update task files + status docs
5. **No silent architecture changes**: If architecture needs to change, create an ADR and flag for review
6. **No hallucination**: Use `UNKNOWN — NEEDS VERIFICATION` or `TBD — REQUIRES DECISION`
7. **Handoff**: Always produce a handoff (see AGENTS.md)

## Code Conventions

- Kotlin, Jetpack Compose, Material 3
- Package: `com.rktuhin.shopflow`
- MVVM + Clean Architecture layers
- Hilt for DI
- Coroutines + Flow + StateFlow
- Room as single source of truth for UI
- Paging 3 with RemoteMediator
- Type-safe Navigation Compose
- Version Catalog (`gradle/libs.versions.toml`)
- Gradle Kotlin DSL

## Testing

- Unit tests: `app/src/test/`
- Instrumented tests: `app/src/androidTest/`
- See `docs/05-engineering/TEST_STRATEGY.md` for full plan
