# ADR-007 — Search/Category Interaction

**Status**: ACCEPTED  
**Date**: 2026-08-27  
**Deciders**: Principal Android Architect (Agentic)

## Context
The user can browse products via a general catalog, by category, or by a search query. We need to decide how search and category filtering interact (e.g., if a user selects "Smartphones" and searches for "Samsung", does it search only in smartphones, or globally?).

## Decision
Search and Category filtering will be **mutually exclusive source modes**.
- Selecting a category clears any active search query.
- Starting a search clears any selected category filter.
- There is only one deterministic paging stream active at a time (All, Category, or Search).

## Consequences
**Positive**:
- Significantly simplifies the `PagingSource` and `RemoteMediator` logic.
- Avoids ambiguous edge cases (e.g., the API might not support searching within a category directly, requiring client-side filtering of paged data which is problematic).
- Clear and predictable user experience.

**Negative**:
- Users cannot search within a specific category. (Acceptable for MVP).

## Alternatives Considered
1. **Composable (Search within Category)**: Requires either the API to support this specific endpoint (not guaranteed in DummyJSON) or fetching all category items and filtering locally, breaking pagination.
