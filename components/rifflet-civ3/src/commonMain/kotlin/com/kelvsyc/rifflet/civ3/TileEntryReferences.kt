package com.kelvsyc.rifflet.civ3

/**
 * Resolves [TileEntry.resource] against [goods].
 */
fun TileEntry.resourceGood(goods: List<GoodEntry>): GoodEntry? = goods.getOrNull(resource)

/**
 * Resolves [TileEntry.colony] against [colonies].
 */
fun TileEntry.colonyClny(colonies: List<ClnyEntry>): ClnyEntry? = colonies.getOrNull(colony.toInt())

/**
 * Resolves [TileEntry.continent] against [continents]. Every tile resolves to some entry — see
 * [ContEntry]'s own KDoc for the partition/contiguity guarantees this always holds.
 */
fun TileEntry.continentCont(continents: List<ContEntry>): ContEntry? = continents.getOrNull(continent.toInt())

/**
 * Resolves [TileEntry.city] against [cities].
 */
fun TileEntry.cityCity(cities: List<CityEntry>): CityEntry? = cities.getOrNull(city.toInt())

/**
 * [TileEntry.bonusGrassland] resolved for [era]: reads [TileEntry.c3cBonusGrassland] for
 * [Civ3FormatEra.CONQUESTS] files, where real per-tile bonus-terrain data lives, or the legacy
 * [TileEntry.bonusGrassland] otherwise, including for [Civ3FormatEra.PTW] files.
 */
fun TileEntry.bonusGrassland(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.CONQUESTS) c3cBonusGrassland else bonusGrassland

/**
 * [TileEntry.snowCappedMountains] resolved for [era]. Same treatment as
 * [TileEntry.bonusGrassland]'s era-resolved overload.
 */
fun TileEntry.snowCappedMountains(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.CONQUESTS) c3cSnowCappedMountains else snowCappedMountains

/**
 * [TileEntry.pineForest] resolved for [era]. Same treatment as [TileEntry.bonusGrassland]'s
 * era-resolved overload.
 */
fun TileEntry.pineForest(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.CONQUESTS) c3cPineForest else pineForest

/**
 * [TileEntry.road] resolved for [era]: reads [TileEntry.c3cRoad] for [Civ3FormatEra.CONQUESTS]
 * files, where real per-tile overlay data lives, or the legacy [TileEntry.road] otherwise.
 */
fun TileEntry.road(era: Civ3FormatEra): Boolean = if (era == Civ3FormatEra.CONQUESTS) c3cRoad else road

/**
 * [TileEntry.railroad] resolved for [era]. Same treatment as [TileEntry.road]'s era-resolved
 * overload.
 */
fun TileEntry.railroad(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.CONQUESTS) c3cRailroad else railroad

/**
 * [TileEntry.mine] resolved for [era]. Same treatment as [TileEntry.road]'s era-resolved overload.
 */
fun TileEntry.mine(era: Civ3FormatEra): Boolean = if (era == Civ3FormatEra.CONQUESTS) c3cMine else mine

/**
 * [TileEntry.irrigation] resolved for [era]. Same treatment as [TileEntry.road]'s era-resolved
 * overload.
 */
fun TileEntry.irrigation(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.CONQUESTS) c3cIrrigation else irrigation

/**
 * [TileEntry.fortress] resolved for [era]. Same treatment as [TileEntry.road]'s era-resolved
 * overload.
 */
fun TileEntry.fortress(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.CONQUESTS) c3cFortress else fortress

/**
 * [TileEntry.goodyHuts] resolved for [era]. Same treatment as [TileEntry.road]'s era-resolved
 * overload.
 */
fun TileEntry.goodyHuts(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.CONQUESTS) c3cGoodyHuts else goodyHuts

/**
 * [TileEntry.pollution] resolved for [era]. Same treatment as [TileEntry.road]'s era-resolved
 * overload.
 */
fun TileEntry.pollution(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.CONQUESTS) c3cPollution else pollution

/**
 * [TileEntry.barbarianCamp] resolved for [era]. Same treatment as [TileEntry.road]'s era-resolved
 * overload.
 */
fun TileEntry.barbarianCamp(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.CONQUESTS) c3cBarbarianCamp else barbarianCamp

/**
 * This tile's base terrain type, as a `TERR` section index, resolved for [era]: the low nibble of
 * [TileEntry.c3cTerrain] for [Civ3FormatEra.CONQUESTS] files, where real per-tile terrain data
 * lives, or the low nibble of the legacy [TileEntry.terrain] otherwise. `null` when
 * [TileEntry.c3cTerrain] itself is `null`.
 */
fun TileEntry.baseTerrainIndex(era: Civ3FormatEra): Int? =
    if (era == Civ3FormatEra.CONQUESTS) c3cTerrain?.toInt()?.and(0x0F) else terrain.toInt() and 0x0F

/**
 * Resolves [TileEntry.baseTerrainIndex] against [terrains].
 */
fun TileEntry.baseTerrain(terrains: List<TerrEntry>, era: Civ3FormatEra): TerrEntry? =
    baseTerrainIndex(era)?.let { terrains.getOrNull(it) }
