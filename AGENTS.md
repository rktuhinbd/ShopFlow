# AGENTS.md — ShopFlow AI Agent Entry Point

> **Every AI agent must read this file first.**

## Project Identity

| Field | Value |
|-------|-------|
| **Name** | ShopFlow |
| **Type** | Android application (Kotlin, Jetpack Compose, Material 3) |
| **Package** | `com.rktuhin.shopflow` |
| **Purpose** | Offline-first product catalog browser with search, categories, and favorites |
| **API** | [DummyJSON](https://dummyjson.com/) Products API |
| **Repository** | Single-module Android project (`:app`) |

## Mandatory Startup Sequence

Before starting ANY work:

1. **Read** this file (`AGENTS.md`)
2. **Read** `docs/07-status/PROJECT_STATUS.md` — current project state
3. **Read** `docs/07-status/CURRENT_WORK.md` — what is being worked on
4. **Read** `docs/07-status/NEXT_ACTIONS.md` — what to do next
5. **Inspect** Git status (`git status`, `git log --oneline -5`)
6. **Read** the current task file in `tasks/` matching CURRENT_WORK
7. **Read** relevant ADRs in `docs/08-decisions/`
8. **Read** relevant requirements/architecture docs in `docs/`

## Authoritative Documentation Map

```
docs/
├── 00-project/
│   └── MASTER_SRS.md              ← Requirements (source of truth)
├── 01-requirements/
│   ├── PRD.md                     ← Product vision & scope
│   └── TRACEABILITY_MATRIX.md     ← Requirement → test mapping
├── 02-architecture/
│   └── SYSTEM_ARCHITECTURE.md     ← Architecture & diagrams
├── 03-data/
│   ├── API_SPECIFICATION.md       ← Verified API contract
│   ├── DATA_MODEL.md              ← Room/domain data model
│   └── ERD.md                     ← Entity-relationship diagram
├── 04-ui-ux/
│   ├── UI_UX_SPECIFICATION.md     ← Visual design system
│   ├── PRODUCT_LIST.md            ← Product list screen spec
│   ├── PRODUCT_DETAIL.md          ← Product detail screen spec
│   └── FAVORITES.md               ← Favorites screen spec
├── 05-engineering/
│   ├── TECH_STACK.md              ← Approved technologies & versions
│   ├── BUILD_AND_RELEASE.md       ← Build, 16KB, release config
│   ├── TEST_STRATEGY.md           ← Test plan & levels
│   ├── PERFORMANCE.md             ← Performance considerations
│   └── SECURITY.md                ← Security requirements
├── 06-planning/
│   ├── MASTER_IMPLEMENTATION_PLAN.md ← Master implementation plan
│   ├── ROADMAP.md                 ← Milestones & dependencies
│   └── TASK_BACKLOG.md            ← All tasks with IDs
├── 07-status/
│   ├── PROJECT_STATUS.md          ← Current state (always read)
│   ├── CURRENT_WORK.md            ← Active work handoff
│   ├── NEXT_ACTIONS.md            ← Prioritized action queue
│   ├── COMPLETED_WORK.md          ← Verified completions
│   ├── BLOCKERS.md                ← Active blockers
│   ├── RISK_REGISTER.md           ← Known risks
│   └── CHANGELOG.md               ← Project changelog
├── 08-decisions/
│   ├── ADR-INDEX.md               ← Decision index
│   └── ADR-NNN-*.md              ← Individual decisions
tasks/
├── planned/                       ← Task files (TASK-NNN-*.md)
├── in-progress/
├── done/
└── blocked/
```

## Task Workflow

```
PROPOSED → PLANNED → READY → IN_PROGRESS → IN_REVIEW → VERIFIED → DONE
                                                          ↓
                                              BLOCKED / DEFERRED / CANCELLED
```

## Development Process

```
DISCOVER → ANALYZE → PLAN → CHECK CONSISTENCY → PRESENT PLAN
    → HUMAN APPROVAL → IMPLEMENT → VERIFY → REVIEW
    → UPDATE PROJECT MEMORY → HANDOFF
```

**HARD GATE**: No implementation without explicit human approval of the plan.

## Verification Requirement

- Never claim a build passed without running it
- Never claim a test passed without running it
- Never mark DONE without acceptance criteria verified
- Never invent API fields — verify against `docs/03-data/API_SPECIFICATION.md`

## Handoff Requirement

Before stopping, update:
- [ ] Task file status
- [ ] `docs/07-status/CURRENT_WORK.md`
- [ ] `docs/07-status/PROJECT_STATUS.md`
- [ ] `docs/07-status/NEXT_ACTIONS.md`
- [ ] `docs/07-status/COMPLETED_WORK.md` (if applicable)
- [ ] ADRs (if decisions were made)

Include in handoff: what changed, what is verified, what failed, next step, verification command.

## Non-Hallucination Policy

- **UNKNOWN — NEEDS VERIFICATION**: When information is unavailable
- **TBD — REQUIRES DECISION**: When a decision has not been made
- Never convert uncertainty into confident assertion
- Never override repository state with assumptions

## Agent-Specific Instructions

| Agent | Instruction File |
|-------|-----------------|
| Antigravity | `.agents/rules/` and `.agents/workflows/` |
| Claude Code | `CLAUDE.md` |
| Cursor | `.cursor/rules/*.mdc` |
| Others | Read this file + `docs/` |
