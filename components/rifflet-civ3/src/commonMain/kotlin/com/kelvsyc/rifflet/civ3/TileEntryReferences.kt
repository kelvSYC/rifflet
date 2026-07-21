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
