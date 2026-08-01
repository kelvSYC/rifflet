package com.kelvsyc.rifflet.civ3

/**
 * Resolves [GovtEntry.prerequisiteTechnology] against [techs].
 */
fun GovtEntry.prerequisiteTechnologyTech(techs: List<TechEntry>): TechEntry? =
    techs.getOrNull(prerequisiteTechnology)

/**
 * The 7 values of [GovtEntry.corruption], per the Governments editor's "Corruption and Waste"
 * radio group. Ordinal position matches the raw file values — do not reorder these constants.
 *
 * The Conquests base ruleset's Anarchy entry has `corruption=4`, matching the Rules Editor's
 * "Catastrophic" selection; every other base-ruleset government's value is thematically exact for
 * its position too (Communism=5="Communal", Democracy=0="Minimal", Despotism=3="Rampant", etc.),
 * and no real file ever uses [OFF].
 *
 * [OFF] is a documented community-reported bug, not a functioning "no corruption" option: setting
 * it doesn't disable corruption in-game, and instead produces very high corruption in every city
 * but the capital, per community bug reports on the Rules Editor's Governments tab.
 *
 * [OFF] is also [Civ3FormatEra.CONQUESTS]-only: PTW's Governments tab has just the other 6
 * options in its "Corruption and Waste" radio group, with no `OFF` at all, and every real
 * PTW/vanilla government's `corruption` value is within 0-5, never 6. [Civ3FormatEra.VANILLA] is
 * assumed to match [Civ3FormatEra.PTW] here, per this codebase's usual treatment when no
 * dedicated vanilla-only source exists.
 */
enum class GovtCorruption { MINIMAL, NUISANCE, PROBLEMATIC, RAMPANT, CATASTROPHIC, COMMUNAL, OFF }

/**
 * Decodes [GovtEntry.corruption] into [GovtCorruption], or `null` if the raw value is outside
 * the documented 0-6 range.
 */
val GovtEntry.corruptionEnum: GovtCorruption?
    get() = GovtCorruption.entries.getOrNull(corruption)
