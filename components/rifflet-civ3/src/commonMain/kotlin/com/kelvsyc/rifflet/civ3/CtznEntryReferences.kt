package com.kelvsyc.rifflet.civ3

/**
 * Resolves [CtznEntry.prerequisite] against [techs].
 */
fun CtznEntry.prerequisiteTech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(prerequisite)
