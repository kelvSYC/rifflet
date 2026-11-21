// Settings plugin to be applied to all builds of rifflet. The shared settings plugin provides the repositories, the
// `libs` catalog (with the shared platform), semver versioning and the foojay toolchain resolver.

// Applied imperatively rather than in a `plugins {}` block: generating a precompiled settings plugin's accessors applies
// the `plugins {}` block to a stand-in Settings after settings evaluation, where Gradle rejects the repositories the
// shared plugin declares.
apply(plugin = "com.kelvsyc.internal.settings")

// Rifflet-only: the convention plugins in gradle/plugins at the repository root, and gradle/libs.versions.toml as the
// `local` catalog. Found by walking up from the settings directory, so that builds at any depth (components/*, the
// root) can apply this plugin; the gradle/plugins build itself applies it too, and must not include itself.
val buildDir: File = layout.settingsDirectory.asFile.canonicalFile
val conventionPlugins: File = generateSequence(buildDir) { it.parentFile }
    .map { it.resolve("gradle/plugins") }
    .firstOrNull { it.resolve("settings.gradle.kts").isFile }
    ?: error("No gradle/plugins build found above $buildDir")

if (conventionPlugins != buildDir) {
    pluginManagement.includeBuild(conventionPlugins.path)
}

// The shared plugin owns `libs`, so rifflet's own entries (the kotlin-tools dependency) live in `local`. Gradle itself
// imports gradle/libs.versions.toml under the default name `libs` in a build whose directory contains one (the root
// build), which would clash with the shared catalog; there the default import is simply renamed to `local`.
val catalogFile: File = conventionPlugins.resolve("../libs.versions.toml")
dependencyResolutionManagement {
    if (buildDir.resolve("gradle/libs.versions.toml").isFile) {
        defaultLibrariesExtensionName = "local"
    } else {
        versionCatalogs.register("local") {
            from(files(catalogFile))
        }
    }
}
