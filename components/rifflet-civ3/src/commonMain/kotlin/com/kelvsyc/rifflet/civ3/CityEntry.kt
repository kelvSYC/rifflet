package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `CITY` section: a placed city instance.
 *
 * @param buildingIds Likely `BLDG` section indices (naming convention/`QueryCiv3` comment only);
 *   `BLDG` itself remains unmodeled in this codebase. Not confirmed by Apolyton's documentation.
 * @param culture Accumulated culture points for this city — NOT an index into the `CULT`
 *   section, which models culture-opinion levels rather than per-city point totals.
 * @param owner Meaning depends on [ownerType]: a `RACE` section index when Civ, a player index
 *   (0-based) when Player, or a barbarian tribe ID when Barbarian — same treatment as
 *   `SlocEntry.owner`/`ClnyEntry.owner`/`UnitEntry.owner`.
 */
data class CityEntry(
    val hasWalls: Byte,
    val hasPalace: Byte,
    val name: String,
    val ownerType: Int,
    val buildingIds: List<Int>,
    val culture: Int,
    val owner: Int,
    val size: Int,
    val x: Int,
    val y: Int,
    val cityLevel: Int,
    val borderLevel: Int,
    val useAutoName: Int,
)
