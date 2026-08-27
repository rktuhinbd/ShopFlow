# Workflow: Verify Task

## Trigger
After implementation is believed complete.

## Steps
1. Run the build: `./gradlew assembleDebug`
2. Run unit tests: `./gradlew test`
3. Run instrumented tests: `./gradlew connectedAndroidTest` (if device available)
4. Verify against task acceptance criteria — check each criterion
5. Record verification results in the task file
6. If verification fails, fix and re-verify
7. Update task status to `IN_REVIEW` if all criteria pass
