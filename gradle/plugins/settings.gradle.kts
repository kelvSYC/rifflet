import org.gradle.kotlin.dsl.support.expectedKotlinDslPluginsVersion

pluginManagement {
    includeBuild("../settings")
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

dependencyResolutionManagement {
    // The convention plugins depend on Gradle plugins published only to the Plugin Portal.
    repositories.gradlePluginPortal()

    versionCatalogs.named("libs") {
        // Add entry for the `kotlin-dsl` plugin, using the expectedKotlinDslPluginsVersion constant
        library("kotlin-dsl-plugins", "org.gradle.kotlin", "gradle-kotlin-dsl-plugins")
            .version(expectedKotlinDslPluginsVersion)
    }
}

include("dokka-convention")
include("kotlin-convention")
