package com.kelvsyc.rifflet.civ3

/**
 * Resolves [GoodEntry.prerequisite] against [techs]. Likely an index into the `TECH` section
 * (inferred from the naming convention shared with `CtznEntry.prerequisite`/
 * `GovtEntry.prerequisiteTechnology`); not confirmed by either primary source.
 */
fun GoodEntry.prerequisiteTech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(prerequisite)
