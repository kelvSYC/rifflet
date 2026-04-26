rootProject.name = "rifflet"

// Components
file("components").list { dir, _ -> dir.isDirectory }?.forEach {
    includeBuild("components/$it")
}
