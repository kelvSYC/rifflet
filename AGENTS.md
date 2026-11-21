# Agent Guidance

Guidance for coding agents (and humans) working in this Gradle composite build: requirements, build commands, conventions, architecture and gotchas. This file is written to be model-neutral; `CLAUDE.md` only imports it. Keep tool- or vendor-specific instructions out of it.

## Requirements

- **Tests must pass.** All code changes must pass `./gradlew :check` before a task is complete.
- **Be concise.** Avoid unnecessary explanation or narrative.
- **No unsolicited explanations.** Only explain what you're doing if explicitly asked.

## Build Commands

```bash
./gradlew :build          # Build all components
./gradlew :check          # Run tests across all components
./gradlew :publish        # Publish to GitHub Packages (requires GITHUB_ACTOR, GITHUB_TOKEN)
./gradlew dokkaGenerate   # Generate HTML API docs for all components
./gradlew consumerCompatibilityTest  # Build compat-tests/consumer on the supported floors (not part of :check)
```

Single component (from repo root):
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

## Architecture

This is a **composite build** of Kotlin Multiplatform libraries for parsing and writing IFF (Interchange File Format) and related audio formats, published to GitHub Packages.

### Build Hierarchy

The root `settings.gradle.kts` composes:

1. **`gradle/`** — Internal build infrastructure (not published):
   - `gradle/libs.versions.toml` — Pins the shared build conventions version and holds rifflet's own catalog entries (the `local` catalog; see Shared Build Conventions)
   - `gradle/settings` — Thin local settings plugin (`com.kelvsyc.internal.rifflet.settings`): applies the shared settings plugin and registers the `local` catalog in every build

   The Kotlin and Dokka convention plugins are shared; this repository has no convention plugins of its own.

2. **`components/`** — Published Kotlin Multiplatform libraries (group `com.kelvsyc.rifflet`):
   - `rifflet-core` — Core IFF/RIFF parsing and writing primitives
   - `rifflet-civ3` — Civilization III scenario and save file (`.BIC`, `.BIX`, `.BIQ`) parsing; depends on `rifflet-core`

### Shared Build Conventions

The catalog, platform BOM, semver versioning, repositories, toolchain resolver and publishing convention come from [gradle-build-conventions](https://github.com/kelvSYC/gradle-build-conventions) (group `com.kelvsyc.internal`, published to GitHub Packages). The settings plugin `com.kelvsyc.internal.settings` provides:

- the foojay toolchain resolver and the repositories (Maven Central, GitHub Packages for gradle-build-conventions and kotlin-tools, Gradle `libs-releases` for `org.gradle.experimental`)
- the `libs` version catalog (`com.kelvsyc.internal:catalog`) and the platform BOM (`libs.conventions.platform`), whose dependency versions are therefore not declared in this repository
- project versions from git tags (`com.kelvsyc.internal.semver`) and default group handling
- versionless resolution of plugin IDs in exactly the `com.kelvsyc.internal` namespace (not the longer `com.kelvsyc.internal.rifflet.*` IDs, such as the thin settings plugin, which resolve from the included `gradle/settings` build), e.g. `com.kelvsyc.internal.github-publishing`

`gradle/settings` applies it with `apply(plugin = "com.kelvsyc.internal.settings")` rather than a `plugins {}` block: generating a precompiled settings plugin's accessors applies its `plugins {}` block to a stand-in `Settings` after settings evaluation, where Gradle rejects the repositories the shared plugin declares.

The shared plugin owns the `libs` catalog, so rifflet's own entries (the kotlin-tools dependency) live in `gradle/libs.versions.toml` and are exposed as the `local` catalog (`local.kotlin.core`). Gradle itself imports `gradle/libs.versions.toml` under the default name `libs` in a build whose directory contains one (the repository root), which would clash with the shared catalog, so the root build renames that default import to `local`. Inside `gradle/settings` itself, before the shared plugin is applied, the same file is registered as `libs` to resolve the shared settings plugin.

The shared version is pinned once, as `gradle-build-conventions` in `gradle/libs.versions.toml`; the thin settings plugin depends on `com.kelvsyc.internal:settings-plugin` at that version, which in turn selects the catalog and platform versions. Dependabot bumps it: `.github/dependabot.yml` gives it the GitHub Packages registry of gradle-build-conventions (and, for the `kotlin-tools` version, a second registry for kotlin-tools), with the `DEPENDABOT_PACKAGES_TOKEN` Dependabot secret (a token with `read:packages`) as credentials. A manual bump is that one line.

A bump PR from Dependabot completes itself (`.github/workflows/conventions-bump.yml`): it moves the Gradle wrapper to the Gradle version the new release is built for (`.github/scripts/follow-conventions-gradle.sh`, reading the release's catalog), pushes that to the PR, and enables GitHub auto-merge, so the PR merges once the required checks pass. Dependabot's release-age cooldown (`cooldown` in `.github/dependabot.yml`) exempts `com.kelvsyc.internal:*` and `com.kelvsyc.kotlin:*` (our own releases), so a conventions release gets its PR on the next run. Other Dependabot PRs are left alone (kotlin-tools bumps arrive as ordinary Dependabot PRs and are not auto-merged: the workflow only acts on gradle-build-conventions bumps), and Dependabot does not propose standalone Gradle wrapper updates: the wrapper follows the conventions. The push and the auto-merge use the `CONVENTIONS_TOKEN` personal access token, stored in both the Dependabot and Actions secrets (a run started by Dependabot sees only the former, one started by the token's own push only the latter); only those two steps get it, not the Gradle build. A push or merge made with `GITHUB_TOKEN` triggers no workflows, so neither CI on the wrapper change nor the snapshot publish after the merge would run. The shared catalog's `gradle` and `kotlin` versions apply here: keep the wrapper and the Kotlin Gradle plugin in step with them.

Resolving the shared plugin requires GitHub Packages credentials even to configure the build (see Building Requires GitHub Packages Credentials). The Kotlin and Dokka convention plugins are shared too (`com.kelvsyc.internal:kotlin-plugin` and `com.kelvsyc.internal:dokka-plugin`, same version as the rest); components apply them by their `com.kelvsyc.internal.<name>` IDs, resolved versionless at the pinned version.

### Component Settings Pattern

Every component's `settings.gradle.kts` (as does the root, with the matching relative path) starts with:
```kotlin
pluginManagement {
    includeBuild("../../gradle/settings")
    // Gradle resolves the included settings plugin's external dependencies with this build's plugin repositories.
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.github.com/kelvSYC/gradle-build-conventions") {
            name = "GitHubPackagesBuildConventions"
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

plugins { id("com.kelvsyc.internal.rifflet.settings") }
```
The `repositories` block is needed because the included settings plugin depends on the shared settings plugin from GitHub Packages, and Gradle resolves those dependencies with the applying build's plugin repositories. Keep the `pluginManagement { repositories { ... } }` block byte-identical in the root and both component settings files; the `gradle/settings` build is the one that supplies the plugin and declares its own repositories instead (see its `settings.gradle.kts`). The root build uses `includeBuild("gradle/settings")` and applies the same plugin.

### Convention Plugins

Shared (from gradle-build-conventions; see its README for details):

- `com.kelvsyc.internal.kotlin-multiplatform-base` — Applies `kotlin("multiplatform")`, the Kotlin consumer-compatibility floors, Kotlin Gradle plugin version and toolchain alignment checks, platform BOM in `commonMain`, Kotest engine/assertions in `commonTest`, `commonMain` platform-import check, Dependency Analysis
- `com.kelvsyc.internal.kotlin-multiplatform-library` — Applies base; enables sources JAR for publication
- `com.kelvsyc.internal.kotlin-multiplatform-jvm` — Applies base; adds JVM target released for Java 17 and compiled with JDK 25 (see JDK Constraints), `org.gradle.jvm.version` on the JVM target, `kotest-runner-junit5` in `jvmTest`, JUnit Platform test task, a javadoc JAR on the `jvm` publication
- `com.kelvsyc.internal.kotlin-multiplatform-js` — Applies base; adds JS IR target with Node.js; raises the JS stdlib back to the compiler version. Not currently applied by any component.
- `com.kelvsyc.internal.dokka` — Dokka HTML generation with GitHub source links resolved from git HEAD and JDK/Kotlin standard library links; wires `assemble` → `dokkaGeneratePublicationHtml`. Every component target is JVM, so the javadoc JAR holds Dokka's Javadoc output; adding a non-JVM target switches it to Dokka HTML.
- `com.kelvsyc.internal.github-publishing` (shared) — Applies `maven-publish`; configures the GitHub Packages repository, deriving its URL from the `origin` git remote and taking credentials from the `gpr.user`/`gpr.key` Gradle properties, else `GITHUB_ACTOR`/`GITHUB_TOKEN`; strips the internal platform BOM (`com.kelvsyc.internal:platform`) from published POM and Gradle module metadata so that consumers don't see an unresolvable dependency. The shared Kotlin conventions still declare `implementation(platform(...))` for local resolution. Because the platform is stripped, published dependencies must carry an explicit version: catalog entries use `version.ref` (e.g. `kotlin-core` → `kotlin-tools`), and dependencies on other components use the project version (`"com.kelvsyc.rifflet:<component>:$version"`). The shared plugin's `RequireDependencyVersions` fails module metadata generation — which `check` depends on — if any published dependency is versionless. The shared plugin also applies `versionMapping { allVariants { fromResolutionResult() } }` to every Maven publication, but the Kotlin Gradle plugin generates the Kotlin Multiplatform target publications and they do not honour that mapping, so explicit versions on published dependencies are still required.

### Testing

Tests use [Kotest](https://kotest.io/) with JUnit Platform (JVM target only).

#### Consumer Compatibility Fixture

`compat-tests/consumer` is a standalone Gradle build that uses rifflet the way the oldest supported consumer would: Gradle 8.14 (its own wrapper), `kotlin("jvm")` 2.1.21, `jvmToolchain(17)`. Its smoke tests exercise the public API of each component. It catches what unit tests inside this build cannot: published metadata, bytecode level, dependency scopes and resolution.

- `./gradlew consumerCompatibilityTest` runs each component's `publishToMavenLocal`, then the fixture's `check` with the JDK 17 toolchain as `JAVA_HOME` (Gradle 8.14 cannot run on JDK 25). The fixture resolves rifflet only from Maven Local (`exclusiveContent`) at the exact version just published (`-PriffletVersion`); the root build applies the same settings plugin as the components, hence the same semver versioning, to know that version. It is not part of `:check`; CI runs it after `:build`.
- It deliberately uses `publishToMavenLocal` rather than a dedicated publishing repository: any repository in `publishing.repositories` becomes part of every component's `publish` lifecycle, and `publish` must stay the GitHub Packages release path only.
- The fixture's pinned versions are the supported floors: a floor moves with a gradle-build-conventions release (the `kelvsycKotlin` defaults), so raising a floor goes in this order: (1) on this repository's `main`, raise the fixture's pinned versions and `consumerCompatibilityJdk` in the root `build.gradle.kts` to the new floor first (a newer test consumer still consumes output built at the older floor, so this passes on its own); (2) then change the floor in gradle-build-conventions, where the required `canary (kelvSYC/rifflet)` check runs `consumerCompatibilityTest` from this repository's `main` against the release candidate and so gates it (changing the floor first fails the canary against the old pins, so it can never release); (3) the resulting conventions release reaches this repository through the usual bump PR. `compat-tests/**` is excluded from Dependabot for that reason.

### Kotlin Consumer Compatibility

The Kotlin Gradle plugin version (catalog `kotlin`) only selects the compiler. What consumers need is set by the `kelvsycKotlin` extension of the shared Kotlin conventions, whose defaults are the floors (deliberately outside the version catalog so Dependabot leaves them alone). A component can override a floor in its `build.gradle.kts`, e.g. `kelvsycKotlin { languageVersion = KotlinVersion.KOTLIN_2_3 }`; none does today:

- `languageVersion` (also the API version) = 2.2, and `coreLibrariesVersion` = 2.2.x, so the `kotlin-stdlib` declared in published metadata is 2.2.x. JVM consumers can use Kotlin 2.1+ (compilers read metadata one version ahead). 2.2 is the lowest level the current Kotlin Gradle plugin does not deprecate; the shared default is raised when a Kotlin bump deprecates it.
- JVM consumers also need Java 17+ (see JDK Constraints).
- Klibs (JS, native) carry the compiler's ABI version, so JS/native consumers need a compiler at least as new as the Kotlin Gradle plugin, whatever the language version is.
- Transitive dependencies can raise the stdlib floor regardless: `rifflet-core` exposes `kotlin-core` via `api`, so the kotlin-tools version must also publish a 2.2.x stdlib.

The applied Kotlin Gradle plugin can be silently raised above the catalog version by Gradle's embedded Kotlin (via `kotlin-dsl`) or another build plugin's stdlib requirement. The shared conventions fail the build when the applied plugin version differs from the compiler version they were released for, so a Gradle bump that raises embedded Kotlin is fixed by a gradle-build-conventions release carrying the matching `kotlin` version, then bumping the single `gradle-build-conventions` pin (the conventions-bump workflow moves the wrapper with it), not by a local catalog edit. They also fail when any other `org.jetbrains.kotlin` artifact on the build script classpath differs from the plugin (`checkKotlinToolchainAlignment`): a build plugin depending on a newer `kotlin-bom` can raise e.g. `kotlin-daemon-client` alone, which surfaces as "Could not connect to Kotlin compile daemon" followed by `OutOfMemoryError: Metaspace`.

### Versioning

Driven by git tags via the shared semver settings plugin (`com.kelvsyc.internal.semver`), applied through the shared settings plugin. Past the latest tag the version is an implicit `-SNAPSHOT` of the next version; uncommitted or untracked changes append `+dirty`. Snapshot versions are published automatically on every push to `main`.

### JDK Constraints

Published JVM artifacts require Java 17, and the components are compiled with JDK 25. The shared Kotlin conventions set both through `kelvsycKotlin` (`jvmRelease` = 17, `jvmToolchain` = 25; deliberately outside the version catalog), and the `kotlin-multiplatform-jvm` convention plugin applies them:

- Compiles with `jvmTarget = 17` and `-Xjdk-release=17`, so only the Java 17 API is available. Lowering `jvmTarget` alone is not enough: against a JDK 21+ class library, `MutableList.removeFirst()` compiles to `java.util.List.removeFirst`, which fails with `NoSuchMethodError` on Java 17.
- Sets `options.release = 17` on Java compilation.
- Runs tests on JDK 17 (provisioned automatically via the foojay resolver; CI installs it alongside JDK 25).
- Sets `org.gradle.jvm.version = 17` on the JVM target. The Kotlin Gradle plugin never sets it on Kotlin Multiplatform JVM variants; without it, Gradle consumers on an older JVM resolve the components and fail at runtime with `UnsupportedClassVersionError`, and our own JVM classpaths would accept dependencies requiring a newer JVM.

17 matches the Java release kotlin-tools supports; `kotlin-core` is on every consumer's runtime classpath, so going lower requires kotlin-tools to go lower first.

### Configuration Cache

Enabled via `org.gradle.configuration-cache=true` in `gradle.properties`.

## Critical Gotchas

### Publishing Requires Credentials ⚠️

`./gradlew :publish` requires credentials: the `gpr.user`/`gpr.key` Gradle properties, else `GITHUB_ACTOR` and `GITHUB_TOKEN`. Without them, it fails. The publish repository URL is derived from the `origin` git remote, so `origin` must point at the GitHub repository.

### Building Requires GitHub Packages Credentials ⚠️

The shared build conventions (and kotlin-tools) are resolved from GitHub Packages when the build is configured, so every Gradle invocation needs credentials with `read:packages`: the `gpr.user`/`gpr.key` Gradle properties (e.g. in `~/.gradle/gradle.properties`), else the `GITHUB_ACTOR`/`GITHUB_TOKEN` environment variables. CI jobs that run Gradle need `packages: read` permission and both variables set. The consumer compatibility fixture needs the same credentials for kotlin-tools.

### Component Settings Pattern ⚠️

Do not modify the `pluginManagement`/`plugins` blocks in each settings file (see Component Settings Pattern above) — they wire up the composite build. What must stay identical is the GitHub Packages `repositories` block inside `pluginManagement` in the root and component settings files. What differs by design: the `includeBuild` paths and the `gradle/settings` build's own settings (it supplies the plugin and declares its own repositories).

## Quick Navigation

- **Architecture & build hierarchy**: see Architecture above
- **Shared conventions pin and local catalog**: `gradle/libs.versions.toml` (the shared catalog itself comes from gradle-build-conventions)
- **Convention plugins**: shared, in gradle-build-conventions (`kotlin-plugin/`, `dokka-plugin/`)
- **Source code**: `components/*/src/commonMain/kotlin/`
- **Tests**: `components/*/src/commonTest/kotlin/`
