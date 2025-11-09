plugins {
    id("com.kelvsyc.internal.rifflet.kotlin-multiplatform-jvm-library")
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(libs.okio)
    }
}
