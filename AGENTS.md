# AGENTS.md

Critical gotchas and quick reference for automated agents working in this Gradle composite build. For comprehensive architecture, see `CLAUDE.md`.

## Requirements for All Agents

- **Tests must pass.** All code changes must pass `./gradlew :check` before completing a task.
- **Be concise.** Avoid unnecessary explanation or narrative.
- **No unsolicited explanations.** Only explain what you're doing if explicitly asked.

## Build Commands

Root commands (aggregate across all components):
```bash
./gradlew :build          # Full build
./gradlew :check          # Tests only
./gradlew :publish        # Publish to GitHub Packages
```

Single component (included build form, from repository root):
```bash
./gradlew :rifflet-core:build
./gradlew :rifflet-core:allTests
```

## Kotlin Style Guidelines

No detekt is configured yet, so these are not enforced by the build. Follow them as conventions:

- No wildcard imports (only `java.util.*` is allowed).
- Do not catch `Exception`, `RuntimeException`, `Error`, `Throwable`, `NullPointerException`, `IndexOutOfBoundsException`, or `IllegalMonitorStateException`.
- Do not throw `Exception`, `RuntimeException`, `Error`, or `Throwable`.
- No `TODO:`, `FIXME:`, or `STOPSHIP:` markers.
- No unexplained numeric literals in non-test, non-`.kts` source; extract to named constants.
- Remove unused private declarations.

## Critical Gotchas

### Publishing Requires Env Vars ⚠️

`./gradlew :publish` requires `GITHUB_ACTOR` and `GITHUB_TOKEN`. Without them, it fails.

### Component Settings Pattern ⚠️

Every component's `settings.gradle.kts` must include:
```kotlin
pluginManagement { includeBuild("../../gradle/settings") }
plugins { id("com.kelvsyc.internal.rifflet.settings") }
```

Do not modify these — they wire up the composite build correctly.

## Quick Navigation

- **Architecture & build hierarchy**: See `CLAUDE.md`
- **Version catalog**: `gradle/libs.versions.toml`
- **Convention plugins**: `gradle/plugins/*/src/main/kotlin/`
- **Source code**: `components/*/src/commonMain/kotlin/`
- **Tests**: `components/*/src/commonTest/kotlin/`
