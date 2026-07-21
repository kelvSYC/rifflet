package com.kelvsyc.rifflet.civ3

/**
 * Resolves [GovtEntry.prerequisiteTechnology] against [techs].
 */
fun GovtEntry.prerequisiteTechnologyTech(techs: List<TechEntry>): TechEntry? =
    techs.getOrNull(prerequisiteTechnology)
