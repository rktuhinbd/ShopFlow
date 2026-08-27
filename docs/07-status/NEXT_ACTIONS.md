# ShopFlow — Next Actions

**Last Updated**: 2026-08-27T17:35:00+06:00

---

## Immediate Next Action

1. **TASK-200**: Create API response DTOs (Priority: P0)
   - Define data classes for DummyJSON API responses using `kotlinx.serialization`.

Plan status: **APPROVED**

## Next 3 Actions

1. **TASK-201**: Create Retrofit API service interface (Priority: P0)
   - Define the Retrofit interface.
2. **TASK-202**: Create OkHttp client configuration (Priority: P0)
   - Configure OkHttp with logging.
3. **TASK-203**: Create Hilt network module (Priority: P0)
   - Provide Retrofit and OkHttp dependencies via Hilt.

## Future Actions

- M3: Implement Room (entities, DAOs, database, TypeConverters)
- M4: Implement Paging + RemoteMediator pipeline
- M5: Implement repositories and domain models
- M6: Implement MVVM / State Management

## Blocked Actions

None currently.
