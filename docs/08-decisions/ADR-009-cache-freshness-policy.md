# ADR-009 — Cache Freshness Policy

**Status**: ACCEPTED  
**Date**: 2026-08-27  
**Deciders**: Principal Android Architect (Agentic)

## Context
When loading products from Room (offline-first approach), we need to determine when the cached data is considered "stale" and needs synchronization with the remote API.

## Decision
Adopt a **15-minute freshness window**. 
- **Fresh cached data**: Displayed immediately. No background refresh triggered automatically.
- **Stale cached data (>15 minutes old)**: Displayed immediately. A background network refresh/synchronization is triggered automatically. Successful responses overwrite Room, and the UI updates automatically via `Flow`.
- **Network unavailable**: Cached data remains usable indefinitely. The app will never delete stale cached content just because the network is unavailable.
- **Data Availability vs. Freshness vs. Synchronization**: Availability is guaranteed by Room. Freshness is tracked via timestamps in Room. Synchronization is coordinated by `RemoteMediator` in response to freshness state.

## Consequences
**Positive**:
- Users always see data immediately if it exists (no loading spinners blocking UI).
- Data stays reasonably up-to-date.
- Resilient to network flakes.

**Negative**:
- Users might briefly see stale data (e.g., old prices) before the background refresh updates the UI.
