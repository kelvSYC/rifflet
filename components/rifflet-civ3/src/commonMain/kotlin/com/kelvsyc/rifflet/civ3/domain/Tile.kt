package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.ContEntry
import com.kelvsyc.rifflet.civ3.GoodEntry
import com.kelvsyc.rifflet.civ3.TerrEntry

/**
 * A single map tile, mutable — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.TileEntry]. Carries no position: unlike [City]/[StartingLocation]/
 * [PlacedUnit]/[Colony], a tile isn't placed anywhere — it *is* a fixed map cell, and position is
 * a property of where it sits in a map structure, not of the tile's own content. A future
 * `WorldMap`/`TileGrid` wrapper type is the intended home for position-aware access.
 *
 * @param rivers This tile's river data. See [TileRivers].
 * @param improvements This tile's worker-built features. See [TileImprovements].
 * @param baseTerrain This tile's base terrain type.
 * @param overlayTerrain This tile's overlay terrain type (e.g. Hills/Forest on top of the base).
 * @param snowCappedMountains Whether this tile's Mountains are the snow-capped terrain-palette
 *   variant — cosmetically distinct from plain Mountains, not a separate gameplay effect.
 * @param pineForest Whether this tile's Forest is the pine terrain-palette variant — cosmetically
 *   distinct from plain Forest, not a separate gameplay effect.
 * @param resource This tile's strategic/luxury/bonus resource, if any.
 * @param bonusGrassland Whether this tile is Bonus Grassland — a genuine gameplay effect (+1
 *   shield vs. plain Grassland, per the Rules/Scenario editor) but not tied to the `GOOD`/resource
 *   system at all, and not purely cosmetic like [snowCappedMountains]/[pineForest].
 * @param textureLocation Which tile graphic within [textureFile] renders this tile. Purely
 *   cosmetic client-side rendering data, per community reverse-engineering documentation — no
 *   gameplay effect.
 * @param textureFile Which terrain-tileset graphic file renders this tile. Purely cosmetic
 *   client-side rendering data — no gameplay effect.
 * @param goodyHuts Whether this tile has a goody hut.
 * @param barbarianCamp Whether this tile has a barbarian camp.
 * @param pollution Whether this tile is polluted.
 * @param craters Whether this tile has craters (from nuclear/WMD damage). [Civ3FormatEra.CONQUESTS]
 *   only.
 * @param playerStart Whether this tile is a valid player starting location.
 * @param isVictoryPointLocation Whether this tile is a Victory Point Location (Victory Point game
 *   mode). `false` in files predating [Civ3FormatEra.PTW].
 * @param ruins Whether this tile has Ruins — an editor-placeable overlay, like a goody hut, that
 *   also results in real play when a player razes a captured city rather than keeping it.
 * @param isLandmarkTile Whether this tile is its terrain type's landmark instance (matching
 *   `TerrEntry.landmarkEnabled` for the corresponding `TERR` entry). [Civ3FormatEra.CONQUESTS]
 *   only.
 * @param barbarianTribe The name of the barbarian tribe associated with this tile (resolved
 *   against the barbarian-placeholder civilization's city-name pool), if any.
 * @param colony The colony/airfield/radar tower/outpost placed on this tile, if any.
 * @param city The city placed on this tile, if any.
 * @param continent The continent this tile belongs to.
 * @param fogOfWar Whether this tile is manually revealed to every civilization at scenario start
 *   (the Rules/Scenario editor's "Fog of War" tool defaults every map to fully covered, with this
 *   flag marking tiles the designer explicitly painted with "Remove Fog"). Not correlated with any
 *   individual civilization's own wartime visibility. [Civ3FormatEra.CONQUESTS] only.
 * @param border Opaque. Investigation (directional-neighbor-pairing tests, per-civilization
 *   ownership correlation, corpus segmentation, and a check against community reverse-engineering
 *   documentation) confirms this is structurally a per-civilization bitmask — every tile with a
 *   city on it has a nonzero value, and every civilization's own city tiles share a common bit —
 *   but the exact bit-to-civilization assignment doesn't match any tested encoding, and the real
 *   Rules/Scenario editor treats it as a read-only "Territory" display concept with no "paint this"
 *   authoring page, unlike every other field resolved during this investigation. Likely
 *   game-computed rather than author-settable. Preserved raw rather than decoded.
 */
data class Tile(
    var rivers: TileRivers = TileRivers(),
    var improvements: TileImprovements = TileImprovements(),
    var baseTerrain: TerrEntry? = null,
    var overlayTerrain: TerrEntry? = null,
    var snowCappedMountains: Boolean = false,
    var pineForest: Boolean = false,
    var resource: GoodEntry? = null,
    var bonusGrassland: Boolean = false,
    var textureLocation: Byte = 0,
    var textureFile: Byte = 0,
    var goodyHuts: Boolean = false,
    var barbarianCamp: Boolean = false,
    var pollution: Boolean = false,
    var craters: Boolean = false,
    var playerStart: Boolean = false,
    var isVictoryPointLocation: Boolean = false,
    var ruins: Boolean = false,
    var isLandmarkTile: Boolean = false,
    var barbarianTribe: String? = null,
    var colony: Colony? = null,
    var city: City? = null,
    var continent: ContEntry? = null,
    var fogOfWar: Boolean = false,
    var border: Byte = 0,
)
