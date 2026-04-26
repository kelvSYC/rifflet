// Settings plugin to be applied to all components

import com.javiersc.semver.settings.gradle.plugin.SemverSettingsExtension

pluginManagement {
    includeBuild("../../gradle/plugins")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
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
}
