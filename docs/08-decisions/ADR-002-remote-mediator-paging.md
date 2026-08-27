# ADR-002 — RemoteMediator for Paging

**Status**: ACCEPTED  
**Date**: 2026-08-27  
**Deciders**: Principal Android Architect (Agentic)

## Context

With Room as the single source of truth (ADR-001), we need a mechanism to fetch data from the network and insert it into Room while integrating with Paging 3's pagination pipeline.

## Decision

Use Paging 3's `RemoteMediator` to coordinate between the DummyJSON API and Room database.

- RemoteMediator handles `REFRESH` (initial/pull-to-refresh) and `APPEND` (next page) load types
- `PREPEND` returns `MediatorResult.Success(endOfPaginationReached = true)` (no prepending needed)
- Remote keys stored in a separate `RemoteKeyEntity` table
- On REFRESH: clear products and remote keys in a single transaction, then refetch
- On APPEND: calculate skip from remote keys, fetch next page, insert

## Consequences

**Positive**:
- Seamless integration with Paging 3 and Room PagingSource
- Automatic cache management tied to pagination lifecycle
- Clean separation: RemoteMediator handles network→Room, PagingSource handles Room→UI
- Built-in retry and error handling

**Negative**:
- More complex than simple network calls
- Remote key management adds a table and coordination logic
- Search and category filtering need separate paging strategies (may need separate RemoteMediator instances or different approach)

## Alternatives Considered

1. **Manual repository fetching**: Simpler but loses Paging 3 integration and requires manual pagination tracking
2. **Network-only PagingSource**: No offline support; violates ADR-001
3. **Pre-fetching all products**: Feasible for ~194 products but doesn't demonstrate scalable pagination
