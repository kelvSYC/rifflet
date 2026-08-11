package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.TechEntry
import com.kelvsyc.rifflet.civ3.findTechPrerequisiteCycle

/**
 * Converts a parsed `TECH` section to its domain-layer form, resolving each entry's
 * self-referencing prerequisite indices into real [Tech] object references, and [era] against
 * the already domain-converted `ERAS` list.
 *
 * Throws [IllegalArgumentException] if this list's prerequisite graph contains a cycle, checked
 * via [findTechPrerequisiteCycle] before constructing any [Tech] — this is what makes [Tech] safe
 * as a `data class` (see its own KDoc): nothing built through this function can ever be cyclic.
 */
fun List<TechEntry>.toDomain(eras: List<Era>): List<Tech> {
    val cycle = findTechPrerequisiteCycle(this)
    require(cycle == null) { "TechEntry prerequisite graph contains a cycle: ${cycle?.joinToString(" -> ") { it.name }}" }

    val techs = map { entry ->
        Tech(
            name = entry.name,
            civilopediaEntry = entry.civilopediaEntry,
            cost = entry.cost,
            era = eras.getOrNull(entry.era),
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

/**
 * Converts a `TECH` section's domain-layer form back to wire entries, resolving each [Tech]'s
 * self-referencing prerequisites back into indices against this list's own roster, and [Tech.era]
 * back into an index against [eras].
 *
 * Throws [IllegalArgumentException] if [Tech.prerequisite1], [Tech.prerequisite2],
 * [Tech.prerequisite3], [Tech.prerequisite4], or [Tech.era] references an object not present in
 * the corresponding list argument — a dangling reference at encode time is a real bug, not
 * something to default silently.
 *
 * Since [Tech] is a `data class`, `indexOf` below is a structural-equality match, not true
 * reference identity — a narrow, accepted limitation shared with GOVT's `toWire()` lookups
 * against its own `data class` wire types. Two hand-built [Tech]s with identical field values
 * would be indistinguishable here; this resolves naturally for genuinely distinct techs, the
 * overwhelmingly common case.
 */
fun List<Tech>.toWire(eras: List<Era>): List<TechEntry> {
    val roster = this
    fun resolve(field: String, prerequisite: Tech?): Int = prerequisite?.let {
        val index = roster.indexOf(it)
        require(index >= 0) { "Tech.$field references a Tech not present in this list" }
        index
    } ?: -1

    return map { tech ->
        TechEntry(
            name = tech.name,
            civilopediaEntry = tech.civilopediaEntry,
            cost = tech.cost,
            era = tech.era?.let {
                val index = eras.indexOf(it)
                require(index >= 0) { "Tech.era references an Era not present in eras" }
                index
            } ?: -1,
            advanceIcon = tech.advanceIcon,
            x = tech.x,
            y = tech.y,
            prerequisite1 = resolve("prerequisite1", tech.prerequisite1),
            prerequisite2 = resolve("prerequisite2", tech.prerequisite2),
            prerequisite3 = resolve("prerequisite3", tech.prerequisite3),
            prerequisite4 = resolve("prerequisite4", tech.prerequisite4),
            flags = tech.flags,
            flavors = tech.flavors,
            unknown = tech.unknown,
        )
    }
}
