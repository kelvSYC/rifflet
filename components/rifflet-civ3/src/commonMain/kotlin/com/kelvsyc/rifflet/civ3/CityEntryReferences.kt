package com.kelvsyc.rifflet.civ3

/**
 * Resolves [CityEntry.ownerType]/[CityEntry.owner] against [races]. See [Owner] for what each
 * case means.
 */
fun CityEntry.resolveOwner(races: List<RaceEntry>): Owner = resolveOwner(ownerType, owner, races)

/**
 * Resolves each id in [CityEntry.buildingIds] against [buildings], preserving position: the
 * result is the same length as [CityEntry.buildingIds], with `null` at any position whose id
 * doesn't resolve. Likely `BLDG` section indices (naming convention/`QueryCiv3` comment only);
 * `BLDG` itself remains unmodeled in this codebase's naming; not confirmed by Apolyton's
 * documentation.
 */
fun CityEntry.buildingsBldg(buildings: List<BldgEntry>): List<BldgEntry?> =
    buildingIds.map { buildings.getOrNull(it) }
