<!--
SYNC IMPACT REPORT
==================
Version change: 0.0.0 → 1.0.0 (MAJOR - initial constitution ratification)

Modified principles: N/A (initial creation)

Added sections:
- Core Principles (3 principles: Simplicity & YAGNI, Test-Driven Development, Compose-First UI)
- Technology Standards
- Development Workflow
- Governance

Removed sections: N/A (initial creation)

Templates requiring updates:
- .specify/templates/plan-template.md: ✅ Compatible (Constitution Check section exists)
- .specify/templates/spec-template.md: ✅ Compatible (User stories with priorities align with TDD)
- .specify/templates/tasks-template.md: ✅ Compatible (Test-first structure supported)

Follow-up TODOs: None
-->

# MealPlanner Constitution

## Core Principles

### I. Simplicity & YAGNI

Every implementation decision MUST favor the simplest solution that meets current requirements.

**Non-negotiable rules:**
- MUST NOT add features, abstractions, or configurability beyond explicit requirements
- MUST NOT create helpers or utilities for one-time operations
- MUST delete unused code completely—no backwards-compatibility shims or commented remnants
- MUST reject speculative design for hypothetical future needs
- Three similar lines of code is preferred over a premature abstraction

**Rationale:** Complexity is the enemy of maintainability. Every abstraction layer adds cognitive load and potential failure points. Build only what is needed today.

### II. Test-Driven Development

Tests MUST be written and verified to fail before implementation begins.

**Non-negotiable rules:**
- MUST follow Red-Green-Refactor cycle: write failing test → implement → refactor
- MUST have failing tests approved before writing production code for non-trivial features
- MUST NOT skip test verification step—tests that never failed are suspect
- Unit tests for business logic; instrumented tests for UI interactions
- Test file naming: `*Test.kt` for unit tests, `*InstrumentedTest.kt` for Android tests

**Rationale:** TDD ensures code is testable by design, prevents scope creep during implementation, and creates living documentation of expected behavior.

### III. Compose-First UI

All UI MUST be implemented using Jetpack Compose with Material 3 design principles.

**Non-negotiable rules:**
- MUST use Compose for all new screens and UI components—no XML layouts
- MUST follow unidirectional data flow: State flows down, events flow up
- MUST use preview annotations (`@Preview`) for all composable functions
- MUST extract reusable UI patterns into the `ui/theme/` or `ui/components/` packages
- SHOULD prefer stateless composables with state hoisting

**Rationale:** Compose provides declarative, testable UI with less boilerplate. Consistency in UI approach reduces cognitive switching and enables component reuse.

## Technology Standards

**Language**: Kotlin (targeting JVM 11)
**UI Framework**: Jetpack Compose with Material 3
**Minimum SDK**: 24 (Android 7.0)
**Target SDK**: 36
**Build System**: Gradle with Kotlin DSL
**Testing**: JUnit 4 for unit tests, Espresso + Compose Testing for instrumented tests

**Dependency policy:**
- Prefer AndroidX libraries over third-party alternatives when equivalent
- New dependencies MUST be justified in PR description
- Version catalogs (`libs.versions.toml`) MUST be used for dependency management

## Development Workflow

**Code organization:**
- Feature code in `com.example.mealplanner.<feature>` packages
- Shared UI in `com.example.mealplanner.ui.theme` and `ui.components`
- Data layer in `com.example.mealplanner.data`
- Domain logic in `com.example.mealplanner.domain`

**Quality gates:**
- All tests MUST pass before merge
- Compose previews MUST render without errors
- No compiler warnings in production code

**Review requirements:**
- PRs MUST verify compliance with constitution principles
- Complexity additions MUST include written justification

## Governance

This constitution supersedes all other practices and conventions. When conflicts arise between this document and external guidance, this constitution prevails.

**Amendment process:**
1. Propose change with rationale in a dedicated PR
2. Update constitution version following semantic versioning:
   - MAJOR: Principle removal or fundamental redefinition
   - MINOR: New principle or section added
   - PATCH: Clarifications and typo fixes
3. Update dependent templates if principles change
4. Document migration plan for existing code if needed

**Compliance:**
- All PRs MUST be reviewed against constitution principles
- Violations require explicit justification and approval
- Repeated violations indicate constitution may need revision

**Version**: 1.0.0 | **Ratified**: 2026-01-11 | **Last Amended**: 2026-01-11