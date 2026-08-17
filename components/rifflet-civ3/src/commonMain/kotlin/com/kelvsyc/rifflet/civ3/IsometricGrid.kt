package com.kelvsyc.rifflet.civ3

/**
 * The isometric-storage-order index for the tile at `(x, y)` within a [width]-wide grid — shared
 * math between [WmapEntry]'s own accessors and the domain-layer `WorldMap` type.
 */
internal fun isometricTileIndex(width: Int, x: Int, y: Int): Int =
    (y / 2) * width + (if (y % 2 == 1) width / 2 else 0) + x / 2

/**
 * The `(x, y)` map coordinate of the tile at [index] within a [width]-wide grid — the inverse of
 * [isometricTileIndex].
 */
internal fun isometricTileCoordinates(width: Int, index: Int): Pair<Int, Int> {
    val row = index / width
    val slot = index % width
    return if (slot < width / 2) {
        2 * slot to 2 * row
    } else {
        (2 * (slot - width / 2) + 1) to (2 * row + 1)
    }
}

/**
 * The 8 isometric-storage-coordinate deltas to a tile's neighbors — shared between [WmapEntry]'s
 * own accessors and the domain-layer `WorldMap` type.
 */
internal val ISOMETRIC_NEIGHBOR_DELTAS = listOf(
    // Diagonal in storage coordinates (the 4 cardinal compass directions visually).
    -1 to -1, 1 to -1, -1 to 1, 1 to 1,
    // Orthogonal in storage coordinates (the 4 diagonal compass directions visually).
    -2 to 0, 2 to 0, 0 to -2, 0 to 2,
)
