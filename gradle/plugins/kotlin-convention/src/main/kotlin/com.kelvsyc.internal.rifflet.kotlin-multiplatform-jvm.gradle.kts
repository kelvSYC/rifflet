import kotlin.jvm.optionals.getOrNull
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("com.kelvsyc.internal.rifflet.kotlin-multiplatform-base")
}

val libs = versionCatalogs.named("libs")

kotlin {
    jvm {
        compilerOptions {
            configureJvmCompatibility(this)
        }
    }

    // Builds with the newest JDK; the published bytecode targets KotlinCompatibility.JVM_RELEASE instead.
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }

    sourceSets.jvmTest.dependencies {
        libs.findLibrary("kotest-runner").getOrNull()?.let { implementation(it) }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(KotlinCompatibility.JVM_RELEASE)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    // Run tests on the oldest supported Java release, so the published bytecode is proven to load there.
    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(KotlinCompatibility.JVM_RELEASE))
        }
    )
}

pluginManager.withPlugin("org.jetbrains.dokka") {
    apply(plugin = "org.jetbrains.dokka-javadoc")

    val javadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
        from(tasks.named("dokkaGeneratePublicationJavadoc"))
    }

    pluginManager.withPlugin("maven-publish") {
        extensions.configure<PublishingExtension> {
            publications.withType<MavenPublication>().matching { it.name == "jvm" }.configureEach {
                artifact(javadocJar)
            }
        }
    }
}
