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

plugins {
    id("com.kelvsyc.internal.rifflet.settings")
}
