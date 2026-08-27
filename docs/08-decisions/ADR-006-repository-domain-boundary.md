# ADR-006 — Repository/Domain Boundary

**Status**: ACCEPTED  
**Date**: 2026-08-27  
**Deciders**: Principal Android Architect (Agentic)

## Context

Clean Architecture prescribes use case classes between ViewModel and Repository. For a simple catalog app, many use cases would be trivial pass-throughs.

## Decision

Use a pragmatic layered architecture:
- **Domain layer** defines repository interfaces and domain models (pure Kotlin)
- **Data layer** implements repositories
- **Use cases** are created only when non-trivial business logic exists (e.g., combining data from multiple repositories)
- ViewModels may call repository interfaces directly when no additional logic is needed

## Consequences

**Positive**: Avoids ceremonial code; faster development; domain layer still provides clean interface boundary; testable via interface substitution.

**Negative**: Less strict separation; if business logic grows, use cases need to be retrofitted.

## Alternatives Considered

1. **Mandatory use cases for everything**: Maximum separation but creates dozens of trivial pass-through classes
2. **No domain layer (ViewModel → Data directly)**: Fastest development but poor testability and coupling
