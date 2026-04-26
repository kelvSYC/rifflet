plugins {
    `kotlin-dsl`
}

group = "com.kelvsyc.internal.rifflet"

kotlin {
    jvmToolchain(25)
}

dependencies {
    implementation(platform("com.kelvsyc.internal.rifflet:platform"))
}
