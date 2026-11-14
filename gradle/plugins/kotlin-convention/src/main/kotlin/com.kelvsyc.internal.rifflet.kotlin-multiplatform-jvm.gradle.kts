import kotlin.jvm.optionals.getOrNull
import org.gradle.api.attributes.java.TargetJvmVersion
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("com.kelvsyc.internal.rifflet.kotlin-multiplatform-base")
}

val libs = versionCatalogs.named("libs")

kotlin {
    jvm {
        project.configureJvmCompatibility(compilerOptions)
        // The Kotlin Gradle plugin never sets org.gradle.jvm.version on JVM target variants. Without it, Gradle
        // consumers on an older JVM resolve the components without complaint and fail at runtime, and our own JVM
        // classpaths accept dependencies that require a newer JVM.
        attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, JvmCompatibility.RELEASE)
    }

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(JvmCompatibility.TOOLCHAIN))
    }

    sourceSets.jvmTest.dependencies {
        libs.findLibrary("kotest-runner").getOrNull()?.let { implementation(it) }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

pluginManager.withPlugin("org.jetbrains.dokka") {
    apply(plugin = "org.jetbrains.dokka-javadoc")

    val javadocJar = tasks.register<Jar>("javadocJar") {
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
