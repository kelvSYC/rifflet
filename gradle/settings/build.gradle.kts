plugins {
    `kotlin-dsl`
}

group = "com.kelvsyc.internal.rifflet"

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.conventions.settings.plugin)
}
