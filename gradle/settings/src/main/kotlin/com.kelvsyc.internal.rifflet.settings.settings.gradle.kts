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
    gitDir.set(layout.settingsDirectory.dir("../../.git"))
    tagPrefix.set("v")
}
