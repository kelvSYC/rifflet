package com.kelvsyc.rifflet.civ3.domain

/**
 * The set of Worker/Engineer-built features present on a tile — the domain-layer counterpart to
 * the worker-built subset of [com.kelvsyc.rifflet.civ3.TileEntry.overlayFlags]/
 * [com.kelvsyc.rifflet.civ3.TileEntry.c3cOverlays].
 *
 * Deliberately excludes [com.kelvsyc.rifflet.civ3.goodyHuts]/
 * [com.kelvsyc.rifflet.civ3.barbarianCamp]/[com.kelvsyc.rifflet.civ3.pollution]/
 * [com.kelvsyc.rifflet.civ3.craters] (kept as flat properties on [Tile] instead) — none of those
 * are placed/built by a worker, even though a worker can clean up pollution/craters.
 *
 * @param road Whether this tile has a road.
 * @param railroad Whether this tile has a railroad.
 * @param mine Whether this tile has a mine.
 * @param irrigation Whether this tile has irrigation.
 * @param fortress Whether this tile has a fortress.
 * @param barricade Whether this tile has a barricade. [Civ3FormatEra.CONQUESTS] only.
 * @param airfield Whether this tile has an airfield. [Civ3FormatEra.CONQUESTS] only.
 * @param radarTower Whether this tile has a radar tower. [Civ3FormatEra.CONQUESTS] only.
 * @param outpost Whether this tile has an outpost. [Civ3FormatEra.CONQUESTS] only.
 */
data class TileImprovements(
    var road: Boolean = false,
    var railroad: Boolean = false,
    var mine: Boolean = false,
    var irrigation: Boolean = false,
    var fortress: Boolean = false,
    var barricade: Boolean = false,
    var airfield: Boolean = false,
    var radarTower: Boolean = false,
    var outpost: Boolean = false,
)
