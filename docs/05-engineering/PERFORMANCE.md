# ShopFlow — Performance Considerations

**Version**: 1.0-DRAFT  
**Date**: 2026-08-27  
**Status**: DRAFT — PENDING HUMAN APPROVAL

---

## 1. Risk Areas

| Area | Risk | Mitigation |
|------|------|------------|
| **Startup** | Cold start >2s | Minimize DI graph; defer heavy init; consider Baseline Profile |
| **Compose recomposition** | Excessive recomposition in lists | Use stable types; `key()` in LazyColumn; avoid unstable lambdas |
| **Lazy lists** | Jank during scroll | Use `contentType`; avoid heavy composition in items; Coil memory cache |
| **Image loading** | Slow image load, OOM | Coil with memory+disk cache; appropriate image sizing; thumbnails in list |
| **Room** | Slow queries on large tables | Indexes on `category`, `title`; avoid main thread queries |
| **Paging** | Over-fetching or stale data | Appropriate page size; RemoteMediator with proper invalidation |
| **Search** | Too many API calls | Debounce 300ms; distinctUntilChanged; cancel previous |
| **Network** | Slow API responses | Timeouts; offline fallback; loading states |
| **Large screens** | More content visible = more recomposition | Efficient list-detail; lazy loading |
| **R8** | Larger APK without shrinking | Enable R8 for release; proper keep rules |

## 2. Measurements (Post-Implementation)

All measurements are UNKNOWN — NEEDS VERIFICATION until implementation.

| Metric | Target | Actual |
|--------|--------|--------|
| Cold start (mid-range) | <2s | UNKNOWN |
| List scroll FPS | 60fps | UNKNOWN |
| APK size (release) | <15MB | UNKNOWN |
| Memory peak | <150MB | UNKNOWN |
| Search latency (debounce→result) | <500ms | UNKNOWN |

## 3. Optimization Strategy

**Do not prematurely optimize.** Measure first, optimize where evidence shows a problem.

Priority order:
1. Correctness first
2. Measure with profiler
3. Fix obvious issues (main thread work, unnecessary recomposition)
4. Baseline Profile if startup is too slow
5. R8 for APK size

---

**Document Status**: DRAFT — Awaiting human review and approval.
