package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.WmapEntry

/**
 * Converts a parsed `WMAP` section to its domain-layer form.
 *
 * [tiles] is the already domain-converted `TILE` list (`TileEntryMapping.toDomain()`'s own
 * output, in the same isometric storage order `WmapEntry`'s indexing math assumes). [resources]
 * is the already domain-converted `GOOD` list. Flat list, no cardinality guard —
 * `validateWmapCardinality` already owns the "exactly 1" invariant at the wire layer.
 */
fun List<WmapEntry>.toDomain(tiles: List<Tile>, resources: List<Resource>): List<WorldMap> = map { entry ->
    WorldMap(
        width = entry.width,
        height = entry.height,
        tiles = tiles.toMutableList(),
        numberOfContinents = entry.numberOfContinents,
        distanceBetweenCivs = entry.distanceBetweenCivs,
        numberOfCivs = entry.numberOfCivs,
        mapSeed = entry.mapSeed,
        resources = entry.resourceIds.map { resources.getOrNull(it) }.toMutableList(),
        flags = entry.flags,
        unknown1 = entry.unknown1,
        unknown2 = entry.unknown2,
    )
}

/**
 * Converts a `WMAP` section's domain-layer form back to wire entries. Does not reconstruct
 * `TILE` entries — [WorldMap.tiles] is converted back separately via the existing
 * `List<Tile>.toWire(...)`, using this list's own [WorldMap.tiles] as its input.
 *
 * Throws [IllegalArgumentException] if any [WorldMap.resources] entry resolves to an object not
 * present in [resources] — `indexOf`-based, the same accepted structural-equality limitation as
 * every other `toWire()` in this codebase. A `null` entry writes back `-1`.
 */
fun List<WorldMap>.toWire(resources: List<Resource>): List<WmapEntry> = map { worldMap ->
    val resourceIds = worldMap.resources.map { resource ->
        resource?.let {
            val index = resources.indexOf(it)
            require(index >= 0) { "WorldMap.resources references a Resource not present in resources" }
            index
        } ?: -1
    }
    WmapEntry(
        resourceIds = resourceIds,
        numberOfContinents = worldMap.numberOfContinents,
        height = worldMap.height,
        distanceBetweenCivs = worldMap.distanceBetweenCivs,
        numberOfCivs = worldMap.numberOfCivs,
        unknown1 = worldMap.unknown1,
        width = worldMap.width,
        unknown2 = worldMap.unknown2,
        mapSeed = worldMap.mapSeed,
        flags = worldMap.flags,
    )
}
