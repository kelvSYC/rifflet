import groovy.json.JsonOutput
import groovy.json.JsonSlurper

plugins {
    `maven-publish`
}

publishing {
    repositories.maven("https://maven.pkg.github.com/kelvSYC/rifflet") {
        name = "GitHubPackages"
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
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
}
