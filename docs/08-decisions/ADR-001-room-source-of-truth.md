# ADR-001 — Room as Single Source of Truth

**Status**: ACCEPTED  
**Date**: 2026-08-27  
**Deciders**: Principal Android Architect (Agentic)

## Context

The app needs to display products from a remote API while supporting offline access. Two common patterns exist: (1) Network-first with Room as fallback cache, or (2) Room as the single source of truth where the UI always reads from Room and the network writes into Room.

## Decision

Room is the single source of truth for the persistent catalog experience (browse and category). Text search is network-backed and is not required to function offline (ADR-010).

- The UI layer (Compose/ViewModel) reads catalog data exclusively from Room via PagingSource and Flow
- The network layer (Retrofit) writes into Room via RemoteMediator
- The UI never reads directly from network responses for the main catalog

## Consequences

**Positive**:
- Consistent data source for UI regardless of network state
- Natural offline support — cache is always the primary data path
- Paging 3's PagingSource works directly with Room
- No complex state merging between network and cache
- Single source of truth eliminates data inconsistency bugs

**Negative**:
- First load requires network → Room → UI pipeline (slightly slower than direct network)
- Cache invalidation must be explicitly managed
- Room schema must accommodate all displayable API fields

## Alternatives Considered

1. **Network-first with Room fallback**: Simpler initially, but creates inconsistent data paths and offline edge cases
2. **In-memory cache only**: No persistence; lost on process death; poor offline support
3. **DataStore for simple caching**: Not suitable for structured product data with pagination
