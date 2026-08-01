package com.kelvsyc.rifflet.civ3

/**
 * Resolves [GoodEntry.prerequisite] against [techs].
 */
fun GoodEntry.prerequisiteTech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(prerequisite)

/**
 * The 3 values of [GoodEntry.type], per the Conquests Rules Editor's Resource Type dropdown.
 *
 * Ordinal position matches the raw file value exactly (raw `0` is [BONUS], raw `1` is [LUXURY],
 * raw `2` is [STRATEGIC]) — do not reorder these constants. The Rules Editor disables a
 * resource's appearance/disappearance controls when [BONUS] is selected, and enables them for
 * [LUXURY]/[STRATEGIC] (see [validateGoodBonusResourceDisabledFields]).
 */
enum class GoodResourceType { BONUS, LUXURY, STRATEGIC }
