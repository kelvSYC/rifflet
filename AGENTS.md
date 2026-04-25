# AGENTS.md

Critical gotchas and quick reference for automated agents working in this Gradle composite build. For comprehensive architecture, see `CLAUDE.md`.

## Requirements for All Agents

- **Tests must pass.** All code changes must pass `./gradlew :test` before completing a task.
- **Be concise.** Avoid unnecessary explanation or narrative.
- **No unsolicited explanations.** Only explain what you're doing if explicitly asked.

## Build Commands

Root commands (aggregate across all components):
```bash
./gradlew :build          # Full build
./gradlew :test           # Tests only
./gradlew :publish        # Publish to GitHub Packages
```

Single component (included build form, from repository root):
```bash
./gradlew :rifflet-core:build
./gradlew :rifflet-core:test
```

## Kotlin File Rules (avoid failures before validation)

- **`NewLineAtEndOfFile`** — every `.kt` file must end with a newline character. **This is the single most common cause of build failures on generated files.** See the mandatory procedure below.
- **`WildcardImport`** — no wildcard imports (only `java.util.*` is allowed).
- **`TooGenericExceptionCaught`** — do not catch `Exception`, `RuntimeException`, `Error`, `Throwable`, `NullPointerException`, `IndexOutOfBoundsException`, or `IllegalMonitorStateException`.
- **`TooGenericExceptionThrown`** — do not throw `Exception`, `RuntimeException`, `Error`, or `Throwable`.
- **`ForbiddenComment`** — do not write `TODO:`, `FIXME:`, or `STOPSHIP:` markers.
- **`MagicNumber`** — no unexplained numeric literals in non-test, non-`.kts` source; extract to named constants.
- **`UnusedPrivateMember` / `UnusedPrivateProperty` / `UnusedPrivateClass`** — remove unused private declarations.

### Trailing newline: mandatory procedure ⚠️

**File-writing tools strip trailing newlines.** A content string ending with `\n` does NOT produce a file ending with `\n`. Fix immediately after every Write call on a `.kt` file:

```bash
echo "" >> path/to/File.kt
```

Verify with `tail -c1 <file> | xxd` — output must be `0a`.

### Pre-completion checklist for Kotlin file changes

Before declaring any task done, for every `.kt` file created or modified:

1. **Trailing newline** — `tail -c1 <file> | xxd` outputs `0a`. Fix with `echo "" >> <file>`.
2. **No wildcard imports** — no `import foo.bar.*` (except `java.util.*`).
3. **No magic numbers** in non-test, non-`.kts` source.
4. **No generic exception types** caught or thrown.
5. **No unused private members**.
6. **No TODO/FIXME/STOPSHIP comments**.

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
- **Convention plugins**: `gradle/plugins/kotlin-convention/src/main/kotlin/`
- **Source code**: `components/*/src/commonMain/kotlin/`
- **Tests**: `components/*/src/commonTest/kotlin/`
