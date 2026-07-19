package com.kelvsyc.rifflet.civ3

/**
 * Resolves [CtznEntry.prerequisite] against [techs]. Likely an index into the `TECH` section
 * (inferred from the naming convention shared with `GovtEntry.prerequisiteTechnology`/
 * `RaceEntry.freeTech1`..`4`); not confirmed by either cross-referenced source.
 */
fun CtznEntry.prerequisiteTech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(prerequisite)
