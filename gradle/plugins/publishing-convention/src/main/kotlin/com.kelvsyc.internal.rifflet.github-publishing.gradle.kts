plugins {
    `maven-publish`
}

publishing {
    repositories.maven("https://maven.pkg.github.com/kelvSYC/rifflet") {
        name = "GitHubPackages"
        credentials {
            username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
            password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
        }
    }

    // Local repository consumed by the consumer compatibility fixture (compat-tests/consumer). Wiped before every
    // publish so it only ever holds the version just built.
    repositories.maven(layout.buildDirectory.dir("compat-repo")) {
        name = "compatTest"
    }

    publications.withType<MavenPublication>().configureEach {
        pom.withXml {
            StripInternalPlatform.fromPom(asNode())
        }
    }
}

tasks.withType<GenerateModuleMetadata>().configureEach {
    doLast(StripInternalPlatform.ModuleMetadataAction())
    doLast(RequireDependencyVersions())
}

// Generate module metadata during `check` so RequireDependencyVersions fails in CI, not at release time.
tasks.matching { it.name == "check" }.configureEach {
    dependsOn(tasks.withType<GenerateModuleMetadata>())
}

val cleanCompatTestRepository = tasks.register<Delete>("cleanCompatTestRepository") {
    delete(layout.buildDirectory.dir("compat-repo"))
}

tasks.withType<PublishToMavenRepository>().matching { it.name.endsWith("ToCompatTestRepository") }.configureEach {
    dependsOn(cleanCompatTestRepository)
}
