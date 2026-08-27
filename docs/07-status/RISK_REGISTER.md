# ShopFlow — Risk Register

**Version**: 1.0-DRAFT  
**Date**: 2026-08-27

---

## Risks

| ID | Category | Risk | Probability | Impact | Mitigation | Status |
|----|----------|------|-------------|--------|------------|--------|
| RISK-001 | API | DummyJSON API becomes unavailable or changes schema | Low | High | API contract documented and verified; tests use fakes | OPEN |
| RISK-002 | Architecture | RemoteMediator complexity for search/category paging | Medium | Medium | May use simpler network-only PagingSource for search; document decision in ADR | OPEN |
| RISK-003 | Data | Brand field nullable in some products | Low | Low | Entity allows nullable brand; UI handles gracefully | MITIGATED |
| RISK-004 | Offline | Stale cache threshold not defined | Medium | Low | TBD — REQUIRES DECISION; default to manual refresh | OPEN |
| RISK-005 | UI | Compose recomposition performance in product list | Low | Medium | Use stable types, contentType, key(); measure before optimizing | OPEN |
| RISK-006 | 16KB | Dependencies may include unaligned native .so | Low | High | Verify with zipalign after release build; most deps are pure JVM | OPEN |
| RISK-007 | Tooling | AGP 9.3.2 / Kotlin 2.2.10 compatibility with all libraries | Low | High | Verify versions before adding; use latest stable releases | OPEN |
| RISK-008 | Testing | Paging 3 testing complexity | Medium | Medium | Use Paging testing library; test RemoteMediator in isolation | OPEN |
| RISK-009 | Adaptive | List-detail layout complexity | Medium | Low | Implement after core features work; use Material 3 Adaptive library | OPEN |
| RISK-010 | AI Agent | Context loss between agent sessions | Medium | Medium | Repository contains all project state; handoff workflow enforced | MITIGATED |
| RISK-011 | Dependency | JSON serialization library choice not finalized | Low | Low | Both kotlinx.serialization and Gson are viable; decide in M1 | OPEN |
| RISK-012 | Scope | Search/category interaction model undefined (FR-304) | Medium | Medium | Decided mutually exclusive (ADR-007) | MITIGATED |
| RISK-013 | Data | Remote key corruption / concurrent refresh | Medium | High | Use Room transactions for all Mediator inserts/clears | OPEN |
| RISK-014 | Concurrency | Coroutine cancellation during navigation | Medium | Medium | Use `viewModelScope` and lifecycle-aware collection (`collectAsStateWithLifecycle`) | OPEN |
| RISK-015 | UI | Image loading performance and OOM | High | Medium | Use Coil with memory/disk caching and downsampling for list thumbnails | OPEN |
| RISK-016 | AI Agent | AI agent divergence and documentation drift | High | High | STRICT enforcement of DISCOVER→PLAN→IMPLEMENT workflow and documentation updates | OPEN |

---

**Document Status**: DRAFT — Awaiting human review.
