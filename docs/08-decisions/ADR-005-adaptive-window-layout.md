# ADR-005 — Adaptive Window-Size-Based Layout

**Status**: ACCEPTED  
**Date**: 2026-08-27  
**Deciders**: Principal Android Architect (Agentic)

## Context

The app must work on phones, tablets, foldables, and in multi-window mode. Layout decisions can be based on device type heuristics or window size classes.

## Decision

Use Material 3 window size classes (Compact, Medium, Expanded) to drive layout decisions. Do not use device-type heuristics like `isTablet`.

## Consequences

**Positive**: Responds correctly to window resizing, multi-window, foldables; device-agnostic; follows Material 3 guidelines.

**Negative**: Requires testing across multiple window sizes; list-detail pattern adds UI complexity.

## Alternatives Considered

1. **Device-type heuristics (`isTablet`)**: Fragile; doesn't handle multi-window or foldables
2. **Fixed phone-only layout**: Simpler but poor tablet/foldable experience
