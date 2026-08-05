package com.kelvsyc.rifflet.civ3.domain

/**
 * A placed city instance, mutable and cross-referenced by real object references — the
 * domain-layer counterpart to [com.kelvsyc.rifflet.civ3.CityEntry].
 *
 * A `data class`, like [Tech]/[Race]/[com.kelvsyc.rifflet.civ3.domain.Prto] and unlike the
 * plain-class [Government]: [City] has no self-referencing structure, so there's no
 * circular-reference risk that would require plain-class identity semantics.
 *
 * @param name This city's name.
 * @param owner This city's owner. See [Owner].
 * @param buildings This city's improvements, per the Tile Properties editor's City tab
 *   "Improvements" listbox. Preserves position and duplicates exactly like the wire
 *   `buildingIds: List<Int>` (not a `Set`) — real files never have a duplicate, but the
 *   representation must still support one for `validateCityGreatWonderUniqueGlobally`/
 *   `validateCitySmallWonderUniquePerNation` to have something to check.
 * @param culture Accumulated culture points for this city.
 * @param size This city's population size level.
 * @param x This city's map X coordinate.
 * @param y This city's map Y coordinate.
 * @param cityLevel This city's level (town/city/metropolis).
 * @param borderLevel This city's culture-border level.
 * @param hasWalls Whether this city has the Walls building present. Not derived from [buildings]:
 *   unlike [hasPalace]/`centerOfEmpire`, there's no BLDG bit-flag backing this correlation, only a
 *   fragile display-name match with no exceptionless corpus evidence — kept as a fully independent
 *   field.
 * @param hasPalace Whether this city has a `centerOfEmpire`-flagged building present. Not derived
 *   from [buildings] despite the exceptionless real-data correlation — kept as a real field with a
 *   paired validation rule (`validateCityHasPalaceMatchesCenterOfEmpire`) instead, matching this
 *   project's usual redundant-but-real-field treatment.
 * @param useAutoName Whether this city uses an auto-generated name. Never observed set in any real
 *   file checked.
 */
data class City(
    var name: String,
    var x: Int,
    var y: Int,
    var owner: Owner = Owner.None,
    var buildings: MutableList<Building?> = mutableListOf(),
    var culture: Int = 0,
    var size: Int = 0,
    var cityLevel: Int = 0,
    var borderLevel: Int = 0,
    var hasWalls: Boolean = false,
    var hasPalace: Boolean = false,
    var useAutoName: Boolean = false,
)
