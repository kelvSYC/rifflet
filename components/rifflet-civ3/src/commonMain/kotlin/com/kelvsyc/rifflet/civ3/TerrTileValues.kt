package com.kelvsyc.rifflet.civ3

/**
 * A terrain type's base yield.
 *
 * Corresponds to the Conquests Rules Editor's `Terrain` tab's "Tile Values" groupbox, in its
 * entirety. Also used, unchanged, for [TerrLandmark]'s own nested "Tile Values" sub-box — the
 * landmark panel re-expresses the same 3 concepts for what a tile yields once its landmark is
 * placed.
 *
 * @param food The "Food" field.
 * @param shields The "Shields" field.
 * @param commerce The "Commerce" field.
 */
data class TerrTileValues(
    val food: Int,
    val shields: Int,
    val commerce: Int,
)
