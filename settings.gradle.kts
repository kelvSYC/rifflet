pluginManagement {
    includeBuild("gradle/settings")
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

plugins {
    // Gives the root project the same git-tag version as the components, so consumerCompatibilityTest can request the
    // exact version it just published, and provisions JDK toolchains for root-build tasks.
    id("com.kelvsyc.internal.rifflet.settings")
}

rootProject.name = "rifflet"

// Components
file("components").list { dir, _ -> dir.isDirectory }?.forEach {
    includeBuild("components/$it")
}
