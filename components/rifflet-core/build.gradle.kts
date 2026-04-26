plugins {
    id("com.kelvsyc.internal.rifflet.dokka")
    id("com.kelvsyc.internal.rifflet.kotlin-multiplatform-library")
    id("com.kelvsyc.internal.rifflet.kotlin-multiplatform-jvm")
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(libs.okio)
    }
}
