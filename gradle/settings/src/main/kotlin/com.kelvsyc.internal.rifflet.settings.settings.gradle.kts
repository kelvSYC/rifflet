// Settings plugin to be applied to all components

import com.javiersc.semver.settings.gradle.plugin.SemverSettingsExtension

pluginManagement {
    includeBuild("../../gradle/plugins")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.github.com/kelvSYC/kotlin-tools") {
            name = "GitHubPackages"
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }

    includeBuild("../../gradle/platform")

    versionCatalogs.register("libs") {
        from(files("../../gradle/libs.versions.toml"))
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
    id("com.javiersc.semver")
}

configure<SemverSettingsExtension> {
    isEnabled.set(true)
    // Resolve .git worktree pointer files: jgit's FileRepositoryBuilder.setGitDir() (used by
    // com.javiersc.semver 0.9.0) does not follow `gitdir:` pointers, so in a git worktree the
    // raw `.git` file must be replaced with the real worktree git directory.
    gitDir.set(layout.settingsDirectory.dir(resolveGitDir("../../.git").absolutePath))
    tagPrefix.set("v")
}

fun resolveGitDir(relativePath: String): java.io.File {
    val marker = layout.settingsDirectory.asFile.resolve(relativePath)
    if (!marker.isFile) return marker
    val pointer = marker.readLines()
        .firstOrNull { it.startsWith("gitdir:") }
        ?.substringAfter("gitdir:")
        ?.trim()
        ?: return marker
    val pointed = java.io.File(pointer)
    val resolved = if (pointed.isAbsolute) pointed else marker.parentFile.resolve(pointed)
    return resolved.canonicalFile
}
