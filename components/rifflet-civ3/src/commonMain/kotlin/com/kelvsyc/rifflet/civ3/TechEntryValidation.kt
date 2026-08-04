package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Finds a cycle in [techs]' prerequisite graph via depth-first search, if one exists. Returns the
 * cyclic path (in traversal order, repeating the starting entry) or `null` if the graph is
 * acyclic. Shared by [validateTechPrerequisitesAcyclic] and the domain-layer `toDomain()`
 * mapping, which uses it to guarantee it never constructs a cyclic `Tech` graph.
 */
internal fun findTechPrerequisiteCycle(techs: List<TechEntry>): List<TechEntry>? {
    val state = IntArray(techs.size) // 0 = unvisited, 1 = in progress, 2 = done
    val path = mutableListOf<Int>()

    fun dfs(index: Int): List<TechEntry>? {
        if (state[index] == 1) {
            val cycleStart = path.indexOf(index)
            return (path.subList(cycleStart, path.size) + index).map { techs[it] }
        }
        if (state[index] == 2) return null
        state[index] = 1
        path.add(index)
        for (prereqIndex in listOf(
            techs[index].prerequisite1,
            techs[index].prerequisite2,
            techs[index].prerequisite3,
            techs[index].prerequisite4,
        )) {
            if (prereqIndex in techs.indices) {
                val cycle = dfs(prereqIndex)
                if (cycle != null) return cycle
            }
        }
        path.removeAt(path.size - 1)
        state[index] = 2
        return null
    }

    for (i in techs.indices) {
        if (state[i] == 0) {
            val cycle = dfs(i)
            if (cycle != null) return cycle
        }
    }
    return null
}

/**
 * Flags each `TECH` entry whose prerequisite resolves to a different-era tech. Returns no issues
 * if the `TECH` section is absent from [file].
 *
 * Every prerequisite link in the real-file corpus is same-era — the Rules Editor's dropdown
 * appears to allow choosing a prerequisite from any era, but no real file (official or
 * third-party) ever does.
 */
fun validateTechPrerequisitesSameEra(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<TechSection>().singleOrNull() ?: return emptyList()
    val techs = section.entries
    return techs.flatMapIndexed { index, entry ->
        listOf(
            "prerequisite1" to entry.prerequisite1,
            "prerequisite2" to entry.prerequisite2,
            "prerequisite3" to entry.prerequisite3,
            "prerequisite4" to entry.prerequisite4,
        ).mapNotNull { (field, prereqIndex) ->
            val prereq = techs.getOrNull(prereqIndex) ?: return@mapNotNull null
            if (prereq.era == entry.era) {
                null
            } else {
                ValidationIssue(
                    ValidationSeverity.ERROR,
                    Civ3SectionIds.TECH,
                    index,
                    field,
                    "$field resolves to a different-era tech (this entry's era=${entry.era}, " +
                        "$field=$prereqIndex resolves to era=${prereq.era})",
                )
            }
        }
    }
}

/**
 * Flags a cycle in the `TECH` section's prerequisite graph, if one exists. Returns no issues if
 * the `TECH` section is absent from [file].
 *
 * No real file (official or third-party) has ever been found with a prerequisite cycle — the
 * Rules Editor doesn't appear to structurally prevent one, but no real file exercises it. A
 * cyclic prerequisite graph would make a tech permanently unresearchable.
 */
fun validateTechPrerequisitesAcyclic(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<TechSection>().singleOrNull() ?: return emptyList()
    val cycle = findTechPrerequisiteCycle(section.entries) ?: return emptyList()
    return listOf(
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.TECH,
            null,
            "prerequisite1/prerequisite2/prerequisite3/prerequisite4",
            "prerequisite graph contains a cycle: ${cycle.joinToString(" -> ") { it.name }}",
        ),
    )
}
