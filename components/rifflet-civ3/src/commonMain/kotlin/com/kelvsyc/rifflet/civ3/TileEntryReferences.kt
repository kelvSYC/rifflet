package com.kelvsyc.rifflet.civ3

/**
 * Resolves [TileEntry.resource] against [goods]. Likely a `GOOD` section index (naming
 * convention only); not confirmed by either cross-referenced source.
 */
fun TileEntry.resourceGood(goods: List<GoodEntry>): GoodEntry? = goods.getOrNull(resource)

/**
 * Resolves [TileEntry.colony] against [colonies]. Likely a `CLNY` section index (naming
 * convention only); not confirmed by either cross-referenced source.
 */
fun TileEntry.colonyClny(colonies: List<ClnyEntry>): ClnyEntry? = colonies.getOrNull(colony.toInt())

/**
 * Resolves [TileEntry.continent] against [continents]. Likely a `CONT` section index (naming
 * convention only); not confirmed by either cross-referenced source.
 */
fun TileEntry.continentCont(continents: List<ContEntry>): ContEntry? = continents.getOrNull(continent.toInt())

/**
 * Resolves [TileEntry.city] against [cities]. Likely a reference to a placed city (naming
 * convention only); not confirmed by either cross-referenced source.
 */
fun TileEntry.cityCity(cities: List<CityEntry>): CityEntry? = cities.getOrNull(city.toInt())
