package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.TechEntry
import com.kelvsyc.rifflet.civ3.findTechPrerequisiteCycle

/**
 * Converts a parsed `TECH` section to its domain-layer form, resolving each entry's
 * self-referencing prerequisite indices into real [Tech] object references.
 *
 * Throws [IllegalArgumentException] if this list's prerequisite graph contains a cycle, checked
 * via [findTechPrerequisiteCycle] before constructing any [Tech] — this is what makes [Tech] safe
 * as a `data class` (see its own KDoc): nothing built through this function can ever be cyclic.
 */
fun List<TechEntry>.toDomain(): List<Tech> {
    val cycle = findTechPrerequisiteCycle(this)
    require(cycle == null) { "TechEntry prerequisite graph contains a cycle: ${cycle?.joinToString(" -> ") { it.name }}" }

    val techs = map { entry ->
        Tech(
            name = entry.name,
            civilopediaEntry = entry.civilopediaEntry,
            cost = entry.cost,
            era = entry.era,
            advanceIcon = entry.advanceIcon,
            x = entry.x,
            y = entry.y,
            flags = entry.flags,
            flavors = entry.flavors,
            unknown = entry.unknown,
        )
    }

    forEachIndexed { index, entry ->
        val tech = techs[index]
        tech.prerequisite1 = techs.getOrNull(entry.prerequisite1)
        tech.prerequisite2 = techs.getOrNull(entry.prerequisite2)
        tech.prerequisite3 = techs.getOrNull(entry.prerequisite3)
        tech.prerequisite4 = techs.getOrNull(entry.prerequisite4)
    }

    return techs
}
