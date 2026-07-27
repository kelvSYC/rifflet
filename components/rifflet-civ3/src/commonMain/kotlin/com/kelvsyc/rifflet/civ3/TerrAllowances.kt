package com.kelvsyc.rifflet.civ3

/**
 * A terrain type's city/improvement/movement allowances.
 *
 * Corresponds to the Conquests Rules Editor's `Terrain` tab's "Flags" groupbox, in its entirety.
 * Named `TerrAllowances` rather than a literal `TerrFlags` translation of the editor's own box
 * label, to avoid confusion with the unrelated, already-existing [TerrEntry.terrainFlags] opaque
 * bitmask field.
 *
 * @param allowCities The "Allow Cities" checkbox.
 * @param allowColonies The "Allow Colonies" checkbox.
 * @param impassable The "Impassable" checkbox — `null` in [Civ3FormatEra.VANILLA] files, which
 *   predate this and the other 4 checkboxes below.
 * @param impassableByWheeled The "Impassable by Wheeled Units" checkbox — same era treatment as
 *   [impassable].
 * @param allowAirfields The "Allow Airfields" checkbox — same era treatment as [impassable].
 * @param allowForts The "Allow Forts" checkbox — same era treatment as [impassable].
 * @param allowOutposts The "Allow Outposts" checkbox — same era treatment as [impassable].
 * @param allowRadarTowers The "Allow Radar Towers" checkbox — same era treatment as [impassable].
 */
data class TerrAllowances(
    val allowCities: Byte,
    val allowColonies: Byte,
    val impassable: Byte?,
    val impassableByWheeled: Byte?,
    val allowAirfields: Byte?,
    val allowForts: Byte?,
    val allowOutposts: Byte?,
    val allowRadarTowers: Byte?,
)
