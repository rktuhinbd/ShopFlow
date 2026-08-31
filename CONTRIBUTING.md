# Contributing to ShopFlow

Thank you for your interest in contributing to ShopFlow! To ensure a smooth process and a high-quality codebase, please follow these guidelines.

## Branching Strategy

- `master`: The primary, stable branch.
- Feature branches: Create a branch for your work following the pattern `feature/TASK-ID-short-description` or `bugfix/TASK-ID-short-description`.

## Development Workflow

1. Ensure there is an open Issue or Task for the work you intend to do.
2. Branch off `master`.
3. Adhere strictly to the [Engineering Standards](docs/development/ENGINEERING_STANDARDS.md).
4. Write tests for your changes.
5. Ensure the project builds successfully and all tests pass locally (`.\gradlew build testDebugUnitTest`).
6. Push your branch and open a Pull Request.

## Pull Request Expectations

- **Use the PR Template**: Fill out the provided Pull Request template completely.
- **Scope**: Keep PRs focused on a single logical change. Do not bundle refactoring with new feature development.
- **UI Changes**: If your PR affects the UI, include "Before" and "After" screenshots or recordings.
- **Offline Behavior**: If modifying data loading, explain how it impacts the offline experience.
- **Review**: At least one approval from a core maintainer is required before merging.

## Documentation

- If you add a new feature or change architectural behavior, update the relevant files in the `docs/` directory.
- Update the `CHANGELOG.md` if your change is user-facing or significant.

By contributing, you agree that your contributions will be licensed under the project's chosen license.
