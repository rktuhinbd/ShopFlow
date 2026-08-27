# Workflow: Implement Task

## Trigger
After starting a task (see `start-task.md`).

## Steps
1. Confirm task acceptance criteria are understood
2. Implement changes per the task specification
3. Follow architecture rules (see `.agents/rules/architecture.md`)
4. Follow Android engineering standards (see `.agents/rules/android.md`)
5. Commit incrementally with descriptive messages: `type(scope): description`
6. Keep `CURRENT_WORK.md` updated with progress
7. If architecture changes are needed, create an ADR FIRST
8. If blocked, update `docs/07-status/BLOCKERS.md` and task file
