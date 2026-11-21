// Consumer compatibility fixture: a standalone build that uses rifflet the way the oldest supported consumer would.
//
// The Gradle (wrapper), Kotlin and JDK versions pinned here ARE the supported floors. Change them only together with
// the floors of the shared Kotlin conventions (`kelvsycKotlin` defaults in gradle-build-conventions), and raise them
// here first: the conventions' canary builds this repository's main against a floor change, so the raised pins must
// already be on main before the floor moves in gradle-build-conventions. Excluded from Dependabot for that reason.
//
// Run via `./gradlew consumerCompatibilityTest` from the repository root, which publishes the components to Maven Local
// and passes the version it just published as `riffletVersion`.

plugins {
    kotlin("jvm") version "2.1.21"
}

val riffletVersion = providers.gradleProperty("riffletVersion").orNull
    ?: error("Pass -PriffletVersion=<version>; run ./gradlew consumerCompatibilityTest from the repository root")

repositories {
    // Rifflet must come from the freshly published local artifacts, never from GitHub Packages. Maven Local may hold
    // other versions too; the exact riffletVersion selects the one just published.
    exclusiveContent {
        forRepository { mavenLocal() }
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
    testImplementation("com.kelvsyc.rifflet:rifflet-core:$riffletVersion")
    testImplementation("com.kelvsyc.rifflet:rifflet-civ3:$riffletVersion")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
