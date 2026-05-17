import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevSort
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import java.io.IOException

/**
 * Gradle [ValueSource] that derives a project version from the nearest ancestor git tag
 * matching a configurable prefix.
 *
 * Version computation:
 * - Locates the git repository via [FileRepositoryBuilder.findGitDir], which walks up the
 *   directory tree and follows `gitdir:` pointer files natively, resolving git worktrees
 *   without any additional configuration.
 * - Finds the nearest ancestor commit reachable from HEAD carrying a tag that starts with
 *   [Params.tagPrefix] and whose suffix parses as a clean `MAJOR.MINOR.PATCH` version.
 *   Tags with pre-release or build-metadata suffixes (e.g. `v1.2.3-beta`) are skipped;
 *   the walk continues until a clean three-part version is found.
 * - If [Params.stage] is `"snapshot"` and [Params.scope] is non-blank, the appropriate
 *   version component is incremented and `-SNAPSHOT` is appended.
 * - If the working tree is dirty, `+dirty` is appended as SemVer build metadata.
 * - Returns `null` if the repository cannot be opened or no matching tag is found
 *   (e.g. shallow clone, brand-new repository).
 *
 * Known limitation: dirty detection uses [Git.status], which traverses the entire working
 * tree. Acceptable for current use; revisit if this is ever extracted to a published plugin
 * targeting large monorepos.
 */
abstract class SemverValueSource : ValueSource<String, SemverValueSource.Params> {

    /**
     * Parameters for [SemverValueSource].
     */
    interface Params : ValueSourceParameters {
        /**
         * The settings directory of the build, used as the starting point for git repository
         * discovery. [FileRepositoryBuilder.findGitDir] walks upward from this directory.
         */
        val settingsDirectory: DirectoryProperty

        /**
         * The tag prefix used to filter version tags. Only tags whose name starts with this
         * value are considered. Defaults to `"v"` in [SemverSettingsPlugin].
         */
        val tagPrefix: Property<String>

        /**
         * The release stage. Set to `"snapshot"` to produce a pre-release snapshot version
         * with the version component specified by [scope] incremented. Leave blank for a
         * release version matching the exact tag.
         */
        val stage: Property<String>

        /**
         * The version component to increment when [stage] is `"snapshot"`. Accepts
         * `"major"`, `"minor"`, or `"patch"` (default when blank or unrecognised).
         */
        val scope: Property<String>
    }

    private data class SemVer(val major: Int, val minor: Int, val patch: Int) {
        override fun toString() = "$major.$minor.$patch"
    }

    override fun obtain(): String? {
        val builder = FileRepositoryBuilder()
            .findGitDir(parameters.settingsDirectory.asFile.get())
            .readEnvironment()
        if (builder.gitDir == null) return null
        return try {
            builder.build().use { findVersion(it) }
        } catch (_: IOException) {
            null
        }
    }

    private fun findVersion(repo: Repository): String? {
        val tagPrefix = parameters.tagPrefix.get()
        val stage = parameters.stage.orNull?.takeIf { it.isNotBlank() }
        val scope = parameters.scope.orNull?.takeIf { it.isNotBlank() }
        val semver = findLatestSemVer(repo, tagPrefix) ?: return null
        val baseVersion = if (stage == "snapshot" && scope != null) {
            "${bump(semver, scope)}-SNAPSHOT"
        } else {
            semver.toString()
        }
        val isDirty = try {
            !Git(repo).status().call().isClean
        } catch (_: GitAPIException) {
            false
        }
        return if (isDirty) "$baseVersion+dirty" else baseVersion
    }

    private fun findLatestSemVer(repo: Repository, tagPrefix: String): SemVer? {
        val tagMap = buildTagMap(repo, tagPrefix)
        val head = repo.resolve("HEAD")
        if (tagMap.isEmpty() || head == null) return null
        val walk = RevWalk(repo)
        return try {
            walk.sort(RevSort.COMMIT_TIME_DESC)
            walk.markStart(walk.parseCommit(head))
            var result: SemVer? = null
            for (commit in walk) {
                val tagName = tagMap[commit.id] ?: continue
                result = parseSemVer(tagName.removePrefix(tagPrefix))
                if (result != null) break
            }
            result
        } finally {
            walk.dispose()
        }
    }

    private fun buildTagMap(repo: Repository, tagPrefix: String): Map<ObjectId, String> {
        val map = mutableMapOf<ObjectId, String>()
        for (ref in repo.refDatabase.getRefsByPrefix("refs/tags/$tagPrefix")) {
            val peeled = repo.refDatabase.peel(ref)
            val id = peeled.peeledObjectId ?: ref.objectId
            map[id] = ref.name.removePrefix("refs/tags/")
        }
        return map
    }

    private fun parseSemVer(version: String): SemVer? {
        val parts = version.split(".")
        if (parts.size != 3) return null
        return try {
            SemVer(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        } catch (_: NumberFormatException) {
            null
        }
    }

    private fun bump(semver: SemVer, scope: String): SemVer = when (scope) {
        "major" -> SemVer(semver.major + 1, 0, 0)
        "minor" -> SemVer(semver.major, semver.minor + 1, 0)
        else -> SemVer(semver.major, semver.minor, semver.patch + 1)
    }
}
