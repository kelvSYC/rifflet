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
}
