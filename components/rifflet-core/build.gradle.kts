plugins {
    id("com.kelvsyc.internal.rifflet.dokka")
    id("com.kelvsyc.internal.rifflet.github-publishing")
    id("com.kelvsyc.internal.rifflet.kotlin-multiplatform-library")
    id("com.kelvsyc.internal.rifflet.kotlin-multiplatform-jvm")
}

group = "com.kelvsyc.rifflet"

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(libs.okio)
    }
}
