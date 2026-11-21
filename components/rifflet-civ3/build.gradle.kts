plugins {
    id("com.kelvsyc.internal.rifflet.dokka")
    id("com.kelvsyc.internal.github-publishing")
    id("com.kelvsyc.internal.rifflet.kotlin-multiplatform-library")
    id("com.kelvsyc.internal.rifflet.kotlin-multiplatform-jvm")
}

group = "com.kelvsyc.rifflet"

kotlin {
    sourceSets.commonMain.dependencies {
        api("com.kelvsyc.rifflet:rifflet-core:$version")
        api(libs.okio)
        implementation(local.kotlin.core)
    }
}
