import java.net.URI
import org.jetbrains.dokka.gradle.DokkaExtension

plugins {
    id("org.jetbrains.dokka")
}

val gitCommitHash: Provider<String> = providers.exec {
    commandLine("git", "rev-parse", "HEAD")
}.standardOutput.asText.map { it.trim() }

// dokka-core bundles its own jackson/jsoup versions with known vulnerabilities; Dokka's generator
// configurations are populated by the plugin itself rather than the project's normal dependency
// declarations, so the platform BOM's constraints don't reach them and versions must be forced here.
val libs = versionCatalogs.named("libs")
val jacksonVersion = libs.findVersion("jackson").get().requiredVersion
val jsoupVersion = libs.findVersion("jsoup").get().requiredVersion
configurations.all {
    resolutionStrategy.eachDependency {
        // jackson-annotations trails the other jackson-bom modules' version numbering (e.g. bom 2.22.2
        // ships annotations 2.22, not 2.22.2), so it's left unforced: jackson-databind's own POM
        // auto-imports jackson-bom, which supplies the matching annotations version as a constraint.
        when {
            requested.group == "com.fasterxml.jackson.core" && requested.name == "jackson-annotations" -> Unit
            requested.group in setOf(
                "com.fasterxml.jackson.core",
                "com.fasterxml.jackson.module",
                "com.fasterxml.jackson.dataformat",
            ) -> useVersion(jacksonVersion)
            requested.group == "org.jsoup" -> useVersion(jsoupVersion)
        }
    }
}

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
