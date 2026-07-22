package com.kelvsyc.rifflet.civ3

/**
 * Resolves each id in [LeadEntry.startingTechnologyIds] against [techs], preserving position:
 * the result is the same length as [LeadEntry.startingTechnologyIds], with `null` at any
 * position whose id doesn't resolve.
 */
fun LeadEntry.startingTechnologyIdsTech(techs: List<TechEntry>): List<TechEntry?> =
    startingTechnologyIds.map { techs.getOrNull(it) }

/**
 * Resolves [LeadEntry.government] against [governments].
 */
fun LeadEntry.governmentGovt(governments: List<GovtEntry>): GovtEntry? =
    governments.getOrNull(government)

/**
 * Resolves [LeadEntry.civ] against [races]. `-2` (random) and `-3` (any) are not special-cased:
 * both are negative and so naturally resolve to `null` via [List.getOrNull], the same as any
 * other unresolvable index — a caller that cares about the specific sentinel value should check
 * [LeadEntry.civ] directly.
 */
fun LeadEntry.civRace(races: List<RaceEntry>): RaceEntry? = races.getOrNull(civ)

/**
 * Resolves [LeadEntry.initialEra] against [eras].
 */
fun LeadEntry.initialEraEras(eras: List<ErasEntry>): ErasEntry? = eras.getOrNull(initialEra)

/**
 * Resolves [LeadEntry.difficulty] against [difficulties]. `-2` (the "Any" sentinel) is not
 * special-cased: it's negative and so naturally resolves to `null` via [List.getOrNull] — a
 * caller that cares about the sentinel should check [LeadEntry.difficulty] directly.
 */
fun LeadEntry.difficultyDiff(difficulties: List<DiffEntry>): DiffEntry? = difficulties.getOrNull(difficulty)
