package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.*
import okio.ByteString

/**
 * Converts a parsed `TILE` section to its domain-layer form.
 *
 * [colonies]/[cities]/[races]/[continents]/[terrains] are the already domain-converted `CLNY`/
 * `CITY`/`RACE`/`CONT`/`TERR` lists (`terrains` specifically as `Map<TerrainSlot, Terrain>.
 * toOrderedList(era)`, since `TERR`'s own domain form is a `Map`, not a `List`). [goods] stays
 * wire-typed for this field specifically — `GOOD` has a domain type (`Resource`) now, but
 * retrofitting `Tile.resource` wasn't part of that pass's scope. The caller is responsible for
 * supplying the right lists for this file — this file's own sections converted via their own
 * `toDomain()`, or externally-sourced standard lists, as appropriate.
 *
 * Every era-dependent field is fully resolved via [era] — see each field's own KDoc on [Tile] for
 * which wire fields it merges. No `require()` guards: unlike `City`/`StartingLocation`/
 * `PlacedUnit`/`Colony`, a `TILE` entry has no ownership-restriction concept to enforce.
 */
fun List<TileEntry>.toDomain(
    era: Civ3FormatEra,
    goods: List<GoodEntry>,
    colonies: List<Colony>,
    cities: List<City>,
    continents: List<Continent>,
    terrains: List<Terrain>,
    races: List<Race>,
): List<Tile> = map { entry ->
    Tile(
        rivers = TileRivers(
            north = entry.riverInNorth,
            west = entry.riverInWest,
            east = entry.riverInEast,
            south = entry.riverInSouth,
            crossingN = entry.crossingN,
            crossingNe = entry.crossingNe,
            crossingE = entry.crossingE,
            crossingSe = entry.crossingSe,
            crossingS = entry.crossingS,
            crossingSw = entry.crossingSw,
            crossingW = entry.crossingW,
            crossingNw = entry.crossingNw,
        ),
        improvements = TileImprovements(
            road = entry.road(era),
            railroad = entry.railroad(era),
            mine = entry.mine(era),
            irrigation = entry.irrigation(era),
            fortress = entry.fortress(era),
            barricade = entry.barricade,
            airfield = entry.airfield,
            radarTower = entry.radarTower,
            outpost = entry.outpost,
        ),
        baseTerrain = entry.baseTerrainIndex(era)?.let { terrains.getOrNull(it) },
        overlayTerrain = entry.overlayTerrainIndex(era)?.let { terrains.getOrNull(it) },
        snowCappedMountains = entry.snowCappedMountains(era),
        pineForest = entry.pineForest(era),
        resource = entry.resourceGood(goods),
        bonusGrassland = entry.bonusGrassland(era),
        textureLocation = entry.textureLocation,
        textureFile = entry.textureFile,
        goodyHuts = entry.goodyHuts(era),
        barbarianCamp = entry.barbarianCamp(era),
        pollution = entry.pollution(era),
        craters = entry.craters,
        playerStart = entry.playerStart(era),
        isVictoryPointLocation = entry.victoryPointLocation == 0.toShort(),
        ruins = entry.ruin != 0,
        isLandmarkTile = entry.isLandmarkTile,
        barbarianTribe = races.getOrNull(0)?.cityNames?.getOrNull(entry.barbarianTribe.toInt()),
        colony = colonies.getOrNull(entry.colony.toInt()),
        city = cities.getOrNull(entry.city.toInt()),
        continent = continents.getOrNull(entry.continent.toInt()),
        fogOfWar = entry.fogOfWar.toInt() != 0,
        border = entry.border,
    )
}

/**
 * Converts a `TILE` section's domain-layer form back to wire entries, resolving each [Tile]'s
 * object references back into indices, and re-packing every era-merged boolean back into the
 * wire representation appropriate for [era] — the other representation (legacy vs. `c3cXxx`) is
 * always written as zero-filled, matching how real files never carry meaningful data in both at
 * once.
 *
 * Throws [IllegalArgumentException] if [Tile.resource], [Tile.colony], [Tile.city],
 * [Tile.continent], [Tile.baseTerrain], or [Tile.overlayTerrain] resolves to an object not present
 * in the corresponding list argument — `indexOf`-based, the same accepted structural-equality
 * limitation as GOVT/TECH/BLDG/PRTO/CITY/SLOC/UNIT/CLNY's `toWire()`. None of these six fields (nor
 * [Tile.barbarianTribe]) preserve a dangling wire index across a `toDomain()`/`toWire()`
 * round-trip, unlike `Owner` — a `null` value writes back `-1` (or `0` for [Tile.baseTerrain]/
 * [Tile.overlayTerrain], which pack into a 4-bit nibble with no room for `-1`).
 *
 * [Tile.barbarianTribe] is the one exception that doesn't throw: a name not found in
 * `races[0].cityNames` (including when [races] is empty) writes back `-1` rather than throwing,
 * since an arbitrary string has no structural guarantee of matching anything.
 */
fun List<Tile>.toWire(
    era: Civ3FormatEra,
    goods: List<GoodEntry>,
    colonies: List<Colony>,
    cities: List<City>,
    continents: List<Continent>,
    terrains: List<Terrain>,
    races: List<Race>,
): List<TileEntry> = map { tile ->
    val isConquests = era == Civ3FormatEra.CONQUESTS

    val riverConnections = packByte(
        tile.rivers.north to 0, tile.rivers.west to 1, tile.rivers.east to 2, tile.rivers.south to 3,
    )
    val riverCrossingFlags = packByte(
        tile.rivers.crossingN to 0, tile.rivers.crossingNe to 1, tile.rivers.crossingE to 2,
        tile.rivers.crossingSe to 3, tile.rivers.crossingS to 4, tile.rivers.crossingSw to 5,
        tile.rivers.crossingW to 6, tile.rivers.crossingNw to 7,
    )

    val overlayFlags = if (isConquests) {
        0.toByte()
    } else {
        packByte(
            tile.improvements.road to 0, tile.improvements.railroad to 1, tile.improvements.mine to 2,
            tile.improvements.irrigation to 3, tile.improvements.fortress to 4, tile.goodyHuts to 5,
            tile.pollution to 6, tile.barbarianCamp to 7,
        )
    }
    val c3cOverlays = if (isConquests) {
        packIntLe(
            tile.improvements.road to 0, tile.improvements.railroad to 1, tile.improvements.mine to 2,
            tile.improvements.irrigation to 3, tile.improvements.fortress to 4, tile.goodyHuts to 5,
            tile.pollution to 6, tile.barbarianCamp to 7,
            tile.craters to 16, tile.improvements.barricade to 28, tile.improvements.airfield to 29,
            tile.improvements.radarTower to 30, tile.improvements.outpost to 31,
        )
    } else {
        ZERO_INT_BYTES
    }

    val bonusFlags = if (isConquests) {
        0.toByte()
    } else {
        packByte(
            tile.bonusGrassland to 0, tile.playerStart to 3, tile.snowCappedMountains to 4,
            tile.pineForest to 5,
        )
    }
    val c3cBonuses = if (isConquests) {
        packIntLe(
            tile.bonusGrassland to 0, tile.playerStart to 3, tile.snowCappedMountains to 4,
            tile.pineForest to 5, tile.isLandmarkTile to 13,
        )
    } else {
        ZERO_INT_BYTES
    }

    val baseTerrainIndex = tile.baseTerrain?.let {
        val index = terrains.indexOf(it)
        require(index >= 0) { "Tile.baseTerrain references a TerrEntry not present in terrains" }
        index
    } ?: 0
    val overlayTerrainIndex = tile.overlayTerrain?.let {
        val index = terrains.indexOf(it)
        require(index >= 0) { "Tile.overlayTerrain references a TerrEntry not present in terrains" }
        index
    } ?: 0
    val terrainNibbles = ((overlayTerrainIndex shl 4) or baseTerrainIndex).toByte()
    val terrainByte = if (isConquests) 0.toByte() else terrainNibbles
    val c3cTerrain: Byte? = if (isConquests) terrainNibbles else null

    val resourceIndex = tile.resource?.let {
        val index = goods.indexOf(it)
        require(index >= 0) { "Tile.resource references a GoodEntry not present in goods" }
        index
    } ?: -1
    val colonyIndex = tile.colony?.let {
        val index = colonies.indexOf(it)
        require(index >= 0) { "Tile.colony references a Colony not present in colonies" }
        index
    } ?: -1
    val cityIndex = tile.city?.let {
        val index = cities.indexOf(it)
        require(index >= 0) { "Tile.city references a City not present in cities" }
        index
    } ?: -1
    val continentIndex = tile.continent?.let {
        val index = continents.indexOf(it)
        require(index >= 0) { "Tile.continent references a Continent not present in continents" }
        index
    } ?: -1
    val barbarianTribeIndex = tile.barbarianTribe?.let { name ->
        races.getOrNull(0)?.cityNames?.indexOf(name)?.takeIf { it >= 0 }
    } ?: -1

    TileEntry(
        riverConnections = riverConnections,
        border = tile.border,
        resource = resourceIndex,
        textureLocation = tile.textureLocation,
        textureFile = tile.textureFile,
        unknown = ByteString.of(0, 0),
        overlayFlags = overlayFlags,
        terrain = terrainByte,
        bonusFlags = bonusFlags,
        riverCrossingFlags = riverCrossingFlags,
        barbarianTribe = barbarianTribeIndex.toShort(),
        colony = colonyIndex.toShort(),
        city = cityIndex.toShort(),
        continent = continentIndex.toShort(),
        unknown2 = ByteString.of(0),
        victoryPointLocation = (if (tile.isVictoryPointLocation) 0 else -1).toShort(),
        ruin = if (tile.ruins) 1 else 0,
        c3cOverlays = c3cOverlays,
        unknown3 = ByteString.of(0),
        c3cTerrain = c3cTerrain,
        unknown4 = ByteString.of(0, 0),
        fogOfWar = (if (tile.fogOfWar) 0x8000 else 0).toShort(),
        c3cBonuses = c3cBonuses,
        unknown5 = ByteString.of(0, 0),
        unknown6 = ByteString.of(0, 0, 0, 0),
    )
}

private val ZERO_INT_BYTES = ByteString.of(0, 0, 0, 0)

private fun packByte(vararg bits: Pair<Boolean, Int>): Byte {
    var result = 0
    for ((flag, bit) in bits) if (flag) result = result or (1 shl bit)
    return result.toByte()
}

private fun packIntLe(vararg bits: Pair<Boolean, Int>): ByteString {
    var result = 0
    for ((flag, bit) in bits) if (flag) result = result or (1 shl bit)
    val buffer = okio.Buffer()
    buffer.writeIntLe(result)
    return buffer.readByteString()
}
