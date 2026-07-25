package com.kelvsyc.rifflet.civ3

/**
 * Resolves [GovtEntry.prerequisiteTechnology] against [techs].
 */
fun GovtEntry.prerequisiteTechnologyTech(techs: List<TechEntry>): TechEntry? =
    techs.getOrNull(prerequisiteTechnology)

/**
 * The 7 values of [GovtEntry.corruption], per the Governments editor's "Corruption and Waste"
 * radio group. Ordinal position matches the raw file values — do not reorder these constants.
 * Confirmed by the Conquests base ruleset's Anarchy entry (`corruption=4`), which the Rules
 * Editor shows with "Catastrophic" selected; the remaining ordinals are corroborated
 * by every other base-ruleset government's value being thematically exact for its position
 * (Communism=5="Communal", Democracy=0="Minimal", Despotism=3="Rampant", etc.), with no real
 * file ever using [OFF].
 *
 * [OFF] is a documented community-reported bug, not a functioning "no corruption" option: setting
 * it doesn't disable corruption in-game, instead producing very high corruption in every city but
 * the capital. Confirmed independently by two separate CivFanatics threads discussing the Rules
 * Editor's Governments tab.
 *
 * [OFF] is also [Civ3FormatEra.CONQUESTS]-only: PTW's Governments tab has just the other 6
 * options in its "Corruption and Waste" radio group, with no `OFF` at all, and every real
 * PTW/vanilla government's `corruption` value is confirmed within 0-5 — never 6.
 * [Civ3FormatEra.VANILLA] is assumed to match [Civ3FormatEra.PTW] here per this codebase's usual
 * treatment, since no dedicated vanilla-only Rules Editor was available to confirm directly.
 */
enum class GovtCorruption { MINIMAL, NUISANCE, PROBLEMATIC, RAMPANT, CATASTROPHIC, COMMUNAL, OFF }

/**
 * Decodes [GovtEntry.corruption] into [GovtCorruption], or `null` if the raw value is outside
 * the documented 0-6 range.
 */
val GovtEntry.corruptionEnum: GovtCorruption?
    get() = GovtCorruption.entries.getOrNull(corruption)
