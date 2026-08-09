package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.GoodEntry

/**
 * Converts a parsed `GOOD` section to its domain-layer form.
 *
 * [techs] is the already domain-converted `TECH` list. No `require()` guards: `GOOD` has no
 * ownership/type-restriction concept to enforce.
 */
fun List<GoodEntry>.toDomain(techs: List<Tech>): List<Resource> = map { entry ->
    Resource(
        name = entry.name,
        civilopediaEntry = entry.civilopediaEntry,
        type = entry.type,
        appearanceRatio = entry.appearanceRatio,
        disappearanceProbability = entry.disappearanceProbability,
        icon = entry.icon,
        prerequisite = techs.getOrNull(entry.prerequisite),
        foodBonus = entry.foodBonus,
        shieldsBonus = entry.shieldsBonus,
        commerceBonus = entry.commerceBonus,
    )
}

/**
 * Converts a `GOOD` section's domain-layer form back to wire entries.
 *
 * Throws [IllegalArgumentException] if [Resource.prerequisite] resolves to an object not present
 * in [techs] — `indexOf`-based, the same accepted structural-equality limitation as
 * GOVT/TECH/BLDG/PRTO/CITY/SLOC/UNIT/CLNY/TILE's `toWire()`.
 *
 * [Resource.prerequisite] is a plain resolved reference, like `PlacedUnit.unitType`/
 * `PlacedUnit.experienceLevel` — unlike `Owner`, a `null` value writes back `-1` rather than
 * preserving the original dangling index.
 */
fun List<Resource>.toWire(techs: List<Tech>): List<GoodEntry> = map { resource ->
    val prerequisiteIndex = resource.prerequisite?.let {
        val index = techs.indexOf(it)
        require(index >= 0) { "Resource.prerequisite references a Tech not present in techs" }
        index
    } ?: -1
    GoodEntry(
        name = resource.name,
        civilopediaEntry = resource.civilopediaEntry,
        type = resource.type,
        appearanceRatio = resource.appearanceRatio,
        disappearanceProbability = resource.disappearanceProbability,
        icon = resource.icon,
        prerequisite = prerequisiteIndex,
        foodBonus = resource.foodBonus,
        shieldsBonus = resource.shieldsBonus,
        commerceBonus = resource.commerceBonus,
    )
}
