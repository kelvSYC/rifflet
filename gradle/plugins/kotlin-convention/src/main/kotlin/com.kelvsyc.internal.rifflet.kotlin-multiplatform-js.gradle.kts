import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    id("com.kelvsyc.internal.rifflet.kotlin-multiplatform-base")
}

kotlin {
    js(IR) {
        nodejs()
        binaries.library()
    }

    // Kotlin/JS rejects a stdlib whose klib ABI is older than the compiler's, so the older stdlib pinned by
    // configureKotlinCompatibility (for the benefit of JVM consumers) is raised back to the compiler's version here.
    sourceSets.jsMain.dependencies {
        implementation(kotlin("stdlib", getKotlinPluginVersion()))
    }
}
