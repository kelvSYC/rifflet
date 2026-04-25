import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import kotlin.jvm.optionals.getOrNull

plugins {
    id("com.kelvsyc.internal.rifflet.kotlin-multiplatform-jvm-base")
}

val libs = versionCatalogs.named("libs")

kotlin {
    withSourcesJar(publish = true)

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }

    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3)
        languageVersion.set(KotlinVersion.KOTLIN_2_3)
    }

    sourceSets.commonMain.dependencies {
        implementation(dependencies.platform("com.kelvsyc.internal.rifflet:platform"))
    }

    sourceSets.commonTest.dependencies {
        libs.findLibrary("kotest-assertions-core").getOrNull()?.let { implementation(it) }
        libs.findLibrary("kotest-assertions-shared").getOrNull()?.let { implementation(it) }
        libs.findLibrary("kotest-framework-engine").getOrNull()?.let { implementation(it) }
        libs.findLibrary("kotest-runner").getOrNull()?.let { implementation(it) }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
