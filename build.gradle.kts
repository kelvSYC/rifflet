plugins {
    base
    publishing
    `jvm-toolchains`
}

group = "com.kelvsyc"

val components = file("components").list { dir, _ -> dir.isDirectory }?.toList().orEmpty()

tasks.clean {
    components.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
}

tasks.assemble {
    components.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
}

tasks.check {
    components.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
}

tasks.build {
    components.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
}

tasks.register("dokkaGenerate") {
    components.forEach {
        dependsOn(gradle.includedBuild(it).task(":dokkaGeneratePublicationHtml"))
    }
}

tasks.publish {
    components.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
}

tasks.register("publishToCompatTestRepository") {
    description = "Publishes every component to its local compatTest repository for the consumer compatibility fixture."
    components.forEach {
        dependsOn(gradle.includedBuild(it).task(":publishAllPublicationsToCompatTestRepository"))
    }
}

// JDK that runs the consumer compatibility fixture: the oldest Java release consumers are expected to use. Keep in
// sync with JvmCompatibility.RELEASE and with jvmToolchain(...) in compat-tests/consumer/build.gradle.kts.
val consumerCompatibilityJdk = 17

tasks.register<Exec>("consumerCompatibilityTest") {
    description = "Builds compat-tests/consumer, pinned to the supported Gradle/Kotlin/Java floors, against freshly published components."
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    dependsOn("publishToCompatTestRepository")

    val compatRepos = components.joinToString(",") { file("components/$it/build/compat-repo").absolutePath }
    // The fixture's (older) Gradle cannot run on the build JDK, so it is launched with the fixture JDK instead.
    val javaHome = javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(consumerCompatibilityJdk))
    }.map { it.metadata.installationPath.asFile.absolutePath }

    workingDir = file("compat-tests/consumer")
    commandLine("./gradlew", "check", "--no-daemon", "-PcompatRepos=$compatRepos")
    doFirst {
        environment("JAVA_HOME", javaHome.get())
    }
}
