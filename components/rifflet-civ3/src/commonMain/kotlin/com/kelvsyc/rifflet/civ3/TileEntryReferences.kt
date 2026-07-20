package com.kelvsyc.rifflet.civ3

/**
 * Resolves [TileEntry.resource] against [goods]. Likely a `GOOD` section index (naming
 * convention only); not confirmed by either cross-referenced source.
 */
fun TileEntry.resourceGood(goods: List<GoodEntry>): GoodEntry? = goods.getOrNull(resource)

/**
 * Resolves [TileEntry.colony] against [colonies]. Confirmed a genuine `CLNY` section index by
 * real map-editor data (2026-07-20) — see [TileEntry.colony]'s own KDoc.
 */
fun TileEntry.colonyClny(colonies: List<ClnyEntry>): ClnyEntry? = colonies.getOrNull(colony.toInt())

/**
 * Resolves [TileEntry.continent] against [continents]. Likely a `CONT` section index (naming
 * convention only); not confirmed by either cross-referenced source.
 */
fun TileEntry.continentCont(continents: List<ContEntry>): ContEntry? = continents.getOrNull(continent.toInt())

/**
 * Resolves [TileEntry.city] against [cities]. Confirmed a genuine `CITY` section index by real
 * map-editor data (2026-07-20) — see [TileEntry.city]'s own KDoc.
 */
fun TileEntry.cityCity(cities: List<CityEntry>): CityEntry? = cities.getOrNull(city.toInt())
