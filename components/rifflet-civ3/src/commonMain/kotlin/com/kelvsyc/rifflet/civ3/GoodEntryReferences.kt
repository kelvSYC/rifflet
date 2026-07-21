package com.kelvsyc.rifflet.civ3

/**
 * Resolves [GoodEntry.prerequisite] against [techs].
 */
fun GoodEntry.prerequisiteTech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(prerequisite)
