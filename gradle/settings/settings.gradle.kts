dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    includeBuild("../platform")

    versionCatalogs.register("libs") {
        from(files("../libs.versions.toml"))
    }
}

plugins {
    // FIXME See if this can be specified from the version catalog
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
