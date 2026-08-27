# ADR-004 — Type-Safe Navigation

**Status**: ACCEPTED  
**Date**: 2026-08-27  
**Deciders**: Principal Android Architect (Agentic)

## Context

Navigation Compose historically used string-based routes, which are error-prone. Recent versions support type-safe navigation with Kotlin serialization.

## Decision

Use type-safe Navigation Compose routes using Kotlin serializable classes/objects for route definitions.

## Consequences

**Positive**: Compile-time route safety; no string typos; type-safe arguments; better refactoring support.

**Negative**: Requires kotlinx.serialization plugin; slightly more setup than string routes.

## Alternatives Considered

1. **String-based routes**: Simpler setup but error-prone, no compile-time safety
2. **Third-party navigation (Voyager, Decompose)**: More features but unnecessary complexity for this project
