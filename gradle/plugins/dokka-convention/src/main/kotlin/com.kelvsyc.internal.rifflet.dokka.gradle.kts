import java.net.URI
import org.jetbrains.dokka.gradle.DokkaExtension

plugins {
    id("org.jetbrains.dokka")
}

val gitCommitHash: Provider<String> = providers.exec {
    commandLine("git", "rev-parse", "HEAD")
}.standardOutput.asText.map { it.trim() }

configure<DokkaExtension> {
    val rootGradle = generateSequence(gradle, Gradle::getParent).last()
    val relativePath = layout.projectDirectory.asFile
        .toRelativeString(rootGradle.rootProject.layout.projectDirectory.asFile)

    dokkaSourceSets.configureEach {
        enableJdkDocumentationLink.set(true)
        enableKotlinStdLibDocumentationLink.set(true)

        sourceLink {
            remoteUrl.set(gitCommitHash.map { URI("https://github.com/kelvSYC/rifflet/blob/$it/$relativePath") })
        }
    }
}

pluginManager.withPlugin("java") {
    apply(plugin = "org.jetbrains.dokka-javadoc")
    configure<DokkaExtension> {
        dokkaSourceSets.configureEach {
            jdkVersion.convention(
                project.the<JavaPluginExtension>().toolchain.languageVersion.map { it.asInt() }.orElse(25)
            )
        }
    }
    configure<JavaPluginExtension> {
        withJavadocJar()
    }
    tasks.named<Jar>("javadocJar") {
        from(tasks.named("dokkaGeneratePublicationJavadoc"))
    }
}

tasks.named("assemble") {
    dependsOn(tasks.named("dokkaGeneratePublicationHtml"))
}
