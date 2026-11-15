plugins {
    base
    publishing
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
