import kotlin.jvm.optionals.getOrNull
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("com.kelvsyc.internal.rifflet.kotlin-multiplatform-base")
}

val libs = versionCatalogs.named("libs")

kotlin {
    jvm()

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
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
