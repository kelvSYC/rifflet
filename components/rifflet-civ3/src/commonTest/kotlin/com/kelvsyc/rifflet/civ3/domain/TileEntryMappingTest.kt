package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.ContEntry
import com.kelvsyc.rifflet.civ3.ContType
import com.kelvsyc.rifflet.civ3.GoodEntry
import com.kelvsyc.rifflet.civ3.GoodResourceType
import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.RaceCultureGroup
import com.kelvsyc.rifflet.civ3.RaceLeader
import com.kelvsyc.rifflet.civ3.TerrAllowances
import com.kelvsyc.rifflet.civ3.TerrEntry
import com.kelvsyc.rifflet.civ3.TerrTerraformBonuses
import com.kelvsyc.rifflet.civ3.TerrTileValues
import com.kelvsyc.rifflet.civ3.TileEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun tileEntry(
    riverConnections: Byte = 0,
    riverCrossingFlags: Byte = 0,
    overlayFlags: Byte = 0,
    c3cOverlays: ByteString = ByteString.of(0, 0, 0, 0),
    terrain: Byte = 0,
    c3cTerrain: Byte? = null,
    bonusFlags: Byte = 0,
    c3cBonuses: ByteString = ByteString.of(0, 0, 0, 0),
    resource: Int = -1,
    textureLocation: Byte = 0,
    textureFile: Byte = 0,
    barbarianTribe: Short = -1,
    colony: Short = -1,
    city: Short = -1,
    continent: Short = -1,
    victoryPointLocation: Short = -1,
    ruin: Int = 0,
    fogOfWar: Short = 0,
    border: Byte = 0,
): TileEntry = TileEntry(
    riverConnections = riverConnections,
    border = border,
    resource = resource,
    textureLocation = textureLocation,
    textureFile = textureFile,
    unknown = ByteString.of(0, 0),
    overlayFlags = overlayFlags,
    terrain = terrain,
    bonusFlags = bonusFlags,
    riverCrossingFlags = riverCrossingFlags,
    barbarianTribe = barbarianTribe,
    colony = colony,
    city = city,
    continent = continent,
    unknown2 = ByteString.of(0),
    victoryPointLocation = victoryPointLocation,
    ruin = ruin,
    c3cOverlays = c3cOverlays,
    unknown3 = ByteString.of(0),
    c3cTerrain = c3cTerrain,
    unknown4 = ByteString.of(0, 0),
    fogOfWar = fogOfWar,
    c3cBonuses = c3cBonuses,
    unknown5 = ByteString.of(0, 0),
    unknown6 = ByteString.of(0, 0, 0, 0),
)

private fun terrEntry(name: String): TerrEntry = TerrEntry(
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
        allowCities = 0, allowColonies = 0, impassable = 0, impassableByWheeled = 0,
        allowAirfields = 0, allowForts = 0, allowOutposts = 0, allowRadarTowers = 0,
    ),
    unknown = ByteString.of(0, 0, 0, 0),
    landmark = null,
    unknown2 = ByteString.of(0, 0, 0, 0),
    terrainFlags = 0,
    diseaseStrength = 0,
)

private fun goodEntry(name: String): GoodEntry = GoodEntry(
    name = name, civilopediaEntry = "", type = GoodResourceType.BONUS, appearanceRatio = 0,
    disappearanceProbability = 0, icon = 0, prerequisite = 0, foodBonus = 0, shieldsBonus = 0,
    commerceBonus = 0,
)

private fun race(name: String, cityNames: List<String> = emptyList()): Race = Race(
    name = name, civilopediaEntry = "", adjective = "", noun = "",
    leader = RaceLeader(name = "", title = "", gender = Gender.MALE),
    cultureGroup = RaceCultureGroup.NONE, civilizationGender = Gender.MALE, cityNames = cityNames,
)

class TileEntryMappingTest : FunSpec({

    test("toDomain maps rivers, texture, and border straight across") {
        val entry = tileEntry(
            riverConnections = 0b0101, // north, east
            riverCrossingFlags = 0b00000011, // crossingN, crossingNe
            textureLocation = 3, textureFile = 5, border = 0x2A,
        )

        val tile = listOf(entry).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()

        tile.rivers shouldBe TileRivers(north = true, east = true, crossingN = true, crossingNe = true)
        tile.textureLocation shouldBe 3
        tile.textureFile shouldBe 5
        tile.border shouldBe 0x2A.toByte()
    }

    test("toDomain merges improvements for VANILLA/PTW from the legacy overlayFlags field") {
        val entry = tileEntry(overlayFlags = 0b00000001) // road

        val tile = listOf(entry).toDomain(Civ3FormatEra.PTW, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()

        tile.improvements shouldBe TileImprovements(road = true)
    }

    test("toDomain merges improvements for CONQUESTS from c3cOverlays, including the 4 consumed-on-build ones") {
        val entry = tileEntry(
            c3cOverlays = ByteString.of(
                0b00000010, // railroad, bit 1
                0,
                0,
                0x80.toByte(), // outpost, bit 31 (local bit 7 of byte index 3)
            ),
        )

        val tile = listOf(entry).toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()

        tile.improvements.railroad shouldBe true
        tile.improvements.outpost shouldBe true
        tile.improvements.road shouldBe false
    }

    test("toDomain resolves base and overlay terrain against the TERR list") {
        val desert = terrEntry("Desert")
        val hills = terrEntry("Hills")
        val entry = tileEntry(terrain = 0x10) // base=0 (Desert), overlay=1 (Hills)

        val tile = listOf(entry).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList(), emptyList(), emptyList(), listOf(desert, hills), emptyList()).single()

        tile.baseTerrain shouldBe desert
        tile.overlayTerrain shouldBe hills
    }

    test("toDomain resolves resource against the GOOD list, null when absent") {
        val iron = goodEntry("Iron")

        val withResource = listOf(tileEntry(resource = 0)).toDomain(Civ3FormatEra.VANILLA, listOf(iron), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()
        val withoutResource = listOf(tileEntry(resource = -1)).toDomain(Civ3FormatEra.VANILLA, listOf(iron), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()

        withResource.resource shouldBe iron
        withoutResource.resource shouldBe null
    }

    test("toDomain resolves barbarianTribe against the barbarian placeholder's cityNames") {
        val barbarianRace = race("Barbarians", cityNames = listOf("Attila's Camp", "Genghis's Horde"))

        val tile = listOf(tileEntry(barbarianTribe = 1)).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), listOf(barbarianRace)).single()

        tile.barbarianTribe shouldBe "Genghis's Horde"
    }

    test("toDomain resolves barbarianTribe to null when the index is out of range or races is empty") {
        val barbarianRace = race("Barbarians", cityNames = listOf("Attila's Camp"))

        val outOfRange = listOf(tileEntry(barbarianTribe = 5)).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), listOf(barbarianRace)).single()
        val noRaces = listOf(tileEntry(barbarianTribe = 0)).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()

        outOfRange.barbarianTribe shouldBe null
        noRaces.barbarianTribe shouldBe null
    }

    test("toDomain converts victoryPointLocation, ruin, and fogOfWar sentinels to Booleans") {
        val vpl = listOf(tileEntry(victoryPointLocation = 0)).toDomain(Civ3FormatEra.PTW, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()
        val notVpl = listOf(tileEntry(victoryPointLocation = -1)).toDomain(Civ3FormatEra.PTW, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()
        val ruined = listOf(tileEntry(ruin = 1)).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()
        val fogged = listOf(tileEntry(fogOfWar = 0x8000.toShort())).toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()

        vpl.isVictoryPointLocation shouldBe true
        notVpl.isVictoryPointLocation shouldBe false
        ruined.ruins shouldBe true
        fogged.fogOfWar shouldBe true
    }

    test("toDomain resolves continent against the CONT list") {
        val plains = ContEntry(type = ContType.LAND, numberOfTiles = 10)

        val tile = listOf(tileEntry(continent = 0)).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList(), emptyList(), listOf(plains), emptyList(), emptyList()).single()

        tile.continent shouldBe plains
    }

    test("toDomain().toWire() round-trips for a VANILLA-era entry using legacy fields") {
        val desert = terrEntry("Desert")
        val hills = terrEntry("Hills")
        val entries = listOf(
            tileEntry(
                riverConnections = 0b0001,
                riverCrossingFlags = 0b00000001,
                overlayFlags = 0b00000001, // road
                bonusFlags = 0b00000001, // bonusGrassland
                terrain = 0x10, // base=0 (Desert), overlay=1 (Hills)
                textureLocation = 2,
                textureFile = 4,
                victoryPointLocation = -1,
                border = 0x0F,
            ),
        )

        val roundTripped = entries.toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList(), emptyList(), emptyList(), listOf(desert, hills), emptyList())
            .toWire(Civ3FormatEra.VANILLA, emptyList(), emptyList(), emptyList(), emptyList(), listOf(desert, hills), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips for a CONQUESTS-era entry using c3c fields") {
        val plains = terrEntry("Plains")
        val forest = terrEntry("Forest")
        val entries = listOf(
            tileEntry(
                c3cOverlays = ByteString.of(0b00000010, 0, 0, 0x80.toByte()), // railroad + outpost
                c3cBonuses = ByteString.of(0b00100000, 0, 0, 0), // pineForest
                c3cTerrain = 0x10, // base=0 (Plains), overlay=1 (Forest)
                victoryPointLocation = 0,
                fogOfWar = 0x8000.toShort(),
                ruin = 1,
            ),
        )

        val roundTripped = entries.toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList(), listOf(plains, forest), emptyList())
            .toWire(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList(), listOf(plains, forest), emptyList())

        roundTripped shouldBe entries
    }

    test("toWire throws on a dangling resource/baseTerrain/continent reference") {
        val tileWithResource = Tile(resource = goodEntry("Outsider"))
        val tileWithTerrain = Tile(baseTerrain = terrEntry("Outsider"))
        val tileWithContinent = Tile(continent = ContEntry(type = ContType.LAND, numberOfTiles = 1))

        shouldThrow<IllegalArgumentException> {
            listOf(tileWithResource).toWire(Civ3FormatEra.VANILLA, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        }
        shouldThrow<IllegalArgumentException> {
            listOf(tileWithTerrain).toWire(Civ3FormatEra.VANILLA, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        }
        shouldThrow<IllegalArgumentException> {
            listOf(tileWithContinent).toWire(Civ3FormatEra.VANILLA, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        }
    }

    test("toWire writes -1 for a barbarianTribe name not found, without throwing") {
        val tile = Tile(barbarianTribe = "Nonexistent Tribe")

        val wire = listOf(tile).toWire(Civ3FormatEra.VANILLA, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()

        wire.barbarianTribe shouldBe (-1).toShort()
    }

    test("toWire writes 0 for a null baseTerrain/overlayTerrain, not -1") {
        val tile = Tile()

        val wire = listOf(tile).toWire(Civ3FormatEra.VANILLA, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()

        wire.terrain shouldBe 0.toByte()
    }
})
