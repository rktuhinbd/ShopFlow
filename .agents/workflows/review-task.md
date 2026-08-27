# Workflow: Review Task

## Trigger
After verification passes.

## Steps
1. Review code changes against architecture rules and ADRs
2. Check for regressions in existing functionality
3. Verify no hallucinated API fields or invented behaviors
4. Confirm all acceptance criteria are objectively met
5. Update task status to `VERIFIED` if review passes
6. Update `COMPLETED_WORK.md` with verification evidence
