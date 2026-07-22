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
 * Resolves [TileEntry.continent] against [continents]. Likely a `CONT` section index (naming
 * convention only); not confirmed by either reverse-engineering source.
 */
fun TileEntry.continentCont(continents: List<ContEntry>): ContEntry? = continents.getOrNull(continent.toInt())

/**
 * Resolves [TileEntry.city] against [cities].
 */
fun TileEntry.cityCity(cities: List<CityEntry>): CityEntry? = cities.getOrNull(city.toInt())

/**
 * [TileEntry.bonusGrassland] resolved for [era]: reads [TileEntry.c3cBonusGrassland] for
 * [Civ3FormatEra.CONQUESTS] files, where real per-tile bonus-terrain data lives, or the legacy
 * [TileEntry.bonusGrassland] otherwise. The [Civ3FormatEra.PTW] case is assumed to match
 * [Civ3FormatEra.VANILLA], not yet confirmed against real PTW data.
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
