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
   - `gradle/plugins/kotlin-convention` — Convention plugins: `kotlin-multiplatform-base`, `kotlin-multiplatform-library`, `kotlin-multiplatform-jvm`, `kotlin-multiplatform-js`
   - `gradle/plugins/publishing-convention` — Convention plugin: `github-publishing` (Maven publication to GitHub Packages)

2. **`components/`** — Published Kotlin Multiplatform libraries (group `com.kelvsyc.rifflet`):
   - `rifflet-core` — Core IFF/RIFF parsing and writing primitives
   - `rifflet-civ3` — Civilization III scenario and save file (`.BIC`, `.BIX`, `.BIQ`) parsing; depends on `rifflet-core`

### Component Settings Pattern

Every component's `settings.gradle.kts`:
```kotlin
pluginManagement { includeBuild("../../gradle/settings") }
plugins { id("com.kelvsyc.internal.rifflet.settings") }
```

### Convention Plugins

- `kotlin-multiplatform-base` — Applies `kotlin("multiplatform")`, Kotlin consumer-compatibility settings (see below), Kotlin Gradle plugin version and toolchain alignment checks, platform BOM in `commonMain`, Kotest engine/assertions in `commonTest`, `commonMain` platform-import check
- `kotlin-multiplatform-library` — Applies base; enables sources JAR for publication
- `kotlin-multiplatform-jvm` — Applies base; adds JVM target, JDK 25 toolchain, `kotest-runner-junit5` in `jvmTest`, JUnit Platform test task
- `kotlin-multiplatform-js` — Applies base; adds JS IR target with Node.js; raises the JS stdlib back to the compiler version (Kotlin/JS rejects an older stdlib klib ABI). Not currently applied by any component.
- `dokka` — Configures Dokka HTML generation with GitHub source links resolved from git HEAD; wires `assemble` → `dokkaGeneratePublicationHtml`
- `github-publishing` — Applies `maven-publish`; configures GitHub Packages repository using `GITHUB_ACTOR`/`GITHUB_TOKEN`; strips the internal platform BOM (`com.kelvsyc.internal.rifflet:platform`) from published POM and Gradle module metadata so that consumers don't see an unresolvable dependency. The convention plugins still declare `implementation(platform(...))` for local resolution — the stripping happens at publication time via `pom.withXml` and a `GenerateModuleMetadata` post-processing action (`StripInternalPlatform`). Because the platform is stripped, published dependencies must carry an explicit version: catalog entries use `version.ref` (e.g. `kotlin-core` → `kotlin-tools`), and dependencies on other components use the project version (`"com.kelvsyc.rifflet:<component>:$version"`). `RequireDependencyVersions` fails module metadata generation — which `check` depends on — if any published dependency is versionless. (`versionMapping { fromResolutionResult() }` would not help: Kotlin Multiplatform publications ignore it.)

### Testing

Tests use [Kotest](https://kotest.io/) with JUnit Platform (JVM target only).

### Kotlin Consumer Compatibility

The Kotlin Gradle plugin version (catalog `kotlin`) only selects the compiler. What consumers need is set by `KotlinCompatibility` in `gradle/plugins/kotlin-convention` (deliberately outside the version catalog so Dependabot leaves it alone):

- `apiVersion`/`languageVersion` = 2.2, and `coreLibrariesVersion` = 2.2.x, so the `kotlin-stdlib` declared in published metadata is 2.2.x. JVM consumers can use Kotlin 2.1+ (compilers read metadata one version ahead). 2.2 is the lowest level the current Kotlin Gradle plugin does not deprecate; raise it when a Kotlin bump deprecates it.
- Klibs (JS, native) carry the compiler's ABI version, so JS/native consumers need a compiler at least as new as the Kotlin Gradle plugin, whatever the language version is.
- Transitive dependencies can raise the stdlib floor regardless: `rifflet-core` exposes `kotlin-core` via `api`, so the kotlin-tools version must also publish a 2.2.x stdlib.

The applied Kotlin Gradle plugin can be silently raised above the catalog version by Gradle's embedded Kotlin (via `kotlin-dsl`) or another build plugin's stdlib requirement. The convention plugins fail the build when the applied plugin version differs from the catalog `kotlin` version (`checkKotlinPluginVersion`), so a Gradle bump that raises embedded Kotlin requires bumping the catalog in the same change. They also fail when any other `org.jetbrains.kotlin` artifact on the build script classpath differs from the plugin version (`checkKotlinToolchainAlignment`): a build plugin depending on a newer `kotlin-bom` can raise e.g. `kotlin-daemon-client` alone, which surfaces as "Could not connect to Kotlin compile daemon" followed by `OutOfMemoryError: Metaspace`.

### Versioning

Driven by git tags via `com.javiersc.semver`, applied through the settings plugin. Snapshot versions are published automatically on every push to `main`.

### JDK Constraints

The `kotlin-multiplatform-jvm` convention plugin pins the JVM toolchain to JDK 25.

### Configuration Cache

Enabled via `org.gradle.configuration-cache=true` in `gradle.properties`.
