import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import kotlin.jvm.optionals.getOrNull

plugins {
    id("com.kelvsyc.internal.rifflet.kotlin-multiplatform-jvm-base")
}

val libs = versionCatalogs.named("libs")

kotlin {
    withSourcesJar(publish = true)

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }

    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3)
        languageVersion.set(KotlinVersion.KOTLIN_2_3)
    }

    sourceSets.commonMain.dependencies {
        implementation(dependencies.platform("com.kelvsyc.internal.rifflet:platform"))
    }

    sourceSets.commonTest.dependencies {
        libs.findLibrary("kotest-assertions-core").getOrNull()?.let { implementation(it) }
        libs.findLibrary("kotest-assertions-shared").getOrNull()?.let { implementation(it) }
        libs.findLibrary("kotest-framework-engine").getOrNull()?.let { implementation(it) }
        libs.findLibrary("kotest-runner").getOrNull()?.let { implementation(it) }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// Catch platform-specific imports that sneak into commonMain source sets.
// Any import of java.*, javax.*, android.*, or sun.* breaks non-JVM targets.
val checkCommonMainPlatformImports by tasks.registering {
    description = "Fails if any commonMain Kotlin source contains platform-specific imports."
    group = "verification"
    val sources = fileTree("src/commonMain") { include("**/*.kt") }
    inputs.files(sources).withPathSensitivity(PathSensitivity.RELATIVE)
    doLast {
        val platformPrefixes = listOf("import java.", "import javax.", "import android.", "import sun.")
        val violations = sources.files.flatMap { file ->
            file.readLines().mapIndexedNotNull { index, line ->
                val trimmed = line.trimStart()
                if (platformPrefixes.any { trimmed.startsWith(it) }) {
                    "  ${file.relativeTo(projectDir)}:${index + 1}: ${line.trim()}"
                } else null
            }
        }
        if (violations.isNotEmpty()) {
            error("Platform-specific imports found in commonMain:\n${violations.joinToString("\n")}")
        }
    }
}

tasks.named("check") {
    dependsOn(checkCommonMainPlatformImports)
}
