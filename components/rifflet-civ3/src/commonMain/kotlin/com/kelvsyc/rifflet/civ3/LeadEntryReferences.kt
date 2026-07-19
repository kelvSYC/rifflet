package com.kelvsyc.rifflet.civ3

/**
 * Resolves each id in [LeadEntry.startingTechnologyIds] against [techs], preserving position:
 * the result is the same length as [LeadEntry.startingTechnologyIds], with `null` at any
 * position whose id doesn't resolve. Likely `TECH` section indices (naming convention only); not
 * confirmed by either cross-referenced source.
 */
fun LeadEntry.startingTechnologyIdsTech(techs: List<TechEntry>): List<TechEntry?> =
    startingTechnologyIds.map { techs.getOrNull(it) }

/**
 * Resolves [LeadEntry.government] against [governments]. Likely a `GOVT` section index (naming
 * convention only); not confirmed by either cross-referenced source.
 */
fun LeadEntry.governmentGovt(governments: List<GovtEntry>): GovtEntry? =
    governments.getOrNull(government)

/**
 * Resolves [LeadEntry.civ] against [races]. `-2` (random) and `-3` (any) are not special-cased:
 * both are negative and so naturally resolve to `null` via [List.getOrNull], the same as any
 * other unresolvable index — a caller that cares about the specific sentinel value should check
 * [LeadEntry.civ] directly. Otherwise likely a `RACE` section index (naming convention only);
 * not confirmed by either cross-referenced source.
 */
fun LeadEntry.civRace(races: List<RaceEntry>): RaceEntry? = races.getOrNull(civ)
