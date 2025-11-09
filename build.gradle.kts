plugins {
    base
    publishing
}

group = "com.kelvsyc"

val components = buildList {
    add("rifflet-core")
}

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

tasks.build {
    components.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
}

tasks.publish {
    components.forEach {
        dependsOn(gradle.includedBuild(it).task(":$name"))
    }
}
