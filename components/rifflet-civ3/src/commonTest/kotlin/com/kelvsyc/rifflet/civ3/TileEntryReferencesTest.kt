package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validTileEntry(
    resource: Int = 0,
    colony: Short = 0,
    continent: Short = 0,
    city: Short = 0,
    bonusFlags: Byte = 0,
    c3cBonuses: ByteString = ByteString.of(0, 0, 0, 0),
    terrain: Byte = 0,
    c3cTerrain: Byte = 0,
    overlayFlags: Byte = 0,
    c3cOverlays: ByteString = ByteString.of(0, 0, 0, 0),
): TileEntry = TileEntry(
    riverConnections = 0.toByte(),
    border = 0.toByte(),
    resource = resource,
    textureLocation = 0.toByte(),
    textureFile = 0.toByte(),
    unknown = ByteString.of(0, 0),
    overlayFlags = overlayFlags,
    terrain = terrain,
    bonusFlags = bonusFlags,
    riverCrossingFlags = 0.toByte(),
    barbarianTribe = 0.toShort(),
    colony = colony,
    city = city,
    continent = continent,
    unknown2 = ByteString.of(0),
    victoryPointLocation = 0.toShort(),
    ruin = 0,
    c3cOverlays = c3cOverlays,
    unknown3 = ByteString.of(0),
    c3cTerrain = c3cTerrain,
    unknown4 = ByteString.of(0, 0),
    fogOfWar = 0.toShort(),
    c3cBonuses = c3cBonuses,
    unknown5 = ByteString.of(0, 0),
    unknown6 = ByteString.of(0, 0, 0, 0),
)

private fun validTerrEntryForTileTest(name: String = ""): TerrEntry = TerrEntry(
    numberOfPossibleResources = 0,
    possibleResources = ByteString.of(),
    name = name,
    civilopediaEntry = "",
    terraformBonuses = TerrTerraformBonuses(irrigationBonus = 0, miningBonus = 0, roadBonus = 0),
    defenseBonus = 0,
    movementCost = 0,
    tileValues = TerrTileValues(food = 0, shields = 0, commerce = 0),
    workerJobAllowed = -1,
    pollutionEffect = -1,
    allowances = TerrAllowances(
        allowCities = 0,
        allowColonies = 0,
        impassable = 0,
        impassableByWheeled = 0,
        allowAirfields = 0,
        allowForts = 0,
        allowOutposts = 0,
        allowRadarTowers = 0,
    ),
    unknown = ByteString.of(0, 0, 0, 0),
    landmark = null,
    unknown2 = ByteString.of(0, 0, 0, 0),
    terrainFlags = 0,
    diseaseStrength = 0,
)

private fun validGoodEntry(): GoodEntry = GoodEntry(
    name = "",
    civilopediaEntry = "",
    type = GoodResourceType.BONUS,
    appearanceRatio = 0,
    disappearanceProbability = 0,
    icon = 0,
    prerequisite = 0,
    foodBonus = 0,
    shieldsBonus = 0,
    commerceBonus = 0,
)

private fun validClnyEntry(): ClnyEntry = ClnyEntry(ownerType = 0, owner = 0, x = 0, y = 0, improvementType = ClnyImprovementType.COLONY)

private fun validContEntry(): ContEntry = ContEntry(type = 0, numberOfTiles = 0)

private fun validCityEntry(): CityEntry = CityEntry(
    hasWalls = 0,
    hasPalace = 0,
    name = "",
    ownerType = 0,
    buildingIds = emptyList(),
    culture = 0,
    owner = 0,
    size = 0,
    x = 0,
    y = 0,
    cityLevel = 0,
    borderLevel = 0,
    useAutoName = 0,
)

class TileEntryReferencesTest : FunSpec({

    test("resourceGood resolves against the GOOD list") {
        val good = validGoodEntry()
        validTileEntry(resource = 0).resourceGood(listOf(good)) shouldBe good
        validTileEntry(resource = 5).resourceGood(emptyList()) shouldBe null
    }

    test("colonyClny resolves against the CLNY list") {
        val clny = validClnyEntry()
        validTileEntry(colony = 0).colonyClny(listOf(clny)) shouldBe clny
        validTileEntry(colony = 5).colonyClny(emptyList()) shouldBe null
    }

    test("continentCont resolves against the CONT list") {
        val cont = validContEntry()
        validTileEntry(continent = 0).continentCont(listOf(cont)) shouldBe cont
        validTileEntry(continent = 5).continentCont(emptyList()) shouldBe null
    }

    test("cityCity resolves against the CITY list") {
        val city = validCityEntry()
        validTileEntry(city = 0).cityCity(listOf(city)) shouldBe city
        validTileEntry(city = 5).cityCity(emptyList()) shouldBe null
    }

    test("bonusGrassland(era) reads the legacy field for VANILLA/PTW and c3cBonuses for CONQUESTS") {
        val entry = validTileEntry(bonusFlags = 1, c3cBonuses = ByteString.of(0, 0, 0, 0))
        entry.bonusGrassland(Civ3FormatEra.VANILLA) shouldBe true
        entry.bonusGrassland(Civ3FormatEra.PTW) shouldBe true
        entry.bonusGrassland(Civ3FormatEra.CONQUESTS) shouldBe false

        val c3cEntry = validTileEntry(bonusFlags = 0, c3cBonuses = ByteString.of(1, 0, 0, 0))
        c3cEntry.bonusGrassland(Civ3FormatEra.CONQUESTS) shouldBe true
        c3cEntry.bonusGrassland(Civ3FormatEra.VANILLA) shouldBe false
    }

    test("snowCappedMountains(era) reads the legacy field for VANILLA/PTW and c3cBonuses for CONQUESTS") {
        val entry = validTileEntry(bonusFlags = (1 shl 4).toByte())
        entry.snowCappedMountains(Civ3FormatEra.PTW) shouldBe true
        entry.snowCappedMountains(Civ3FormatEra.CONQUESTS) shouldBe false

        val c3cEntry = validTileEntry(c3cBonuses = ByteString.of((1 shl 4).toByte(), 0, 0, 0))
        c3cEntry.snowCappedMountains(Civ3FormatEra.CONQUESTS) shouldBe true
    }

    test("pineForest(era) reads the legacy field for VANILLA/PTW and c3cBonuses for CONQUESTS") {
        val entry = validTileEntry(bonusFlags = (1 shl 5).toByte())
        entry.pineForest(Civ3FormatEra.VANILLA) shouldBe true
        entry.pineForest(Civ3FormatEra.CONQUESTS) shouldBe false

        val c3cEntry = validTileEntry(c3cBonuses = ByteString.of((1 shl 5).toByte(), 0, 0, 0))
        c3cEntry.pineForest(Civ3FormatEra.CONQUESTS) shouldBe true
    }

    test("road(era) reads the legacy field for VANILLA/PTW and c3cOverlays for CONQUESTS") {
        val entry = validTileEntry(overlayFlags = 1, c3cOverlays = ByteString.of(0, 0, 0, 0))
        entry.road(Civ3FormatEra.VANILLA) shouldBe true
        entry.road(Civ3FormatEra.PTW) shouldBe true
        entry.road(Civ3FormatEra.CONQUESTS) shouldBe false

        val c3cEntry = validTileEntry(overlayFlags = 0, c3cOverlays = ByteString.of(1, 0, 0, 0))
        c3cEntry.road(Civ3FormatEra.CONQUESTS) shouldBe true
        c3cEntry.road(Civ3FormatEra.VANILLA) shouldBe false
    }

    test("railroad(era) reads the legacy field for VANILLA/PTW and c3cOverlays for CONQUESTS") {
        val entry = validTileEntry(overlayFlags = (1 shl 1).toByte())
        entry.railroad(Civ3FormatEra.PTW) shouldBe true
        entry.railroad(Civ3FormatEra.CONQUESTS) shouldBe false

        val c3cEntry = validTileEntry(c3cOverlays = ByteString.of((1 shl 1).toByte(), 0, 0, 0))
        c3cEntry.railroad(Civ3FormatEra.CONQUESTS) shouldBe true
    }

    test("mine(era) reads the legacy field for VANILLA/PTW and c3cOverlays for CONQUESTS") {
        val entry = validTileEntry(overlayFlags = (1 shl 2).toByte())
        entry.mine(Civ3FormatEra.VANILLA) shouldBe true
        entry.mine(Civ3FormatEra.CONQUESTS) shouldBe false

        val c3cEntry = validTileEntry(c3cOverlays = ByteString.of((1 shl 2).toByte(), 0, 0, 0))
        c3cEntry.mine(Civ3FormatEra.CONQUESTS) shouldBe true
    }

    test("irrigation(era) reads the legacy field for VANILLA/PTW and c3cOverlays for CONQUESTS") {
        val entry = validTileEntry(overlayFlags = (1 shl 3).toByte())
        entry.irrigation(Civ3FormatEra.PTW) shouldBe true
        entry.irrigation(Civ3FormatEra.CONQUESTS) shouldBe false

        val c3cEntry = validTileEntry(c3cOverlays = ByteString.of((1 shl 3).toByte(), 0, 0, 0))
        c3cEntry.irrigation(Civ3FormatEra.CONQUESTS) shouldBe true
    }

    test("fortress(era) reads the legacy field for VANILLA/PTW and c3cOverlays for CONQUESTS") {
        val entry = validTileEntry(overlayFlags = (1 shl 4).toByte())
        entry.fortress(Civ3FormatEra.VANILLA) shouldBe true
        entry.fortress(Civ3FormatEra.CONQUESTS) shouldBe false

        val c3cEntry = validTileEntry(c3cOverlays = ByteString.of((1 shl 4).toByte(), 0, 0, 0))
        c3cEntry.fortress(Civ3FormatEra.CONQUESTS) shouldBe true
    }

    test("goodyHuts(era) reads the legacy field for VANILLA/PTW and c3cOverlays for CONQUESTS") {
        val entry = validTileEntry(overlayFlags = (1 shl 5).toByte())
        entry.goodyHuts(Civ3FormatEra.PTW) shouldBe true
        entry.goodyHuts(Civ3FormatEra.CONQUESTS) shouldBe false

        val c3cEntry = validTileEntry(c3cOverlays = ByteString.of((1 shl 5).toByte(), 0, 0, 0))
        c3cEntry.goodyHuts(Civ3FormatEra.CONQUESTS) shouldBe true
    }

    test("pollution(era) reads the legacy field for VANILLA/PTW and c3cOverlays for CONQUESTS") {
        val entry = validTileEntry(overlayFlags = (1 shl 6).toByte())
        entry.pollution(Civ3FormatEra.VANILLA) shouldBe true
        entry.pollution(Civ3FormatEra.CONQUESTS) shouldBe false

        val c3cEntry = validTileEntry(c3cOverlays = ByteString.of((1 shl 6).toByte(), 0, 0, 0))
        c3cEntry.pollution(Civ3FormatEra.CONQUESTS) shouldBe true
    }

    test("barbarianCamp(era) reads the legacy field for VANILLA/PTW and c3cOverlays for CONQUESTS") {
        val entry = validTileEntry(overlayFlags = (1 shl 7).toByte())
        entry.barbarianCamp(Civ3FormatEra.PTW) shouldBe true
        entry.barbarianCamp(Civ3FormatEra.CONQUESTS) shouldBe false

        val c3cEntry = validTileEntry(c3cOverlays = ByteString.of((1 shl 7).toByte(), 0, 0, 0))
        c3cEntry.barbarianCamp(Civ3FormatEra.CONQUESTS) shouldBe true
    }

    test("baseTerrainIndex(era) reads the low nibble of the legacy terrain field for VANILLA/PTW") {
        val entry = validTileEntry(terrain = 0x25.toByte(), c3cTerrain = 0x13.toByte())
        entry.baseTerrainIndex(Civ3FormatEra.VANILLA) shouldBe 5
        entry.baseTerrainIndex(Civ3FormatEra.PTW) shouldBe 5
    }

    test("baseTerrainIndex(era) reads the low nibble of c3cTerrain for CONQUESTS") {
        val entry = validTileEntry(terrain = 0x25.toByte(), c3cTerrain = 0x13.toByte())
        entry.baseTerrainIndex(Civ3FormatEra.CONQUESTS) shouldBe 3
    }

    test("baseTerrain(terrains, era) resolves the decoded index against the TERR list") {
        val desert = validTerrEntryForTileTest(name = "Desert")
        val plains = validTerrEntryForTileTest(name = "Plains")
        val entry = validTileEntry(terrain = 0x01.toByte())

        entry.baseTerrain(listOf(desert, plains), Civ3FormatEra.VANILLA) shouldBe plains
    }

    test("baseTerrain(terrains, era) returns null for an out-of-range index") {
        val entry = validTileEntry(terrain = 0x09.toByte())

        entry.baseTerrain(listOf(validTerrEntryForTileTest(name = "Desert")), Civ3FormatEra.VANILLA) shouldBe null
    }
})
