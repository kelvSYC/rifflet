package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `CTZN` section: a citizen type's naming and economic-output parameters.
 *
 * @param prerequisite Likely an index into the `TECH` section (inferred from the naming
 *   convention shared with `GovtEntry.prerequisiteTechnology`/`RaceEntry.freeTech1`..`4`); not
 *   confirmed by either primary source.
 */
data class CtznEntry(
    val defaultCitizen: Int,
    val singularName: String,
    val civilopediaEntry: String,
    val pluralName: String,
    val prerequisite: Int,
    val luxuries: Int,
    val research: Int,
    val taxes: Int,
    val corruption: Int,
    val construction: Int,
)
