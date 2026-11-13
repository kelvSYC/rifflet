import groovy.json.JsonSlurper
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.publish.tasks.GenerateModuleMetadata

/**
 * Fails module metadata generation if any published dependency has no version.
 *
 * A dependency declared without a version gets one at build time from somewhere consumers cannot see (the stripped
 * internal platform, Kotlin Gradle plugin defaults, included-build substitution). Publishing it as-is leaves
 * consumers unable to resolve it. Must run after [StripInternalPlatform.ModuleMetadataAction].
 */
class RequireDependencyVersions : Action<Task> {
    override fun execute(task: Task) {
        val file = (task as GenerateModuleMetadata).outputFile.get().asFile
        if (!file.exists()) return

        @Suppress("UNCHECKED_CAST")
        val json = JsonSlurper().parseText(file.readText()) as Map<String, Any?>
        @Suppress("UNCHECKED_CAST")
        val versionless = (json["variants"] as? List<Map<String, Any?>>).orEmpty()
            .flatMap { variant -> (variant["dependencies"] as? List<Map<String, Any?>>).orEmpty() }
            .filter { dep -> (dep["version"] as? Map<*, *>).isNullOrEmpty() }
            .map { dep -> "${dep["group"]}:${dep["module"]}" }
            .distinct()

        if (versionless.isNotEmpty()) {
            val component = json["component"] as? Map<*, *>
            throw GradleException(
                "${component?.get("group")}:${component?.get("module")} would publish dependencies without a version: " +
                    "${versionless.joinToString()}. " +
                    "Declare an explicit version (for other components, use the project version: " +
                    "\"com.kelvsyc.rifflet:<component>:\$version\")."
            )
        }
    }
}
