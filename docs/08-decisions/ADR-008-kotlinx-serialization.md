# ADR-008 — JSON Serialization (kotlinx.serialization)

**Status**: ACCEPTED  
**Date**: 2026-08-27  
**Deciders**: Principal Android Architect (Agentic)

## Context
The app needs to deserialize JSON from the DummyJSON API and serialize objects into Room (e.g., list of tags or images). We must choose a serialization library.

## Decision
Use `kotlinx.serialization` for all JSON parsing and serialization needs.

## Consequences
**Positive**:
- Kotlin-first, compile-time generated serialization.
- No unnecessary reflection at runtime (better performance, smaller APK).
- Concise DTO definitions.
- Required anyway for Type-Safe Navigation Compose (ADR-004).

**Negative**:
- Requires a separate Kotlin compiler plugin.
- Slightly stricter about nullability and missing fields than Gson (requires explicit default values).

## Alternatives Considered
1. **Gson**: Older, reflection-based. Less performant in Kotlin projects and doesn't handle Kotlin default parameters well without reflection.
2. **Moshi**: Good alternative, but `kotlinx.serialization` provides better synergy with Navigation Compose.
