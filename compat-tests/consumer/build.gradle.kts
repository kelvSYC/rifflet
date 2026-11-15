// Consumer compatibility fixture: a standalone build that uses rifflet the way the oldest supported consumer would.
//
// The Gradle (wrapper), Kotlin and JDK versions pinned here ARE the supported floors. Change them only together with
// KotlinCompatibility / JvmCompatibility in gradle/plugins/kotlin-convention. Excluded from Dependabot for that reason.
//
// Run via `./gradlew consumerCompatibilityTest` from the repository root, which publishes the components to their
// local compatTest repositories and passes their paths in as `compatRepos`.

plugins {
    kotlin("jvm") version "2.1.21"
}

val compatRepos = providers.gradleProperty("compatRepos").orNull
    ?: error("Pass -PcompatRepos=<comma-separated repository paths>; run ./gradlew consumerCompatibilityTest from the repository root")

repositories {
    // Rifflet must come from the freshly published local artifacts, never from GitHub Packages.
    exclusiveContent {
        forRepositories(*compatRepos.split(",").map { path -> maven(file(path)) }.toTypedArray())
        filter { includeGroup("com.kelvsyc.rifflet") }
    }
    maven("https://maven.pkg.github.com/kelvSYC/kotlin-tools") {
        credentials {
            username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
            password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
        }
    }
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation("com.kelvsyc.rifflet:rifflet-core:+")
    testImplementation("com.kelvsyc.rifflet:rifflet-civ3:+")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
