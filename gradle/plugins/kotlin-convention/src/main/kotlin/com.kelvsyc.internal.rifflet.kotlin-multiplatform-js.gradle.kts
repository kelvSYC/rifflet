plugins {
    id("com.kelvsyc.internal.rifflet.kotlin-multiplatform-base")
}

kotlin {
    js(IR) {
        nodejs()
        binaries.library()
    }
}
