# Rifflet

Kotlin Multiplatform library for parsing and writing IFF (Interchange File Format) binary files and derivatives, and for parsing T3 VM image files.

## Features

- Parse and write standard IFF containers (FORM, LIST, CAT, PROP)
- RIFF support (little-endian IFF variant used by WAV, AVI, etc.)
- RIFX support (big-endian RIFF variant)
- Chunk-level parsing and encoding API
- T3 VM image file parsing (bytecode pools, object definitions, constant pools, resource metadata, debug symbols)
- Built on [Okio](https://square.github.io/okio/) for efficient I/O across platforms

## Modules

| Module | Coordinates | Description |
|---|---|---|
| `rifflet-core` | `com.kelvsyc.rifflet:rifflet-core` | Core IFF/RIFF/RIFX parsing and writing primitives; T3 VM image file parsing |
| `rifflet-civ3` | `com.kelvsyc.rifflet:rifflet-civ3` | Civilization III scenario/save file (`.BIC`/`.BIX`/`.BIQ`) parsing, plus editor-behavior validation |

## Platform Support

Rifflet targets JVM (JDK 25) via Kotlin Multiplatform.

## Installation

Rifflet is published to [GitHub Packages](https://github.com/kelvSYC/rifflet/packages). GitHub Packages requires authentication even for public packages.

### 1. Create a Personal Access Token

Generate a [classic GitHub PAT](https://github.com/settings/tokens/new) with at least the `read:packages` scope. Fine-grained tokens do not support GitHub Packages.

### 2. Configure Credentials

Add the following to `~/.gradle/gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_PAT
```

### 3. Add the Repository

In your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.github.com/kelvSYC/rifflet") {
            name = "GitHubPackages"
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

### 4. Add the Dependency

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.kelvsyc.rifflet:rifflet-core:$version")
}
```

## Contributing

### Prerequisites

- JDK 25 (automatically downloaded via [Foojay Toolchains](https://github.com/gradle/foojay-toolchains))
- A classic GitHub PAT with `read:packages` scope (fine-grained tokens do not support GitHub Packages)

### Credentials Setup

Rifflet depends on packages from [kotlin-tools](https://github.com/kelvSYC/kotlin-tools), which is also hosted on GitHub Packages. To build locally, add the following to `~/.gradle/gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_PAT
```

This is also required for IntelliJ IDEA to sync and resolve dependencies correctly.

For CI environments, set the `GITHUB_ACTOR` and `GITHUB_TOKEN` environment variables instead.

### Build Commands

```bash
./gradlew :build          # Build all components
./gradlew :check          # Run all tests
./gradlew :publish        # Publish to GitHub Packages
./gradlew dokkaGenerate   # Generate API documentation
```

Single component:

```bash
./gradlew :rifflet-core:build
./gradlew :rifflet-core:allTests
```

### Project Structure

This is a Gradle composite build with the following layout:

```
rifflet/
  gradle/             # Internal build infrastructure (not published)
    platform/         # BOM centralizing dependency versions
    settings/         # Settings plugin shared by all components
    plugins/          # Convention plugins (Kotlin, Dokka, publishing)
  components/         # Published libraries
    rifflet-core/     # Core IFF/RIFF/RIFX primitives
    rifflet-civ3/     # Civilization III scenario/save file parsing and validation
```

### Testing

Tests use [Kotest](https://kotest.io/) with JUnit Platform. Run `./gradlew :check` to execute all tests.

### Versioning

Versions are derived from git tags via [SemVer Gradle Plugin](https://github.com/JavierSegoviaCordoba/semver-gradle-plugin). Snapshot versions are published automatically on every push to `main`.

## License

See [LICENSE](LICENSE) for details.
