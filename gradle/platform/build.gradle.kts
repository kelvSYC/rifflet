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
}
