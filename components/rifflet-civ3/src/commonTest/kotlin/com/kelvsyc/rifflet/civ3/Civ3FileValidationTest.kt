package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun wsizEntry(): WsizEntry = WsizEntry(
    optimalNumberOfCities = 0,
    techRate = 0,
    reserved = ByteString.of(*ByteArray(24)),
    name = "",
    height = 0,
    distanceBetweenCivs = 0,
    numberOfCivs = 0,
    width = 0,
)

private fun exprEntry(): ExprEntry = ExprEntry(name = "", baseHitPoints = 0, retreatBonus = 0)

private fun erasEntry(): ErasEntry = ErasEntry(
    name = "",
    civilopediaEntry = "",
    researcher1 = "",
    researcher2 = "",
    researcher3 = "",
    researcher4 = "",
    researcher5 = "",
    numberOfUsedResearcherNames = 0,
    unknown = ByteString.of(*ByteArray(4)),
)

private fun diffEntry(): DiffEntry = DiffEntry(
    name = "",
    numberOfCitizensBornContent = 0,
    maxGovernmentTransitionTime = 0,
    numberOfAiDefensiveStartingUnits = 0,
    numberOfAiOffensiveStartingUnits = 0,
    extraStartUnit1 = 0,
    extraStartUnit2 = 0,
    additionalFreeSupport = 0,
    unitSupportBonusForEachSettlement = 0,
    attackBonusAgainstBarbarians = 0,
    costFactor = 0,
    percentageOfOptimalCities = 0,
    aiToAiTradeRate = 0,
    corruptionPercentage = 0,
    militaryLaw = 0,
)

private fun fileWithSections(major: Int, sections: List<Civ3Section>): Civ3File =
    Civ3File(Civ3Header(major = major, minor = 0, description = "", title = ""), sections)

private fun wmapEntry(width: Int, height: Int): WmapEntry = WmapEntry(
    resourceIds = emptyList(),
    numberOfContinents = 0,
    height = height,
    distanceBetweenCivs = 0,
    numberOfCivs = 0,
    unknown1 = ByteString.of(*ByteArray(8)),
    width = width,
    unknown2 = ByteString.of(*ByteArray(128)),
    mapSeed = 0,
    flags = 0,
)

private fun tileEntry(city: Short = 0, colony: Short = 0): TileEntry = TileEntry(
    riverConnections = 0,
    border = 0,
    resource = -1,
    textureLocation = 0,
    textureFile = 0,
    unknown = ByteString.of(*ByteArray(2)),
    overlayFlags = 0,
    terrain = 0,
    bonusFlags = 0,
    riverCrossingFlags = 0,
    barbarianTribe = 0,
    colony = colony,
    city = city,
    continent = 0,
    unknown2 = ByteString.of(*ByteArray(1)),
    victoryPointLocation = 0,
    ruin = 0,
    c3cOverlays = ByteString.of(*ByteArray(4)),
    unknown3 = ByteString.of(*ByteArray(1)),
    c3cTerrain = 0,
    unknown4 = ByteString.of(*ByteArray(2)),
    fogOfWar = 0,
    c3cBonuses = ByteString.of(*ByteArray(4)),
    unknown5 = ByteString.of(*ByteArray(2)),
    unknown6 = ByteString.of(*ByteArray(4)),
)

private fun cityEntry(x: Int, y: Int): CityEntry = CityEntry(
    hasWalls = 0,
    hasPalace = 0,
    name = "",
    ownerType = 2,
    buildingIds = emptyList(),
    culture = 0,
    owner = 0,
    size = 0,
    x = x,
    y = y,
    cityLevel = 0,
    borderLevel = 0,
    useAutoName = 0,
)

private fun clnyEntry(x: Int, y: Int): ClnyEntry = ClnyEntry(
    ownerType = 2,
    owner = 0,
    x = x,
    y = y,
    improvementType = 0,
)

private fun terrEntryWithPollutionEffect(pollutionEffect: Int): TerrEntry = TerrEntry(
    numberOfPossibleResources = 0,
    possibleResources = ByteString.of(),
    name = "",
    civilopediaEntry = "",
    irrigationBonus = 0,
    miningBonus = 0,
    roadBonus = 0,
    defenseBonus = 0,
    movementCost = 0,
    food = 0,
    shields = 0,
    commerce = 0,
    workerJobAllowed = -1,
    pollutionEffect = pollutionEffect,
    allowCities = 0,
    allowColonies = 0,
    impassable = 0,
    impassableByWheeled = 0,
    allowAirfields = 0,
    allowForts = 0,
    allowOutposts = 0,
    allowRadarTowers = 0,
    unknown = ByteString.of(*ByteArray(4)),
    landmarkEnabled = 0,
    landmarkFood = 0,
    landmarkShields = 0,
    landmarkCommerce = 0,
    landmarkIrrigationBonus = 0,
    landmarkMiningBonus = 0,
    landmarkRoadBonus = 0,
    landmarkMovementBonus = 0,
    landmarkDefensiveBonus = 0,
    landmarkName = "",
    landmarkCivilopediaEntry = "",
    unknown2 = ByteString.of(*ByteArray(4)),
    terrainFlags = 0,
    diseaseStrength = 0,
)

class Civ3FileValidationTest : FunSpec({

    test("validate() surfaces the seed rule's issue for a file with an invalid pollutionEffect") {
        val file = Civ3File(
            Civ3Header(major = 12, minor = 0, description = "", title = ""),
            listOf(TerrSection(listOf(terrEntryWithPollutionEffect(pollutionEffect = 99)))),
        )

        file.validate() shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TERR,
                0,
                "pollutionEffect",
                "pollutionEffect=99 is not -1, not the base-terrain sentinel (1), and not a valid TERR index (0..<1)",
            ),
        )
    }

    test("validate() returns no issues for a file with no sections") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        file.validate() shouldBe emptyList()
    }

    test("validateWsizCardinality returns no issues for exactly 5 entries") {
        val file = fileWithSections(major = 12, listOf(WsizSection(List(5) { wsizEntry() })))

        validateWsizCardinality(file) shouldBe emptyList()
    }

    test("validateWsizCardinality flags a count other than 5") {
        val file = fileWithSections(major = 12, listOf(WsizSection(List(4) { wsizEntry() })))

        validateWsizCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.WSIZ,
                null,
                "entries",
                "WSIZ has 4 entries; the Rules Editor always produces exactly 5",
            ),
        )
    }

    test("validateWsizCardinality returns no issues when WSIZ is absent") {
        validateWsizCardinality(fileWithSections(major = 12, emptyList())) shouldBe emptyList()
    }

    test("validateExprCardinality returns no issues for exactly 4 entries") {
        val file = fileWithSections(major = 12, listOf(ExprSection(List(4) { exprEntry() })))

        validateExprCardinality(file) shouldBe emptyList()
    }

    test("validateExprCardinality flags a count other than 4") {
        val file = fileWithSections(major = 12, listOf(ExprSection(List(3) { exprEntry() })))

        validateExprCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.EXPR,
                null,
                "entries",
                "EXPR has 3 entries; the Rules Editor always produces exactly 4",
            ),
        )
    }

    test("validateExprCardinality returns no issues when EXPR is absent") {
        validateExprCardinality(fileWithSections(major = 12, emptyList())) shouldBe emptyList()
    }

    test("validateErasCardinality returns no issues for exactly 4 entries") {
        val file = fileWithSections(major = 12, listOf(ErasSection(List(4) { erasEntry() })))

        validateErasCardinality(file) shouldBe emptyList()
    }

    test("validateErasCardinality flags a count other than 4") {
        val file = fileWithSections(major = 12, listOf(ErasSection(List(5) { erasEntry() })))

        validateErasCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.ERAS,
                null,
                "entries",
                "ERAS has 5 entries; the Rules Editor always produces exactly 4",
            ),
        )
    }

    test("validateErasCardinality returns no issues when ERAS is absent") {
        validateErasCardinality(fileWithSections(major = 12, emptyList())) shouldBe emptyList()
    }

    test("validateDiffCardinality returns no issues for exactly 8 entries in Conquests") {
        val file = fileWithSections(major = 12, listOf(DiffSection(List(8) { diffEntry() })))

        validateDiffCardinality(file) shouldBe emptyList()
    }

    test("validateDiffCardinality returns no issues for more than 8 entries in Conquests") {
        val file = fileWithSections(major = 12, listOf(DiffSection(List(9) { diffEntry() })))

        validateDiffCardinality(file) shouldBe emptyList()
    }

    test("validateDiffCardinality flags fewer than 8 entries in Conquests") {
        val file = fileWithSections(major = 12, listOf(DiffSection(List(7) { diffEntry() })))

        validateDiffCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.DIFF,
                null,
                "entries",
                "DIFF has 7 entries; CONQUESTS requires at least 8",
            ),
        )
    }

    test("validateDiffCardinality returns no issues for exactly 6 entries in PTW") {
        val file = fileWithSections(major = 11, listOf(DiffSection(List(6) { diffEntry() })))

        validateDiffCardinality(file) shouldBe emptyList()
    }

    test("validateDiffCardinality flags a count other than 6 in PTW") {
        val file = fileWithSections(major = 11, listOf(DiffSection(List(8) { diffEntry() })))

        validateDiffCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.DIFF,
                null,
                "entries",
                "DIFF has 8 entries; PTW requires exactly 6",
            ),
        )
    }

    test("validateDiffCardinality flags a count other than 6 in vanilla") {
        val file = fileWithSections(major = 3, listOf(DiffSection(List(5) { diffEntry() })))

        validateDiffCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.DIFF,
                null,
                "entries",
                "DIFF has 5 entries; VANILLA requires exactly 6",
            ),
        )
    }

    test("validateDiffCardinality returns no issues when DIFF is absent") {
        validateDiffCardinality(fileWithSections(major = 12, emptyList())) shouldBe emptyList()
    }

    test("validateTileCardinality returns no issues when TILE matches width * height / 2") {
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 90, height = 84))), TileSection(List(3780) { tileEntry() })),
        )

        validateTileCardinality(file) shouldBe emptyList()
    }

    test("validateTileCardinality flags a TILE count that doesn't match width * height / 2") {
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 100, height = 100))), TileSection(List(4999) { tileEntry() })),
        )

        validateTileCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TILE,
                null,
                "entries",
                "TILE has 4999 entries; WMAP width=100, height=100 implies exactly 5000",
            ),
        )
    }

    test("validateTileCardinality returns no issues when WMAP is absent") {
        val file = fileWithSections(major = 12, listOf(TileSection(List(5000) { tileEntry() })))

        validateTileCardinality(file) shouldBe emptyList()
    }

    test("validateTileCardinality returns no issues when TILE is absent") {
        val file = fileWithSections(major = 12, listOf(WmapSection(listOf(wmapEntry(width = 100, height = 100)))))

        validateTileCardinality(file) shouldBe emptyList()
    }

    test("validateCityTileBackReference returns no issues when the TILE back-reference matches") {
        val tiles = List(10) { i -> if (i == 1) tileEntry(city = 0) else tileEntry() }
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TileSection(tiles),
                CitySection(listOf(cityEntry(x = 2, y = 0))),
            ),
        )

        validateCityTileBackReference(file) shouldBe emptyList()
    }

    test("validateCityTileBackReference flags a mismatched TILE back-reference") {
        val tiles = List(10) { tileEntry(city = -1) }
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TileSection(tiles),
                CitySection(listOf(cityEntry(x = 2, y = 0))),
            ),
        )

        validateCityTileBackReference(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.CITY,
                0,
                "x/y",
                "CityEntry at (2, 0) resolves to TILE[1], whose city back-reference is -1, not 0",
            ),
        )
    }

    test("validateCityTileBackReference returns no issues when WMAP is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(TileSection(List(10) { tileEntry() }), CitySection(listOf(cityEntry(x = 2, y = 0)))),
        )

        validateCityTileBackReference(file) shouldBe emptyList()
    }

    test("validateCityTileBackReference returns no issues when TILE is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 10, height = 2))), CitySection(listOf(cityEntry(x = 2, y = 0)))),
        )

        validateCityTileBackReference(file) shouldBe emptyList()
    }

    test("validateCityTileBackReference returns no issues when CITY is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 10, height = 2))), TileSection(List(10) { tileEntry() })),
        )

        validateCityTileBackReference(file) shouldBe emptyList()
    }

    test("validateClnyTileBackReference returns no issues when the TILE back-reference matches") {
        val tiles = List(10) { i -> if (i == 1) tileEntry(colony = 0) else tileEntry() }
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TileSection(tiles),
                ClnySection(listOf(clnyEntry(x = 2, y = 0))),
            ),
        )

        validateClnyTileBackReference(file) shouldBe emptyList()
    }

    test("validateClnyTileBackReference flags a mismatched TILE back-reference") {
        val tiles = List(10) { tileEntry(colony = -1) }
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TileSection(tiles),
                ClnySection(listOf(clnyEntry(x = 2, y = 0))),
            ),
        )

        validateClnyTileBackReference(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.CLNY,
                0,
                "x/y",
                "ClnyEntry at (2, 0) resolves to TILE[1], whose colony back-reference is -1, not 0",
            ),
        )
    }

    test("validateClnyTileBackReference returns no issues when WMAP is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(TileSection(List(10) { tileEntry() }), ClnySection(listOf(clnyEntry(x = 2, y = 0)))),
        )

        validateClnyTileBackReference(file) shouldBe emptyList()
    }

    test("validateClnyTileBackReference returns no issues when TILE is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 10, height = 2))), ClnySection(listOf(clnyEntry(x = 2, y = 0)))),
        )

        validateClnyTileBackReference(file) shouldBe emptyList()
    }

    test("validateClnyTileBackReference returns no issues when CLNY is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 10, height = 2))), TileSection(List(10) { tileEntry() })),
        )

        validateClnyTileBackReference(file) shouldBe emptyList()
    }

    test("validate() surfaces a cardinality rule's issue alongside the seed rule's") {
        val file = Civ3File(
            Civ3Header(major = 12, minor = 0, description = "", title = ""),
            listOf(WsizSection(List(4) { wsizEntry() })),
        )

        file.validate() shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.WSIZ,
                null,
                "entries",
                "WSIZ has 4 entries; the Rules Editor always produces exactly 5",
            ),
        )
    }
})
