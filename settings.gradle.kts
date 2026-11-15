plugins {
    // Lets root-build tasks (consumerCompatibilityTest) provision JDK toolchains; components get it from the settings
    // plugin. Keep the version in step with foojay-resolver-plugin in gradle/libs.versions.toml.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "rifflet"

// Components
file("components").list { dir, _ -> dir.isDirectory }?.forEach {
    includeBuild("components/$it")
}
