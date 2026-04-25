# CLAUDE.md

Architectural reference for this Gradle composite build. See `AGENTS.md` for build commands and quick-reference gotchas.

## Requirements

- **Tests must pass** before any task is complete: `./gradlew :test`.
- No detekt is configured yet.

## Build Commands

```bash
./gradlew :build          # Build all components
./gradlew :test           # Run tests across all components
./gradlew :publish        # Publish to GitHub Packages (requires GITHUB_ACTOR, GITHUB_TOKEN)
```

Single component (from repo root):
```bash
./gradlew :rifflet-core:build
./gradlew :rifflet-core:test
```

## Architecture

This is a **composite build** of Kotlin Multiplatform libraries for parsing and writing IFF (Interchange File Format) and related audio formats, published to GitHub Packages.

### Build Hierarchy

The root `settings.gradle.kts` composes:

1. **`gradle/`** — Internal build infrastructure (not published):
   - `gradle/platform` — BOM centralizing all dependency versions
   - `gradle/settings` — Settings plugin (`com.kelvsyc.internal.rifflet.settings`) wiring platform/catalog into every component build
   - `gradle/plugins/kotlin-convention` — Convention plugins: `kotlin-multiplatform-jvm-base`, `kotlin-multiplatform-jvm-library`

2. **`components/`** — Published Kotlin Multiplatform libraries (group `com.kelvsyc`):
   - `rifflet-core` — Core IFF/RIFF parsing and writing primitives

### Component Settings Pattern

Every component's `settings.gradle.kts`:
```kotlin
pluginManagement { includeBuild("../../gradle/settings") }
plugins { id("com.kelvsyc.internal.rifflet.settings") }
```

### Convention Plugins

- `kotlin-multiplatform-jvm-base` — Applies `kotlin("multiplatform")` with JVM target
- `kotlin-multiplatform-jvm-library` — Extends base: sets toolchain to JDK 21, Kotlin 2.2, wires platform BOM and Kotest test dependencies

### Testing

Tests use [Kotest](https://kotest.io/) with JUnit Platform.

### JDK Constraints

Convention plugins pin the toolchain to JDK 21 (`jvmToolchain(21)`).
