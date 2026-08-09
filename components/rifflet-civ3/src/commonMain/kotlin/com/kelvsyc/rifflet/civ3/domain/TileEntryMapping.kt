package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.*

/**
 * Converts a parsed `TILE` section to its domain-layer form.
 *
 * [colonies]/[cities]/[races] are the already domain-converted `CLNY`/`CITY`/`RACE` lists;
 * [goods]/[continents]/[terrains] stay wire types (`GOOD`/`CONT`/`TERR` don't have domain types
 * yet). The caller is responsible for supplying the right lists for this file — this file's own
 * sections converted via their own `toDomain()`, or externally-sourced standard lists, as
 * appropriate.
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
    continents: List<ContEntry>,
    terrains: List<TerrEntry>,
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
        baseTerrain = entry.baseTerrain(terrains, era),
        overlayTerrain = entry.overlayTerrain(terrains, era),
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
        continent = entry.continentCont(continents),
        fogOfWar = entry.fogOfWar.toInt() != 0,
        border = entry.border,
    )
}
