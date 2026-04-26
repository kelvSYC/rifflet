import org.gradle.kotlin.dsl.support.expectedKotlinDslPluginsVersion

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    includeBuild("../platform")

    versionCatalogs.register("libs") {
        from(files("../libs.versions.toml"))
        // Add entry for the `kotlin-dsl` plugin, using the expectedKotlinDslPluginsVersion constant
        library("kotlin-dsl-plugins", "org.gradle.kotlin", "gradle-kotlin-dsl-plugins")
            .version(expectedKotlinDslPluginsVersion)
    }
}

plugins {
    // FIXME See if this can be specified from the version catalog
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include("dokka-convention")
include("kotlin-convention")
include("publishing-convention")
