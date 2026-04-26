# CLAUDE.md

Architectural reference for this Gradle composite build. See `AGENTS.md` for build commands and quick-reference gotchas.

## Requirements

- **Tests must pass** before any task is complete: `./gradlew :check`.
- No detekt is configured yet.

## Build Commands

```bash
./gradlew :build          # Build all components
./gradlew :check          # Run tests across all components
./gradlew :publish        # Publish to GitHub Packages (requires GITHUB_ACTOR, GITHUB_TOKEN)
./gradlew dokkaGenerate   # Generate HTML API docs for all components
```

Single component (from repo root):
```bash
./gradlew :rifflet-core:build
./gradlew :rifflet-core:allTests
```

## Architecture

This is a **composite build** of Kotlin Multiplatform libraries for parsing and writing IFF (Interchange File Format) and related audio formats, published to GitHub Packages.

### Build Hierarchy

The root `settings.gradle.kts` composes:

1. **`gradle/`** — Internal build infrastructure (not published):
   - `gradle/platform` — BOM centralizing all dependency versions
   - `gradle/settings` — Settings plugin (`com.kelvsyc.internal.rifflet.settings`) wiring platform/catalog and semver into every component build
   - `gradle/plugins/dokka-convention` — Convention plugin: `dokka` (Dokka HTML docs, GitHub source links resolved from git HEAD)
   - `gradle/plugins/kotlin-convention` — Convention plugins: `kotlin-multiplatform-base`, `kotlin-multiplatform-library`, `kotlin-multiplatform-jvm`
   - `gradle/plugins/publishing-convention` — Convention plugin: `github-publishing` (Maven publication to GitHub Packages)

2. **`components/`** — Published Kotlin Multiplatform libraries (group `com.kelvsyc.rifflet`):
   - `rifflet-core` — Core IFF/RIFF parsing and writing primitives

### Component Settings Pattern

Every component's `settings.gradle.kts`:
```kotlin
pluginManagement { includeBuild("../../gradle/settings") }
plugins { id("com.kelvsyc.internal.rifflet.settings") }
```

### Convention Plugins

- `kotlin-multiplatform-base` — Applies `kotlin("multiplatform")`, Kotlin 2.3 compiler options, platform BOM in `commonMain`, Kotest engine/assertions in `commonTest`, `commonMain` platform-import check
- `kotlin-multiplatform-library` — Applies base; enables sources JAR for publication
- `kotlin-multiplatform-jvm` — Applies base; adds JVM target, JDK 25 toolchain, `kotest-runner-junit5` in `jvmTest`, JUnit Platform test task
- `dokka` — Configures Dokka HTML generation with GitHub source links resolved from git HEAD; wires `assemble` → `dokkaGeneratePublicationHtml`
- `github-publishing` — Applies `maven-publish`; configures GitHub Packages repository using `GITHUB_ACTOR`/`GITHUB_TOKEN`

### Testing

Tests use [Kotest](https://kotest.io/) with JUnit Platform (JVM target only).

### Versioning

Driven by git tags via `com.javiersc.semver`, applied through the settings plugin. Snapshot versions are published automatically on every push to `main`.

### JDK Constraints

The `kotlin-multiplatform-jvm` convention plugin pins the JVM toolchain to JDK 25.

### Configuration Cache

Enabled via `org.gradle.configuration-cache=true` in `gradle.properties`.
