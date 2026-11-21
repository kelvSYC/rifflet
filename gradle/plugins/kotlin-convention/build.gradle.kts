plugins {
    `kotlin-dsl`
}

group = "com.kelvsyc.internal.rifflet"

kotlin {
    jvmToolchain(25)
}

dependencies {
    implementation(platform(libs.conventions.platform))
    implementation(libs.kotlin.plugin)
}
