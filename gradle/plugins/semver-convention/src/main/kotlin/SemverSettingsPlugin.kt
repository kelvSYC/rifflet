import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

/**
 * Settings plugin that derives [project version][org.gradle.api.Project.getVersion] from
 * the nearest ancestor git tag matching a configurable prefix.
 *
 * Configured via JVM system properties, which propagate across composite-build
 * included-build settings evaluations (unlike Gradle project properties, which only
 * reach the root build's `StartParameter`):
 *
 * - `semver.tagPrefix` — tag prefix to match (default: `"v"`)
 * - `semver.stage`     — set to `"snapshot"` to produce a snapshot pre-release version
 * - `semver.scope`     — `"major"`, `"minor"`, or `"patch"` (default) for the snapshot bump
 *
 * The version is set to `"unspecified"` when no matching tag is found (e.g. shallow clone,
 * brand-new repository). Publishing tasks should guard against this value.
 */
class SemverSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        val versionProvider = settings.providers.of(SemverValueSource::class.java) {
            parameters {
                settingsDirectory.set(settings.layout.settingsDirectory)
                tagPrefix.set(settings.providers.systemProperty("semver.tagPrefix").orElse("v"))
                stage.set(settings.providers.systemProperty("semver.stage").orElse(""))
                scope.set(settings.providers.systemProperty("semver.scope").orElse(""))
            }
        }.orElse("unspecified")

        settings.gradle.beforeProject {
            version = versionProvider.get()
        }
    }
}
