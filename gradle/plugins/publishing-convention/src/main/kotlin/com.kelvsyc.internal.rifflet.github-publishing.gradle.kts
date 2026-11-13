import groovy.json.JsonOutput
import groovy.json.JsonSlurper

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
