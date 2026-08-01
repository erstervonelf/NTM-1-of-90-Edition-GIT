# Contributing to NTM 1-of-90 Edition

## Scope expectations

This fork accepts focused, production-ready contributions that improve fork content, tooling, stability, and maintainability.

- Keep PRs small and purpose-driven.
- Avoid unrelated refactors.
- Do not introduce new dependencies unless strictly required.
- Prefer extending `com.oneof90` for fork-exclusive work.

## Required validation

Before opening or updating a PR:

1. Build locally with `./gradlew build`
2. Verify there are no obvious compile/runtime regressions
3. For compatibility changes, validate with and without the target mod when possible

## Contribution categories

Preferred:

- Bug fixes
- Compatibility fixes
- Performance improvements
- Documentation and process improvements
- Fork-scoped gameplay/content additions

## Pull request requirements

- Use the PR template.
- Describe scope, motivation, and impact clearly.
- Link related issues.
- Include testing notes and affected environments.

## Communication

For larger additions, open a feature request first so design and scope can be aligned before implementation.

## Non-goals

- Drive-by mass formatting changes
- Broad architecture rewrites without prior agreement
- Half-finished features
