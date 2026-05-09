import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.publish.tasks.GenerateModuleMetadata

object StripInternalPlatform {
    private const val INTERNAL_GROUP = "com.kelvsyc.internal.rifflet"

    fun fromPom(root: groovy.util.Node) {
        root.children().removeAll { child ->
            child is groovy.util.Node && child.name().toString().contains("dependencyManagement")
                && child.children().any { deps ->
                    deps is groovy.util.Node && deps.children().any { dep ->
                        dep is groovy.util.Node && groupIdOf(dep) == INTERNAL_GROUP
                    }
                }
        }
        val deps = root.children().find {
            it is groovy.util.Node && it.name().toString().contains("dependencies")
        } as? groovy.util.Node
        deps?.children()?.removeAll { dep ->
            dep is groovy.util.Node && groupIdOf(dep) == INTERNAL_GROUP
        }
    }

    private fun groupIdOf(dep: groovy.util.Node): String? =
        (dep.children().find {
            it is groovy.util.Node && it.name().toString().contains("groupId")
        } as? groovy.util.Node)?.text()

    class ModuleMetadataAction : Action<Task> {
        override fun execute(task: Task) {
            val file = (task as GenerateModuleMetadata).outputFile.get().asFile
            if (!file.exists()) return

            @Suppress("UNCHECKED_CAST")
            val json = JsonSlurper().parseText(file.readText()) as MutableMap<String, Any?>
            @Suppress("UNCHECKED_CAST")
            (json["variants"] as? List<MutableMap<String, Any?>>)?.forEach { variant ->
                @Suppress("UNCHECKED_CAST")
                (variant["dependencies"] as? MutableList<Map<String, Any?>>)?.removeAll { dep ->
                    dep["group"] == INTERNAL_GROUP
                }
                @Suppress("UNCHECKED_CAST")
                (variant["dependencyConstraints"] as? MutableList<Map<String, Any?>>)?.removeAll { dep ->
                    dep["group"] == INTERNAL_GROUP
                }
            }
            file.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(json)))
        }
    }
}
