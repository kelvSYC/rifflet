package com.kelvsyc.rifflet.civ3

/**
 * A named identity for one of the fixed-position `TERR` section slots. The Rules Editor's Terrain
 * tab only ever offers a Rename control — no Add, no Delete — so every real `TERR` section has
 * exactly one entry per slot valid for its era: 12 slots ([Civ3FormatEra.VANILLA]/
 * [Civ3FormatEra.PTW]) or 14 ([Civ3FormatEra.CONQUESTS], which inserts [MARSH] and [VOLCANO] after
 * [JUNGLE]).
 */
enum class TerrainSlot {
    DESERT, PLAINS, GRASSLAND, TUNDRA, FLOOD_PLAIN, HILLS, MOUNTAINS, FOREST, JUNGLE,
    MARSH, VOLCANO, COAST, SEA, OCEAN,
}

/**
 * This slot's `TERR` wire index for [era], or `null` if this slot doesn't exist in that era.
 * [TerrainSlot.MARSH]/[TerrainSlot.VOLCANO] are [Civ3FormatEra.CONQUESTS]-only; every other slot is
 * stable at indices 0-8, with [TerrainSlot.COAST]/[TerrainSlot.SEA]/[TerrainSlot.OCEAN] shifting by
 * +2 in [Civ3FormatEra.CONQUESTS] to make room.
 */
fun TerrainSlot.index(era: Civ3FormatEra): Int? = when (this) {
    TerrainSlot.DESERT -> 0
    TerrainSlot.PLAINS -> 1
    TerrainSlot.GRASSLAND -> 2
    TerrainSlot.TUNDRA -> 3
    TerrainSlot.FLOOD_PLAIN -> 4
    TerrainSlot.HILLS -> 5
    TerrainSlot.MOUNTAINS -> 6
    TerrainSlot.FOREST -> 7
    TerrainSlot.JUNGLE -> 8
    TerrainSlot.MARSH -> if (era == Civ3FormatEra.CONQUESTS) 9 else null
    TerrainSlot.VOLCANO -> if (era == Civ3FormatEra.CONQUESTS) 10 else null
    TerrainSlot.COAST -> if (era == Civ3FormatEra.CONQUESTS) 11 else 9
    TerrainSlot.SEA -> if (era == Civ3FormatEra.CONQUESTS) 12 else 10
    TerrainSlot.OCEAN -> if (era == Civ3FormatEra.CONQUESTS) 13 else 11
}
