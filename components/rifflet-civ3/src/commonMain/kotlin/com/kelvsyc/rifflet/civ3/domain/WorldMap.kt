package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.ISOMETRIC_NEIGHBOR_DELTAS
import com.kelvsyc.rifflet.civ3.isometricTileCoordinates
import com.kelvsyc.rifflet.civ3.isometricTileIndex
import okio.ByteString

/**
 * The file's generated world map, mutable — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.WmapEntry], and the 2D-indexed home for [Tile]s (see [Tile]'s own
 * KDoc).
 *
 * @param tiles This map's tiles, in isometric storage order — the same order
 *   `TileEntryMapping.toDomain()` produces.
 * @param resources Likely `GOOD` section resources available on this map, positionally resolved —
 *   inferred from a separate reverse-engineered reference implementation's comment; not confirmed
 *   by existing reverse-engineering documentation.
 * @param unknown1 8 bytes with zero documented behavior; preserved raw, not validated.
 * @param unknown2 128 bytes with zero documented behavior; preserved raw, not validated.
 */
data class WorldMap(
    var width: Int = 0,
    var height: Int = 0,
    var tiles: MutableList<Tile> = mutableListOf(),
    var numberOfContinents: Int = 0,
    var distanceBetweenCivs: Int = 0,
    var numberOfCivs: Int = 0,
    var mapSeed: Int = 0,
    var resources: MutableList<Resource?> = mutableListOf(),
    var flags: Int = 0,
    var unknown1: ByteString = ByteString.of(*ByteArray(8)),
    var unknown2: ByteString = ByteString.of(*ByteArray(128)),
) {
    /** The [Tile] at map coordinate `(x, y)`. */
    operator fun get(x: Int, y: Int): Tile = tiles[isometricTileIndex(width, x, y)]

    /** Replaces the [Tile] at map coordinate `(x, y)`. */
    operator fun set(x: Int, y: Int, tile: Tile) {
        tiles[isometricTileIndex(width, x, y)] = tile
    }

    /** The `(x, y)` map coordinate of [tiles]'s entry at [index] — the inverse of [get]'s indexing. */
    fun coordinatesOf(index: Int): Pair<Int, Int> = isometricTileCoordinates(width, index)

    /** The up to 8 isometric neighbors of the tile at `(x, y)`, per [xWrapping]/[yWrapping]. */
    fun neighbors(x: Int, y: Int): List<Tile> = neighborIndices(x, y).map { tiles[it] }

    private fun neighborIndices(x: Int, y: Int): List<Int> = ISOMETRIC_NEIGHBOR_DELTAS.mapNotNull { (dx, dy) ->
        val nx = if (xWrapping) (x + dx).mod(width) else x + dx
        val ny = if (yWrapping) (y + dy).mod(height) else y + dy
        if (nx !in 0 until width || ny !in 0 until height) null else isometricTileIndex(width, nx, ny)
    }
}
