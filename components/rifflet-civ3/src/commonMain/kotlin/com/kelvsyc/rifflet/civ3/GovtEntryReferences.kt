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
 * [OFF] is also [Civ3FormatEra.CONQUESTS]-only: PTW's and [Civ3FormatEra.VANILLA]'s Governments
 * tabs have just the other 6 options in their "Corruption and Waste" radio group, with no `OFF`
 * at all, and every real PTW/vanilla government's `corruption` value is within 0-5, never 6.
 */
enum class GovtCorruption { MINIMAL, NUISANCE, PROBLEMATIC, RAMPANT, CATASTROPHIC, COMMUNAL, OFF }

/**
 * The 3 values of [GovtEntry.hurrying], per the Governments editor's "Hurrying" dropdown. Ordinal
 * position matches the raw file value directly — do not reorder these constants.
 */
enum class GovtHurrying { CANNOT_HURRY, FORCED_LABOR, PAID_LABOR }

/**
 * The 3 values of [GovtEntry.warWeariness], per the Governments editor's "War Weariness"
 * dropdown. Ordinal position matches the raw file value directly — do not reorder these
 * constants.
 */
enum class GovtWarWeariness { NONE, LOW, HIGH }

/**
 * Resolves [GovtEntry.diplomatsAre] against [experienceLevels] — an `EXPR` section index, per the
 * Governments editor's Espionage groupbox "Diplomats Are" dropdown (Conscript/Regular/Veteran/
 * Elite, same values as [UnitEntry.experienceLevel]).
 */
fun GovtEntry.diplomatsAreExpr(experienceLevels: List<ExprEntry>): ExprEntry? = experienceLevels.getOrNull(diplomatsAre)

/**
 * Resolves [GovtEntry.spiesAre] against [experienceLevels] — an `EXPR` section index, same
 * treatment as [diplomatsAreExpr].
 */
fun GovtEntry.spiesAreExpr(experienceLevels: List<ExprEntry>): ExprEntry? = experienceLevels.getOrNull(spiesAre)

/**
 * Resolves [GovtEntry.immuneTo] against [espionageMissions] — an `ESPN` section index, per the
 * Governments editor's Espionage groupbox "Immune" dropdown ("None" or a specific espionage
 * mission this government's cities can't have performed against them). `-1` is "None" and
 * naturally resolves to `null` here, the same as any other out-of-range value.
 */
fun GovtEntry.immuneToEspn(espionageMissions: List<EspnEntry>): EspnEntry? = espionageMissions.getOrNull(immuneTo)
