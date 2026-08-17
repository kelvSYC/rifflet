plugins {
    `java-platform`
}

group = "com.kelvsyc.internal.rifflet"

javaPlatform {
    // Allow for dependencies on other platforms (including BOMs)
    allowDependencies()
}

dependencies {
    api(platform(libs.kotest.bom))
    api(platform(libs.kotlin.gradle.plugins.bom))
    api(platform(libs.kotlin.tools.bom))
    // Not a direct dependency of any component; pins the versions of jackson-databind/jackson-core
    // and jsoup pulled in transitively by org.jetbrains.dokka:dokka-core past their known vulnerable
    // ranges, since dokka-gradle-plugin 2.2.0 (the latest release) still bundles older versions.
    api(platform(libs.jackson.bom))

    constraints {
        api(libs.jsoup)
    }
}
