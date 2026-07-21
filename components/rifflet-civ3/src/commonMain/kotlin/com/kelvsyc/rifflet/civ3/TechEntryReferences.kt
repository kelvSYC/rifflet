package com.kelvsyc.rifflet.civ3

/**
 * Resolves [TechEntry.prerequisite1] against [techs] (the same `TECH` section this entry came
 * from). Same treatment applies to [TechEntry.prerequisite2], [TechEntry.prerequisite3], and
 * [TechEntry.prerequisite4].
 */
fun TechEntry.prerequisite1Tech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(prerequisite1)

/**
 * Resolves [TechEntry.prerequisite2] against [techs]. Same treatment as
 * [TechEntry.prerequisite1].
 */
fun TechEntry.prerequisite2Tech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(prerequisite2)

/**
 * Resolves [TechEntry.prerequisite3] against [techs]. Same treatment as
 * [TechEntry.prerequisite1].
 */
fun TechEntry.prerequisite3Tech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(prerequisite3)

/**
 * Resolves [TechEntry.prerequisite4] against [techs]. Same treatment as
 * [TechEntry.prerequisite1].
 */
fun TechEntry.prerequisite4Tech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(prerequisite4)
