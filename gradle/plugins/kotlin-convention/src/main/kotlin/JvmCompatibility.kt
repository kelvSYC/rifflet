import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions

/**
 * JDK versions for the published JVM components: built with the newest LTS JDK, released for the oldest one consumers use.
 *
 * Kept out of the version catalog so that dependency update tooling does not propose bumping it.
 */
object JvmCompatibility {
    /** Oldest JDK that consumers of the published components are expected to run on. */
    const val RELEASE = 17

    /** JDK the components are compiled with. */
    const val TOOLCHAIN = 25
}

/**
 * Configures the project so the published JVM output only requires [JvmCompatibility.RELEASE] from consumers.
 *
 * Lowering `jvmTarget` alone is not enough: compiling against a newer JDK's class library lets newer members into the
 * bytecode (e.g. `MutableList.removeFirst()` resolves to the JDK 21 `java.util.List.removeFirst`). `-Xjdk-release`
 * compiles against the release's API instead. The Java release also determines the published
 * `org.gradle.jvm.version`, and tests run on the release JDK to catch behaviour that differs from newer JDKs.
 */
fun Project.configureJvmCompatibility(compilerOptions: KotlinJvmCompilerOptions) {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(JvmCompatibility.RELEASE.toString()))
    compilerOptions.freeCompilerArgs.add("-Xjdk-release=${JvmCompatibility.RELEASE}")

    tasks.withType<JavaCompile>().configureEach {
        options.release.set(JvmCompatibility.RELEASE)
    }

    val releaseLauncher = extensions.getByType<JavaToolchainService>().launcherFor {
        languageVersion.set(JavaLanguageVersion.of(JvmCompatibility.RELEASE))
    }
    tasks.withType<Test>().configureEach {
        javaLauncher.set(releaseLauncher)
    }
}
