package com.kelvsyc.rifflet.civ3

/**
 * Resolves [GovtEntry.prerequisiteTechnology] against [techs]. Likely a `TECH` section index —
 * inferred from the field name and the same naming convention as `CtznEntry.prerequisite`/
 * `RaceEntry.freeTech1`..`4` (see [CtznEntry.prerequisite]'s KDoc); not confirmed by either
 * primary source.
 */
fun GovtEntry.prerequisiteTechnologyTech(techs: List<TechEntry>): TechEntry? =
    techs.getOrNull(prerequisiteTechnology)
