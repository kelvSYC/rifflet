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

private fun wchrEntry(worldSize: Int = 0): WchrEntry = WchrEntry(
    selectedClimate = Climate.NORMAL,
    actualClimate = Climate.NORMAL,
    selectedBarbarianActivity = BarbarianActivity.NO_BARBARIANS,
    actualBarbarianActivity = BarbarianActivity.NO_BARBARIANS,
    selectedLandform = Landform.CONTINENTS,
    actualLandform = Landform.CONTINENTS,
    selectedOceanCoverage = OceanCoverage.SEVENTY_PERCENT,
    actualOceanCoverage = OceanCoverage.SEVENTY_PERCENT,
    selectedTemperature = Temperature.TEMPERATE,
    actualTemperature = Temperature.TEMPERATE,
    selectedAge = Age.FOUR_BILLION_YEARS,
    actualAge = Age.FOUR_BILLION_YEARS,
    worldSize = worldSize,
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

private fun wmapEntry(width: Int, height: Int, flags: Int = 0): WmapEntry = WmapEntry(
    resourceIds = emptyList(),
    numberOfContinents = 0,
    height = height,
    distanceBetweenCivs = 0,
    numberOfCivs = 0,
    unknown1 = ByteString.of(*ByteArray(8)),
    width = width,
    unknown2 = ByteString.of(*ByteArray(128)),
    mapSeed = 0,
    flags = flags,
)

private fun tileEntry(
    city: Short = 0,
    colony: Short = 0,
    terrain: Byte = 0,
    c3cTerrain: Byte = 0,
    fortress: Boolean = false,
    continent: Int = 0,
): TileEntry = TileEntry(
    riverConnections = 0,
    border = 0,
    resource = -1,
    textureLocation = 0,
    textureFile = 0,
    unknown = ByteString.of(*ByteArray(2)),
    overlayFlags = if (fortress) (1 shl 4).toByte() else 0,
    terrain = terrain,
    bonusFlags = 0,
    riverCrossingFlags = 0,
    barbarianTribe = 0,
    colony = colony,
    city = city,
    continent = continent.toShort(),
    unknown2 = ByteString.of(*ByteArray(1)),
    victoryPointLocation = 0,
    ruin = 0,
    c3cOverlays = if (fortress) ByteString.of((1 shl 4).toByte(), 0, 0, 0) else ByteString.of(*ByteArray(4)),
    unknown3 = ByteString.of(*ByteArray(1)),
    c3cTerrain = c3cTerrain,
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

private fun clnyEntry(x: Int, y: Int, improvementType: ClnyImprovementType = ClnyImprovementType.COLONY): ClnyEntry = ClnyEntry(
    ownerType = 2,
    owner = 0,
    x = x,
    y = y,
    improvementType = improvementType,
)

private fun unitEntry(x: Int, y: Int, unitType: Int = 0): UnitEntry = UnitEntry(
    legacyName = "",
    ownerType = 2,
    experienceLevel = 0,
    owner = 0,
    unitType = unitType,
    aiStrategy = 0,
    x = x,
    y = y,
    ptwName = "",
    useCivilizationKing = 0,
)

private fun prtoEntry(wheeledAbility: Boolean = false): PrtoEntry = PrtoEntry(
    unitStatistics = PrtoUnitStatistics(
        zoneOfControl = 0, bombardStrength = 0, bombardRange = 0, capacity = 0, shieldCost = 0,
        defense = 0, attack = 0, operationalRange = 0, populationCost = 0, rateOfFire = 0,
        movement = 0, upgradeTo = -1, hpBonus = 0, bombardEffects = 0, requireSupport = 0,
        createCraters = 0, workerStrength = 0f, airDefense = 0,
    ),
    name = "",
    civilopediaEntry = "",
    iconIndex = 0,
    required = -1,
    requiredResource1 = -1,
    requiredResource2 = -1,
    requiredResource3 = -1,
    abilities = if (wheeledAbility) 1 shl 0 else 0,
    aiStrategies = 0,
    availableTo = 0,
    flags2 = ByteString.of(*ByteArray(8)),
    type = PrtoDomain.LAND,
    otherStrategy = -1,
    standardOrders = 0,
    specialActions = 0,
    workerActions = 0,
    airMissions = 0,
    flags4 = ByteString.of(*ByteArray(4)),
    ignoreMovementCost = ByteString.of(),
    unknown = ByteString.of(*ByteArray(16)),
    enslaveResults = -1,
    unknown2 = ByteString.of(*ByteArray(4)),
    stealthTargetUnitTypes = emptyList(),
    unknown3 = ByteString.of(*ByteArray(8)),
    unknown4 = ByteString.of(*ByteArray(4)),
)

private fun ctznEntry(defaultCitizen: Int = 0, prerequisite: Int = -1): CtznEntry = CtznEntry(
    defaultCitizen = defaultCitizen,
    singularName = "",
    civilopediaEntry = "",
    pluralName = "",
    prerequisite = prerequisite,
    luxuries = 0,
    research = 0,
    taxes = 0,
    corruption = 0,
    construction = 0,
)

private fun contEntry(type: ContType = ContType.LAND, numberOfTiles: Int = 0): ContEntry = ContEntry(
    type = type,
    numberOfTiles = numberOfTiles,
)

private fun terrEntry(
    name: String = "",
    allowCities: Byte = 0,
    allowColonies: Byte = 0,
    allowAirfields: Byte? = 0,
    allowRadarTowers: Byte? = 0,
    allowOutposts: Byte? = 0,
    allowForts: Byte? = 0,
    impassable: Byte? = 0,
    impassableByWheeled: Byte? = 0,
): TerrEntry = TerrEntry(
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
        allowCities = allowCities,
        allowColonies = allowColonies,
        impassable = impassable,
        impassableByWheeled = impassableByWheeled,
        allowAirfields = allowAirfields,
        allowForts = allowForts,
        allowOutposts = allowOutposts,
        allowRadarTowers = allowRadarTowers,
    ),
    unknown = ByteString.of(*ByteArray(4)),
    landmark = null,
    unknown2 = ByteString.of(*ByteArray(4)),
    terrainFlags = 0,
    diseaseStrength = 0,
)

private fun tfrmEntry(): TfrmEntry = TfrmEntry(
    name = "",
    civilopediaEntry = "",
    turnsToComplete = 0,
    required = -1,
    requiredResource1 = -1,
    requiredResource2 = -1,
    order = "",
)

private fun ruleEntryWithSpaceshipPartCount(count: Int): RuleEntry = RuleEntry(
    citySizeLevels = RuleCitySizeLevels(
        citySizeLevel1Name = "",
        citySizeLevel2Name = "",
        citySizeLevel3Name = "",
        maximumLevel1CitySize = 6,
        maximumLevel2CitySize = 12,
    ),
    spaceshipPartQuantities = List(count) { 1 },
    defaultUnits = RuleDefaultUnits(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1),
    citiesNeededToSupportAnArmy = 0,
    chanceOfRioting = 0,
    turnPenaltyForEachDraftedCitizen = 0,
    shieldCostPerGold = 0,
    defensiveBonuses = RuleDefensiveBonuses(0, 0, 0, 0, 0, 0, 0, 0),
    citizensAffectedByEachHappyFace = 0,
    unknown = ByteString.of(*ByteArray(8)),
    forestValueInShields = 0,
    shieldValueInGold = 0,
    citizenValueInShields = 0,
    defaultDifficultyLevel = -1,
    defaultMoneyResource = -1,
    chanceToInterceptEnemyAirMissions = 0,
    chanceToInterceptEnemyStealthMissions = 0,
    startingTreasury = 0,
    unknown2 = ByteString.of(*ByteArray(4)),
    foodConsumptionPerCitizen = 0,
    turnPenaltyForEachHurrySacrifice = 0,
    movementAlongRoads = 0,
    minimumPopulationForWeLoveTheKing = 0,
    unknown3 = ByteString.of(*ByteArray(4)),
    culture = RuleCulture(emptyList(), 0, 0),
    technology = RuleTechnology(0, 0, 0),
    goldenAgeDuration = 0,
    upgradeCost = 0,
)

private fun bldgEntry(spaceshipPart: Int): BldgEntry = BldgEntry(
    description = "",
    name = "",
    civilopediaEntry = "",
    doublesHappiness = 0,
    gainInEveryCity = 0,
    gainInEveryCityOnContinent = 0,
    requirements = BldgRequirements(requiredBuilding = -1, requiredGovernment = -1, requiredAdvance = -1),
    cost = 0,
    culture = 0,
    combatValues = BldgCombatValues(
        bombardDefense = 0, navalBombardDefense = 0, defenseBonus = 0, airPower = 0, navalPower = 0,
    ),
    navalDefenseBonus = 0,
    maintenanceCost = 0,
    happiness = BldgHappiness(
        contentFacesAllCities = 0, contentFaces = 0, unhappyFacesAllCities = 0, unhappyFaces = 0,
    ),
    numberOfRequiredBuildings = 0,
    pollution = 0,
    production = 0,
    spaceshipPart = spaceshipPart,
    renderedObsoleteBy = -1,
    requiredResources = BldgRequiredResources(requiredResource1 = -1, requiredResource2 = -1),
    flags = ByteString.of(*ByteArray(16)),
    numberOfArmiesRequired = 0,
    flavors = 0,
    unknown = ByteString.of(*ByteArray(4)),
    unitsProduced = BldgUnitsProduced(unitProduced = -1, unitFrequency = 0),
)

private fun terrEntryWithPollutionEffect(pollutionEffect: Int): TerrEntry = TerrEntry(
    numberOfPossibleResources = 0,
    possibleResources = ByteString.of(),
    name = "",
    civilopediaEntry = "",
    terraformBonuses = TerrTerraformBonuses(irrigationBonus = 0, miningBonus = 0, roadBonus = 0),
    defenseBonus = 0,
    movementCost = 0,
    tileValues = TerrTileValues(food = 0, shields = 0, commerce = 0),
    workerJobAllowed = -1,
    pollutionEffect = pollutionEffect,
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
    unknown = ByteString.of(*ByteArray(4)),
    landmark = null,
    unknown2 = ByteString.of(*ByteArray(4)),
    terrainFlags = 0,
    diseaseStrength = 0,
)

class Civ3FileValidationTest : FunSpec({

    test("validate() surfaces the seed rule's issue for a file with an invalid pollutionEffect") {
        // 14 entries so this doesn't also trip validateTerrCardinality (Conquests requires exactly 14).
        val terrains = listOf(terrEntryWithPollutionEffect(pollutionEffect = 99)) +
            List(13) { terrEntryWithPollutionEffect(pollutionEffect = -1) }
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), listOf(TerrSection(terrains)))

        file.validate() shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TERR,
                0,
                "pollutionEffect",
                "pollutionEffect=99 is not -1, not the base-terrain sentinel (14), and not a valid TERR index (0..<14)",
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

    test("validateWmapCardinality returns no issues for exactly 1 entry") {
        val file = fileWithSections(major = 12, listOf(WmapSection(listOf(wmapEntry(0, 0)))))

        validateWmapCardinality(file) shouldBe emptyList()
    }

    test("validateWmapCardinality flags a count other than 1") {
        val file = fileWithSections(major = 12, listOf(WmapSection(List(2) { wmapEntry(0, 0) })))

        validateWmapCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.WMAP,
                null,
                "entries",
                "WMAP has 2 entries; every real official file has exactly 1",
            ),
        )
    }

    test("validateWmapCardinality returns no issues when WMAP is absent") {
        validateWmapCardinality(fileWithSections(major = 12, emptyList())) shouldBe emptyList()
    }

    test("validateWorldSizeResolves returns no issues when worldSize is in range") {
        val file = fileWithSections(major = 12, listOf(WchrSection(listOf(wchrEntry(worldSize = 4)))))

        validateWorldSizeResolves(file) shouldBe emptyList()
    }

    test("validateWorldSizeResolves flags an out-of-range worldSize as a warning") {
        val file = fileWithSections(
            major = 12,
            listOf(WsizSection(List(5) { wsizEntry() }), WchrSection(listOf(wchrEntry(worldSize = 5)))),
        )

        validateWorldSizeResolves(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.WCHR,
                0,
                "worldSize",
                "WCHR[0] has worldSize=5, which is not a valid WSIZ index (0..<5)",
            ),
        )
    }

    test("validateWorldSizeResolves flags an out-of-range worldSize even when WSIZ is absent entirely") {
        // Matches the 2 known real files: a bare map export with no embedded WSIZ section, whose
        // default ruleset's own WSIZ would still have the usual 5 entries.
        val file = fileWithSections(major = 12, listOf(WchrSection(listOf(wchrEntry(worldSize = 5)))))

        validateWorldSizeResolves(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.WCHR,
                0,
                "worldSize",
                "WCHR[0] has worldSize=5, which is not a valid WSIZ index (0..<5)",
            ),
        )
    }

    test("validateWorldSizeResolves returns no issues when WCHR is absent") {
        validateWorldSizeResolves(fileWithSections(major = 12, emptyList())) shouldBe emptyList()
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

    test("validateCityTerrainAllowsCities returns no issues when the tile's terrain allows cities") {
        val terrains = listOf(terrEntry(name = "Grassland", allowCities = 1))
        val tiles = List(10) { i -> if (i == 1) tileEntry(terrain = 0) else tileEntry() }
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(terrains),
                TileSection(tiles),
                CitySection(listOf(cityEntry(x = 2, y = 0))),
            ),
        )

        validateCityTerrainAllowsCities(file) shouldBe emptyList()
    }

    test("validateCityTerrainAllowsCities flags a city sitting on a terrain type that disallows cities") {
        val terrains = listOf(terrEntry(name = "Ocean", allowCities = 0))
        val tiles = List(10) { i -> if (i == 1) tileEntry(terrain = 0) else tileEntry() }
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(terrains),
                TileSection(tiles),
                CitySection(listOf(cityEntry(x = 2, y = 0))),
            ),
        )

        validateCityTerrainAllowsCities(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.CITY,
                0,
                "x/y",
                "CityEntry at (2, 0) sits on Ocean terrain, which disallows cities",
            ),
        )
    }

    test("validateCityTerrainAllowsCities returns no issues when WMAP is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(
                TerrSection(listOf(terrEntry())),
                TileSection(List(10) { tileEntry() }),
                CitySection(listOf(cityEntry(x = 2, y = 0))),
            ),
        )

        validateCityTerrainAllowsCities(file) shouldBe emptyList()
    }

    test("validateCityTerrainAllowsCities returns no issues when TERR is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TileSection(List(10) { tileEntry() }),
                CitySection(listOf(cityEntry(x = 2, y = 0))),
            ),
        )

        validateCityTerrainAllowsCities(file) shouldBe emptyList()
    }

    test("validateCityTerrainAllowsCities returns no issues when TILE is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(listOf(terrEntry())),
                CitySection(listOf(cityEntry(x = 2, y = 0))),
            ),
        )

        validateCityTerrainAllowsCities(file) shouldBe emptyList()
    }

    test("validateCityTerrainAllowsCities returns no issues when CITY is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(listOf(terrEntry())),
                TileSection(List(10) { tileEntry() }),
            ),
        )

        validateCityTerrainAllowsCities(file) shouldBe emptyList()
    }

    test("validateColonyTerrainAllowsImprovementType returns no issues when the terrain allows the improvement") {
        val terrains = listOf(terrEntry(name = "Grassland", allowAirfields = 1))
        val tiles = List(10) { i -> if (i == 1) tileEntry(terrain = 0) else tileEntry() }
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(terrains),
                TileSection(tiles),
                ClnySection(listOf(clnyEntry(x = 2, y = 0, improvementType = ClnyImprovementType.AIRFIELD))),
            ),
        )

        validateColonyTerrainAllowsImprovementType(file) shouldBe emptyList()
    }

    test("validateColonyTerrainAllowsImprovementType flags a colony sitting on a terrain type that disallows its improvement type") {
        val terrains = listOf(terrEntry(name = "Ocean", allowAirfields = 0))
        val tiles = List(10) { i -> if (i == 1) tileEntry(terrain = 0) else tileEntry() }
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(terrains),
                TileSection(tiles),
                ClnySection(listOf(clnyEntry(x = 2, y = 0, improvementType = ClnyImprovementType.AIRFIELD))),
            ),
        )

        validateColonyTerrainAllowsImprovementType(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.CLNY,
                0,
                "x/y",
                "ClnyEntry (AIRFIELD) at (2, 0) sits on Ocean terrain, which disallows it",
            ),
        )
    }

    test("validateColonyTerrainAllowsImprovementType returns no issues when the allowance field is absent (VANILLA/PTW)") {
        val terrains = listOf(terrEntry(name = "Ocean", allowAirfields = null))
        val tiles = List(10) { i -> if (i == 1) tileEntry(terrain = 0) else tileEntry() }
        val file = fileWithSections(
            major = 4,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(terrains),
                TileSection(tiles),
                ClnySection(listOf(clnyEntry(x = 2, y = 0, improvementType = ClnyImprovementType.AIRFIELD))),
            ),
        )

        validateColonyTerrainAllowsImprovementType(file) shouldBe emptyList()
    }

    test("validateColonyTerrainAllowsImprovementType returns no issues when WMAP is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(
                TerrSection(listOf(terrEntry())),
                TileSection(List(10) { tileEntry() }),
                ClnySection(listOf(clnyEntry(x = 2, y = 0))),
            ),
        )

        validateColonyTerrainAllowsImprovementType(file) shouldBe emptyList()
    }

    test("validateColonyTerrainAllowsImprovementType returns no issues when TERR is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TileSection(List(10) { tileEntry() }),
                ClnySection(listOf(clnyEntry(x = 2, y = 0))),
            ),
        )

        validateColonyTerrainAllowsImprovementType(file) shouldBe emptyList()
    }

    test("validateColonyTerrainAllowsImprovementType returns no issues when TILE is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(listOf(terrEntry())),
                ClnySection(listOf(clnyEntry(x = 2, y = 0))),
            ),
        )

        validateColonyTerrainAllowsImprovementType(file) shouldBe emptyList()
    }

    test("validateColonyTerrainAllowsImprovementType returns no issues when CLNY is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(listOf(terrEntry())),
                TileSection(List(10) { tileEntry() }),
            ),
        )

        validateColonyTerrainAllowsImprovementType(file) shouldBe emptyList()
    }

    test("validateFortressTerrainAllowsForts returns no issues when the terrain allows forts") {
        val terrains = listOf(terrEntry(name = "Grassland", allowForts = 1))
        val tiles = List(10) { i -> if (i == 3) tileEntry(terrain = 0, fortress = true) else tileEntry() }
        val file = fileWithSections(major = 12, listOf(TerrSection(terrains), TileSection(tiles)))

        validateFortressTerrainAllowsForts(file) shouldBe emptyList()
    }

    test("validateFortressTerrainAllowsForts returns no issues when there is no fortress") {
        val terrains = listOf(terrEntry(name = "Ocean", allowForts = 0))
        val tiles = List(10) { tileEntry(terrain = 0) }
        val file = fileWithSections(major = 12, listOf(TerrSection(terrains), TileSection(tiles)))

        validateFortressTerrainAllowsForts(file) shouldBe emptyList()
    }

    test("validateFortressTerrainAllowsForts flags a fortress sitting on a terrain type that disallows forts") {
        val terrains = listOf(terrEntry(name = "Ocean", allowForts = 0))
        val tiles = List(10) { i -> if (i == 3) tileEntry(terrain = 0, fortress = true) else tileEntry() }
        val file = fileWithSections(major = 12, listOf(TerrSection(terrains), TileSection(tiles)))

        validateFortressTerrainAllowsForts(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TILE,
                3,
                "fortress",
                "TILE[3] has a Fortress built, but sits on Ocean terrain, which disallows Forts",
            ),
        )
    }

    test("validateFortressTerrainAllowsForts returns no issues when the allowance field is absent (VANILLA/PTW)") {
        val terrains = listOf(terrEntry(name = "Ocean", allowForts = null))
        val tiles = List(10) { i -> if (i == 3) tileEntry(terrain = 0, fortress = true) else tileEntry() }
        val file = fileWithSections(major = 4, listOf(TerrSection(terrains), TileSection(tiles)))

        validateFortressTerrainAllowsForts(file) shouldBe emptyList()
    }

    test("validateFortressTerrainAllowsForts returns no issues when TERR is absent") {
        val file = fileWithSections(major = 12, listOf(TileSection(List(10) { tileEntry() })))

        validateFortressTerrainAllowsForts(file) shouldBe emptyList()
    }

    test("validateFortressTerrainAllowsForts returns no issues when TILE is absent") {
        val file = fileWithSections(major = 12, listOf(TerrSection(listOf(terrEntry()))))

        validateFortressTerrainAllowsForts(file) shouldBe emptyList()
    }

    test("validateUnitNotOnImpassableTerrain returns no issues when the terrain is passable") {
        val terrains = listOf(terrEntry(name = "Grassland", impassable = 0))
        val tiles = List(10) { i -> if (i == 1) tileEntry(terrain = 0) else tileEntry() }
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(terrains),
                TileSection(tiles),
                UnitSection(listOf(unitEntry(x = 2, y = 0))),
            ),
        )

        validateUnitNotOnImpassableTerrain(file) shouldBe emptyList()
    }

    test("validateUnitNotOnImpassableTerrain flags a unit sitting on impassable terrain") {
        val terrains = listOf(terrEntry(name = "Mountains", impassable = 1))
        val tiles = List(10) { i -> if (i == 1) tileEntry(terrain = 0) else tileEntry() }
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(terrains),
                TileSection(tiles),
                UnitSection(listOf(unitEntry(x = 2, y = 0))),
            ),
        )

        validateUnitNotOnImpassableTerrain(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.UNIT,
                0,
                "x/y",
                "UnitEntry at (2, 0) sits on Mountains terrain, which is Impassable",
            ),
        )
    }

    test("validateUnitNotOnImpassableTerrain returns no issues when the allowance field is absent (VANILLA/PTW)") {
        val terrains = listOf(terrEntry(name = "Mountains", impassable = null))
        val tiles = List(10) { i -> if (i == 1) tileEntry(terrain = 0) else tileEntry() }
        val file = fileWithSections(
            major = 4,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(terrains),
                TileSection(tiles),
                UnitSection(listOf(unitEntry(x = 2, y = 0))),
            ),
        )

        validateUnitNotOnImpassableTerrain(file) shouldBe emptyList()
    }

    test("validateUnitNotOnImpassableTerrain returns no issues when WMAP is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(TerrSection(listOf(terrEntry())), TileSection(List(10) { tileEntry() }), UnitSection(listOf(unitEntry(x = 2, y = 0)))),
        )

        validateUnitNotOnImpassableTerrain(file) shouldBe emptyList()
    }

    test("validateUnitNotOnImpassableTerrain returns no issues when TERR is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TileSection(List(10) { tileEntry() }),
                UnitSection(listOf(unitEntry(x = 2, y = 0))),
            ),
        )

        validateUnitNotOnImpassableTerrain(file) shouldBe emptyList()
    }

    test("validateUnitNotOnImpassableTerrain returns no issues when TILE is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(listOf(terrEntry())),
                UnitSection(listOf(unitEntry(x = 2, y = 0))),
            ),
        )

        validateUnitNotOnImpassableTerrain(file) shouldBe emptyList()
    }

    test("validateUnitNotOnImpassableTerrain returns no issues when UNIT is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(listOf(terrEntry())),
                TileSection(List(10) { tileEntry() }),
            ),
        )

        validateUnitNotOnImpassableTerrain(file) shouldBe emptyList()
    }

    test("validateWheeledUnitNotOnImpassableByWheeledTerrain returns no issues for a non-wheeled unit") {
        val terrains = listOf(terrEntry(name = "Desert", impassableByWheeled = 1))
        val tiles = List(10) { i -> if (i == 1) tileEntry(terrain = 0) else tileEntry() }
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(terrains),
                TileSection(tiles),
                UnitSection(listOf(unitEntry(x = 2, y = 0, unitType = 0))),
                PrtoSection(listOf(prtoEntry(wheeledAbility = false))),
            ),
        )

        validateWheeledUnitNotOnImpassableByWheeledTerrain(file) shouldBe emptyList()
    }

    test("validateWheeledUnitNotOnImpassableByWheeledTerrain flags a wheeled unit sitting on terrain impassable by wheeled units") {
        val terrains = listOf(terrEntry(name = "Desert", impassableByWheeled = 1))
        val tiles = List(10) { i -> if (i == 1) tileEntry(terrain = 0) else tileEntry() }
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(terrains),
                TileSection(tiles),
                UnitSection(listOf(unitEntry(x = 2, y = 0, unitType = 0))),
                PrtoSection(listOf(prtoEntry(wheeledAbility = true))),
            ),
        )

        validateWheeledUnitNotOnImpassableByWheeledTerrain(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.UNIT,
                0,
                "x/y",
                "UnitEntry at (2, 0) is a wheeled unit sitting on Desert terrain, which is Impassable by Wheeled Units",
            ),
        )
    }

    test("validateWheeledUnitNotOnImpassableByWheeledTerrain returns no issues when the allowance field is absent (VANILLA/PTW)") {
        val terrains = listOf(terrEntry(name = "Desert", impassableByWheeled = null))
        val tiles = List(10) { i -> if (i == 1) tileEntry(terrain = 0) else tileEntry() }
        val file = fileWithSections(
            major = 4,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(terrains),
                TileSection(tiles),
                UnitSection(listOf(unitEntry(x = 2, y = 0, unitType = 0))),
                PrtoSection(listOf(prtoEntry(wheeledAbility = true))),
            ),
        )

        validateWheeledUnitNotOnImpassableByWheeledTerrain(file) shouldBe emptyList()
    }

    test("validateWheeledUnitNotOnImpassableByWheeledTerrain returns no issues when PRTO is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 10, height = 2))),
                TerrSection(listOf(terrEntry())),
                TileSection(List(10) { tileEntry() }),
                UnitSection(listOf(unitEntry(x = 2, y = 0))),
            ),
        )

        validateWheeledUnitNotOnImpassableByWheeledTerrain(file) shouldBe emptyList()
    }

    // 4 TERR entries: [0] Land, [1] Coast, [2] Sea, [3] Ocean.
    // 4x4 WMAP (8 tiles): TILE[0]=(0,0) TILE[1]=(2,0) TILE[2]=(1,1) TILE[3]=(3,1)
    //                     TILE[4]=(0,2) TILE[5]=(2,2) TILE[6]=(1,3) TILE[7]=(3,3)
    val fourTerrTypes = listOf(terrEntry(name = "Land"), terrEntry(name = "Coast"), terrEntry(name = "Sea"), terrEntry(name = "Ocean"))

    test("validateLandNotDirectlyAdjacentToSeaOrOcean returns no issues when land only borders Coast") {
        val tiles = listOf(
            tileEntry(c3cTerrain = 0), // (0,0) Land
            tileEntry(c3cTerrain = 1), // (2,0) Coast
            tileEntry(c3cTerrain = 1), // (1,1) Coast
            tileEntry(c3cTerrain = 2), // (3,1) Sea
            tileEntry(c3cTerrain = 1), // (0,2) Coast
            tileEntry(c3cTerrain = 3), // (2,2) Ocean
            tileEntry(c3cTerrain = 1), // (1,3) Coast
            tileEntry(c3cTerrain = 3), // (3,3) Ocean
        )
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4))), TerrSection(fourTerrTypes), TileSection(tiles)),
        )

        validateLandNotDirectlyAdjacentToSeaOrOcean(file) shouldBe emptyList()
    }

    test("validateLandNotDirectlyAdjacentToSeaOrOcean flags land directly adjacent to Ocean") {
        // Only (0,0) is Land next to (2,0)'s Ocean; the rest of (2,0)'s other neighbors are Coast
        // so they don't add unrelated violations of their own.
        val tiles = listOf(
            tileEntry(c3cTerrain = 0), // (0,0) Land
            tileEntry(c3cTerrain = 3), // (2,0) Ocean - directly adjacent to (0,0), no Coast between
            tileEntry(c3cTerrain = 1), // (1,1) Coast
            tileEntry(c3cTerrain = 1), // (3,1) Coast
            tileEntry(c3cTerrain = 0), // (0,2) Land
            tileEntry(c3cTerrain = 1), // (2,2) Coast
            tileEntry(c3cTerrain = 0), // (1,3) Land
            tileEntry(c3cTerrain = 0), // (3,3) Land
        )
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4))), TerrSection(fourTerrTypes), TileSection(tiles)),
        )

        validateLandNotDirectlyAdjacentToSeaOrOcean(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TILE,
                0,
                "terrain",
                "TILE[0] at (0, 0) has land terrain directly adjacent to Sea or Ocean, with no Coast in between",
            ),
        )
    }

    test("validateLandNotDirectlyAdjacentToSeaOrOcean flags land directly adjacent to Sea") {
        // Only (0,0) is Land next to (1,1)'s Sea; the rest of (1,1)'s other neighbors are Coast so
        // they don't add unrelated violations of their own.
        val tiles = listOf(
            tileEntry(c3cTerrain = 0), // (0,0) Land
            tileEntry(c3cTerrain = 1), // (2,0) Coast
            tileEntry(c3cTerrain = 2), // (1,1) Sea - directly adjacent to (0,0), no Coast between
            tileEntry(c3cTerrain = 1), // (3,1) Coast
            tileEntry(c3cTerrain = 1), // (0,2) Coast
            tileEntry(c3cTerrain = 1), // (2,2) Coast
            tileEntry(c3cTerrain = 1), // (1,3) Coast
            tileEntry(c3cTerrain = 0), // (3,3) Land
        )
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4))), TerrSection(fourTerrTypes), TileSection(tiles)),
        )

        validateLandNotDirectlyAdjacentToSeaOrOcean(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TILE,
                0,
                "terrain",
                "TILE[0] at (0, 0) has land terrain directly adjacent to Sea or Ocean, with no Coast in between",
            ),
        )
    }

    test("validateLandNotDirectlyAdjacentToSeaOrOcean does not flag a water tile itself") {
        val tiles = listOf(
            tileEntry(c3cTerrain = 3), // (0,0) Ocean
            tileEntry(c3cTerrain = 3), // (2,0) Ocean
            tileEntry(c3cTerrain = 3), // (1,1) Ocean
            tileEntry(c3cTerrain = 3), // (3,1) Ocean
            tileEntry(c3cTerrain = 3), // (0,2) Ocean
            tileEntry(c3cTerrain = 3), // (2,2) Ocean
            tileEntry(c3cTerrain = 3), // (1,3) Ocean
            tileEntry(c3cTerrain = 3), // (3,3) Ocean
        )
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4))), TerrSection(fourTerrTypes), TileSection(tiles)),
        )

        validateLandNotDirectlyAdjacentToSeaOrOcean(file) shouldBe emptyList()
    }

    // 8x4 WMAP (16 tiles), for a wraparound case a 4x4 map is too small to distinguish from a
    // normal (non-wrap) adjacency. TILE[0]=(0,0) TILE[1]=(2,0) TILE[2]=(4,0) TILE[3]=(6,0)
    // TILE[4]=(1,1) TILE[5]=(3,1) TILE[6]=(5,1) TILE[7]=(7,1) TILE[8]=(0,2) TILE[9]=(2,2)
    // TILE[10]=(4,2) TILE[11]=(6,2) TILE[12]=(1,3) TILE[13]=(3,3) TILE[14]=(5,3) TILE[15]=(7,3)
    // TILE[0](0,0) and TILE[3](6,0) are 3 columns apart and are not neighbors on a flat map, but
    // become neighbors once xWrapping wraps x=0-2 around to x=width-2=6. TILE[2]/[6]/[7]/[11] are
    // Coast — TILE[3]'s own (non-wrap) neighbors — so they don't add unrelated violations.
    val wrapTestTerrainByIndex =
        mapOf(0 to 0, 1 to 0, 2 to 1, 3 to 3, 4 to 0, 5 to 0, 6 to 1, 7 to 1, 8 to 0, 9 to 0, 10 to 0, 11 to 1, 12 to 0, 13 to 0, 14 to 0, 15 to 0)
    val wrapTestTiles = List(16) { i -> tileEntry(c3cTerrain = wrapTestTerrainByIndex.getValue(i).toByte()) }

    test("validateLandNotDirectlyAdjacentToSeaOrOcean detects a violation only visible via x-wrap") {
        val file = fileWithSections(
            major = 12,
            listOf(
                WmapSection(listOf(wmapEntry(width = 8, height = 4, flags = 1 shl 0))),
                TerrSection(fourTerrTypes),
                TileSection(wrapTestTiles),
            ),
        )

        validateLandNotDirectlyAdjacentToSeaOrOcean(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TILE,
                0,
                "terrain",
                "TILE[0] at (0, 0) has land terrain directly adjacent to Sea or Ocean, with no Coast in between",
            ),
        )
    }

    test("validateLandNotDirectlyAdjacentToSeaOrOcean does not flag the same pair without wrapping") {
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 8, height = 4))), TerrSection(fourTerrTypes), TileSection(wrapTestTiles)),
        )

        validateLandNotDirectlyAdjacentToSeaOrOcean(file) shouldBe emptyList()
    }

    test("validateLandNotDirectlyAdjacentToSeaOrOcean returns no issues when WMAP is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(TerrSection(fourTerrTypes), TileSection(List(8) { tileEntry() })),
        )

        validateLandNotDirectlyAdjacentToSeaOrOcean(file) shouldBe emptyList()
    }

    test("validateLandNotDirectlyAdjacentToSeaOrOcean returns no issues when TERR is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4))), TileSection(List(8) { tileEntry() })),
        )

        validateLandNotDirectlyAdjacentToSeaOrOcean(file) shouldBe emptyList()
    }

    test("validateLandNotDirectlyAdjacentToSeaOrOcean returns no issues when TILE is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4))), TerrSection(fourTerrTypes)),
        )

        validateLandNotDirectlyAdjacentToSeaOrOcean(file) shouldBe emptyList()
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

    test("validateCtznDefaultCount returns no issues when exactly one entry is the default") {
        val file = fileWithSections(major = 12, listOf(CtznSection(listOf(ctznEntry(defaultCitizen = 1), ctznEntry()))))

        validateCtznDefaultCount(file) shouldBe emptyList()
    }

    test("validateCtznDefaultCount flags zero default entries") {
        val file = fileWithSections(major = 12, listOf(CtznSection(listOf(ctznEntry(), ctznEntry()))))

        validateCtznDefaultCount(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.CTZN,
                null,
                "defaultCitizen",
                "CTZN has 0 entries with defaultCitizen set; exactly 1 is expected",
            ),
        )
    }

    test("validateCtznDefaultCount flags more than one default entry") {
        val file = fileWithSections(
            major = 12,
            listOf(CtznSection(listOf(ctznEntry(defaultCitizen = 1), ctznEntry(defaultCitizen = 1)))),
        )

        validateCtznDefaultCount(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.CTZN,
                null,
                "defaultCitizen",
                "CTZN has 2 entries with defaultCitizen set; exactly 1 is expected",
            ),
        )
    }

    test("validateCtznDefaultCount returns no issues when CTZN is absent") {
        validateCtznDefaultCount(fileWithSections(major = 12, emptyList())) shouldBe emptyList()
    }

    test("validateCtznDefaultPrerequisite returns no issues when the default has no prerequisite") {
        val file = fileWithSections(
            major = 12,
            listOf(CtznSection(listOf(ctznEntry(defaultCitizen = 1, prerequisite = -1)))),
        )

        validateCtznDefaultPrerequisite(file) shouldBe emptyList()
    }

    test("validateCtznDefaultPrerequisite flags a default entry with a prerequisite") {
        val file = fileWithSections(
            major = 12,
            listOf(CtznSection(listOf(ctznEntry(defaultCitizen = 1, prerequisite = 3)))),
        )

        validateCtznDefaultPrerequisite(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.CTZN,
                0,
                "prerequisite",
                "the default citizen type has prerequisite=3; it should need no prerequisite (-1)",
            ),
        )
    }

    test("validateCtznDefaultPrerequisite returns no issues when there isn't exactly one default") {
        val file = fileWithSections(
            major = 12,
            listOf(
                CtznSection(
                    listOf(ctznEntry(defaultCitizen = 1, prerequisite = 3), ctznEntry(defaultCitizen = 1)),
                ),
            ),
        )

        // Wrong default count is validateCtznDefaultCount's concern, not this rule's.
        validateCtznDefaultPrerequisite(file) shouldBe emptyList()
    }

    test("validateCtznDefaultPrerequisite returns no issues when CTZN is absent") {
        validateCtznDefaultPrerequisite(fileWithSections(major = 12, emptyList())) shouldBe emptyList()
    }

    test("validateTerrCardinality returns no issues for exactly 12 entries in PTW") {
        val file = fileWithSections(major = 11, listOf(TerrSection(List(12) { terrEntry() })))

        validateTerrCardinality(file) shouldBe emptyList()
    }

    test("validateTerrCardinality returns no issues for exactly 12 entries in vanilla") {
        val file = fileWithSections(major = 3, listOf(TerrSection(List(12) { terrEntry() })))

        validateTerrCardinality(file) shouldBe emptyList()
    }

    test("validateTerrCardinality flags a count other than 12 in PTW") {
        val file = fileWithSections(major = 11, listOf(TerrSection(List(11) { terrEntry() })))

        validateTerrCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TERR,
                null,
                "entries",
                "TERR has 11 entries; PTW requires exactly 12",
            ),
        )
    }

    test("validateTerrCardinality returns no issues for exactly 14 entries in Conquests") {
        val file = fileWithSections(major = 12, listOf(TerrSection(List(14) { terrEntry() })))

        validateTerrCardinality(file) shouldBe emptyList()
    }

    test("validateTerrCardinality flags a count other than 14 in Conquests") {
        val file = fileWithSections(major = 12, listOf(TerrSection(List(15) { terrEntry() })))

        validateTerrCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TERR,
                null,
                "entries",
                "TERR has 15 entries; CONQUESTS requires exactly 14",
            ),
        )
    }

    test("validateTerrCardinality returns no issues when TERR is absent") {
        validateTerrCardinality(fileWithSections(major = 12, emptyList())) shouldBe emptyList()
    }

    test("validateTfrmCardinality returns no issues for exactly 9 entries in vanilla") {
        val file = fileWithSections(major = 3, listOf(TfrmSection(List(9) { tfrmEntry() })))

        validateTfrmCardinality(file) shouldBe emptyList()
    }

    test("validateTfrmCardinality flags a count other than 9 in vanilla") {
        val file = fileWithSections(major = 3, listOf(TfrmSection(List(8) { tfrmEntry() })))

        validateTfrmCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TFRM,
                null,
                "entries",
                "TFRM has 8 entries; VANILLA requires exactly 9",
            ),
        )
    }

    test("validateTfrmCardinality returns no issues for exactly 12 entries in PTW") {
        val file = fileWithSections(major = 11, listOf(TfrmSection(List(12) { tfrmEntry() })))

        validateTfrmCardinality(file) shouldBe emptyList()
    }

    test("validateTfrmCardinality flags a count other than 12 in PTW") {
        val file = fileWithSections(major = 11, listOf(TfrmSection(List(13) { tfrmEntry() })))

        validateTfrmCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TFRM,
                null,
                "entries",
                "TFRM has 13 entries; PTW requires exactly 12",
            ),
        )
    }

    test("validateTfrmCardinality returns no issues for exactly 13 entries in Conquests") {
        val file = fileWithSections(major = 12, listOf(TfrmSection(List(13) { tfrmEntry() })))

        validateTfrmCardinality(file) shouldBe emptyList()
    }

    test("validateTfrmCardinality flags a count other than 13 in Conquests") {
        val file = fileWithSections(major = 12, listOf(TfrmSection(List(12) { tfrmEntry() })))

        validateTfrmCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TFRM,
                null,
                "entries",
                "TFRM has 12 entries; CONQUESTS requires exactly 13",
            ),
        )
    }

    test("validateTfrmCardinality returns no issues when TFRM is absent") {
        validateTfrmCardinality(fileWithSections(major = 12, emptyList())) shouldBe emptyList()
    }

    test("validateBldgSpaceshipPartBounds returns no issues for distinct in-bounds parts") {
        val file = fileWithSections(
            major = 12,
            listOf(
                RuleSection(listOf(ruleEntryWithSpaceshipPartCount(3))),
                BldgSection(listOf(bldgEntry(spaceshipPart = -1), bldgEntry(spaceshipPart = 0), bldgEntry(spaceshipPart = 2))),
            ),
        )

        validateBldgSpaceshipPartBounds(file) shouldBe emptyList()
    }

    test("validateBldgSpaceshipPartBounds flags an out-of-bounds spaceshipPart") {
        val file = fileWithSections(
            major = 12,
            listOf(
                RuleSection(listOf(ruleEntryWithSpaceshipPartCount(3))),
                BldgSection(listOf(bldgEntry(spaceshipPart = 3))),
            ),
        )

        validateBldgSpaceshipPartBounds(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "spaceshipPart",
                "spaceshipPart=3 is not -1 and not a valid RULE spaceshipPartQuantities index (0..<3)",
            ),
        )
    }

    test("validateBldgSpaceshipPartBounds flags a duplicate spaceshipPart") {
        val file = fileWithSections(
            major = 12,
            listOf(
                RuleSection(listOf(ruleEntryWithSpaceshipPartCount(3))),
                BldgSection(listOf(bldgEntry(spaceshipPart = 1), bldgEntry(spaceshipPart = 1))),
            ),
        )

        validateBldgSpaceshipPartBounds(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                1,
                "spaceshipPart",
                "spaceshipPart=1 is already assigned to an earlier BLDG entry",
            ),
        )
    }

    test("validateBldgSpaceshipPartBounds returns no issues when BLDG is absent") {
        val file = fileWithSections(major = 12, listOf(RuleSection(listOf(ruleEntryWithSpaceshipPartCount(3)))))

        validateBldgSpaceshipPartBounds(file) shouldBe emptyList()
    }

    test("validateBldgSpaceshipPartBounds returns no issues when RULE is absent") {
        val file = fileWithSections(major = 12, listOf(BldgSection(listOf(bldgEntry(spaceshipPart = 0)))))

        validateBldgSpaceshipPartBounds(file) shouldBe emptyList()
    }

    test("validateTileContinentResolves returns no issues when every continent resolves") {
        val file = fileWithSections(
            major = 12,
            listOf(ContSection(listOf(contEntry())), TileSection(List(3) { tileEntry(continent = 0) })),
        )

        validateTileContinentResolves(file) shouldBe emptyList()
    }

    test("validateTileContinentResolves flags an out-of-range continent") {
        val file = fileWithSections(
            major = 12,
            listOf(ContSection(listOf(contEntry())), TileSection(listOf(tileEntry(continent = 5)))),
        )

        validateTileContinentResolves(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TILE,
                0,
                "continent",
                "TILE[0] has continent=5, which is not a valid CONT index (0..<1)",
            ),
        )
    }

    test("validateTileContinentResolves returns no issues when CONT is absent") {
        val file = fileWithSections(major = 12, listOf(TileSection(listOf(tileEntry(continent = 5)))))

        validateTileContinentResolves(file) shouldBe emptyList()
    }

    test("validateTileContinentResolves returns no issues when TILE is absent") {
        val file = fileWithSections(major = 12, listOf(ContSection(listOf(contEntry()))))

        validateTileContinentResolves(file) shouldBe emptyList()
    }

    test("validateContCardinality returns no issues when the sum matches TILE's entry count") {
        val file = fileWithSections(
            major = 12,
            listOf(ContSection(listOf(contEntry(numberOfTiles = 2), contEntry(numberOfTiles = 1))), TileSection(List(3) { tileEntry() })),
        )

        validateContCardinality(file) shouldBe emptyList()
    }

    test("validateContCardinality flags a mismatch between the sum and TILE's entry count") {
        val file = fileWithSections(
            major = 12,
            listOf(ContSection(listOf(contEntry(numberOfTiles = 2))), TileSection(List(3) { tileEntry() })),
        )

        validateContCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.CONT,
                null,
                "numberOfTiles",
                "sum(CONT.numberOfTiles)=2 does not match TILE's 3 entries",
            ),
        )
    }

    test("validateContCardinality returns no issues when CONT is absent") {
        val file = fileWithSections(major = 12, listOf(TileSection(List(3) { tileEntry() })))

        validateContCardinality(file) shouldBe emptyList()
    }

    test("validateContCardinality returns no issues when TILE is absent") {
        val file = fileWithSections(major = 12, listOf(ContSection(listOf(contEntry(numberOfTiles = 2)))))

        validateContCardinality(file) shouldBe emptyList()
    }

    test("validateContTypeNotMixed returns no issues when land and water ids are distinct") {
        val tiles = listOf(
            tileEntry(c3cTerrain = 0, continent = 0), // Land
            tileEntry(c3cTerrain = 3, continent = 1), // Ocean
        )
        val file = fileWithSections(
            major = 12,
            listOf(
                TerrSection(fourTerrTypes),
                TileSection(tiles),
                ContSection(listOf(contEntry(type = ContType.LAND), contEntry(type = ContType.WATER))),
            ),
        )

        validateContTypeNotMixed(file) shouldBe emptyList()
    }

    test("validateContTypeNotMixed flags a continent id used by both land and water tiles") {
        val tiles = listOf(
            tileEntry(c3cTerrain = 0, continent = 0), // Land, continent 0
            tileEntry(c3cTerrain = 3, continent = 0), // Ocean, also continent 0
        )
        val file = fileWithSections(
            major = 12,
            listOf(TerrSection(fourTerrTypes), TileSection(tiles), ContSection(listOf(contEntry(type = ContType.LAND)))),
        )

        validateContTypeNotMixed(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.CONT,
                0,
                "type",
                "CONT[0] (LAND) is used by both a land tile and a water tile",
            ),
        )
    }

    test("validateContTypeNotMixed returns no issues when TERR is absent") {
        val file = fileWithSections(major = 12, listOf(TileSection(listOf(tileEntry())), ContSection(listOf(contEntry()))))

        validateContTypeNotMixed(file) shouldBe emptyList()
    }

    test("validateContTypeNotMixed returns no issues when TILE is absent") {
        val file = fileWithSections(major = 12, listOf(TerrSection(fourTerrTypes), ContSection(listOf(contEntry()))))

        validateContTypeNotMixed(file) shouldBe emptyList()
    }

    test("validateContTypeNotMixed returns no issues when CONT is absent") {
        val file = fileWithSections(major = 12, listOf(TerrSection(fourTerrTypes), TileSection(listOf(tileEntry()))))

        validateContTypeNotMixed(file) shouldBe emptyList()
    }

    test("validateAdjacentLandTilesShareContinent returns no issues when adjacent land tiles share continent") {
        // 4x4 grid (see fourTerrTypes/wmapEntry usage above): all Land, all continent 0.
        val tiles = List(8) { tileEntry(c3cTerrain = 0, continent = 0) }
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4))), TerrSection(fourTerrTypes), TileSection(tiles)),
        )

        validateAdjacentLandTilesShareContinent(file) shouldBe emptyList()
    }

    test("validateAdjacentLandTilesShareContinent flags adjacent land tiles with different continent ids") {
        // Only TILE[0]=(0,0) and TILE[1]=(2,0) are Land (everything else Ocean, so it can't add
        // unrelated violations of its own); they are adjacent (see the 4x4 coordinate table
        // above) and each is the other's only land neighbor.
        val tiles = listOf(
            tileEntry(c3cTerrain = 0, continent = 0),
            tileEntry(c3cTerrain = 0, continent = 1),
            tileEntry(c3cTerrain = 3, continent = 2),
            tileEntry(c3cTerrain = 3, continent = 2),
            tileEntry(c3cTerrain = 3, continent = 2),
            tileEntry(c3cTerrain = 3, continent = 2),
            tileEntry(c3cTerrain = 3, continent = 2),
            tileEntry(c3cTerrain = 3, continent = 2),
        )
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4))), TerrSection(fourTerrTypes), TileSection(tiles)),
        )

        validateAdjacentLandTilesShareContinent(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TILE,
                0,
                "continent",
                "TILE[0] at (0, 0) has continent=0, but its adjacent land TILE[1] has continent=1",
            ),
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TILE,
                1,
                "continent",
                "TILE[1] at (2, 0) has continent=1, but its adjacent land TILE[0] has continent=0",
            ),
        )
    }

    test("validateAdjacentLandTilesShareContinent returns no issues when WMAP is absent") {
        val file = fileWithSections(major = 12, listOf(TerrSection(fourTerrTypes), TileSection(List(8) { tileEntry() })))

        validateAdjacentLandTilesShareContinent(file) shouldBe emptyList()
    }

    test("validateAdjacentLandTilesShareContinent returns no issues when TERR is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4))), TileSection(List(8) { tileEntry() })),
        )

        validateAdjacentLandTilesShareContinent(file) shouldBe emptyList()
    }

    test("validateAdjacentLandTilesShareContinent returns no issues when TILE is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4))), TerrSection(fourTerrTypes)),
        )

        validateAdjacentLandTilesShareContinent(file) shouldBe emptyList()
    }

    test("validateContinentContiguous returns no issues when every continent id is one connected group") {
        val tiles = listOf(
            tileEntry(continent = 0), tileEntry(continent = 0), tileEntry(continent = 1), tileEntry(continent = 1),
            tileEntry(continent = 0), tileEntry(continent = 0), tileEntry(continent = 1), tileEntry(continent = 1),
        )
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4))), TileSection(tiles)),
        )

        validateContinentContiguous(file) shouldBe emptyList()
    }

    test("validateContinentContiguous flags a continent id split into disconnected pieces") {
        // TILE[0]=(0,0) and TILE[7]=(3,3) both continent 0, but are not adjacent or otherwise
        // connected via any chain of continent-0 tiles on this 4x4 grid.
        val tiles = listOf(
            tileEntry(continent = 0), tileEntry(continent = 1), tileEntry(continent = 1), tileEntry(continent = 1),
            tileEntry(continent = 1), tileEntry(continent = 1), tileEntry(continent = 1), tileEntry(continent = 0),
        )
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4))), TileSection(tiles)),
        )

        validateContinentContiguous(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.CONT,
                0,
                "type",
                "CONT[0]'s tiles are split into more than one physically-disconnected group",
            ),
        )
    }

    test("validateContinentContiguous treats a wrap-only connection as contiguous") {
        // On the 8x4 grid (see wrapTestTiles above), TILE[0]=(0,0) and TILE[3]=(6,0) are only
        // adjacent via xWrapping. Assign them both continent 0 and everything else continent 1;
        // with wrapping enabled this is one connected group, so no issue is expected.
        val tiles = List(16) { i -> tileEntry(continent = if (i == 0 || i == 3) 0 else 1) }
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 8, height = 4, flags = 1 shl 0))), TileSection(tiles)),
        )

        validateContinentContiguous(file) shouldBe emptyList()
    }

    test("validateContinentContiguous returns no issues when WMAP is absent") {
        val file = fileWithSections(major = 12, listOf(TileSection(List(8) { tileEntry() })))

        validateContinentContiguous(file) shouldBe emptyList()
    }

    test("validateContinentContiguous returns no issues when TILE is absent") {
        val file = fileWithSections(major = 12, listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4)))))

        validateContinentContiguous(file) shouldBe emptyList()
    }

    test("validateWaterContinentTouchesLand returns no issues when every water id touches land") {
        val tiles = listOf(
            tileEntry(c3cTerrain = 0, continent = 0), // (0,0) Land
            tileEntry(c3cTerrain = 3, continent = 1), // (2,0) Ocean, touches (0,0)
            tileEntry(c3cTerrain = 0, continent = 0),
            tileEntry(c3cTerrain = 0, continent = 0),
            tileEntry(c3cTerrain = 0, continent = 0),
            tileEntry(c3cTerrain = 0, continent = 0),
            tileEntry(c3cTerrain = 0, continent = 0),
            tileEntry(c3cTerrain = 0, continent = 0),
        )
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4))), TerrSection(fourTerrTypes), TileSection(tiles)),
        )

        validateWaterContinentTouchesLand(file) shouldBe emptyList()
    }

    test("validateWaterContinentTouchesLand flags a water continent that never touches land") {
        // All 8 tiles Ocean, continent 0: this water body never touches land at all.
        val tiles = List(8) { tileEntry(c3cTerrain = 3, continent = 0) }
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4))), TerrSection(fourTerrTypes), TileSection(tiles)),
        )

        validateWaterContinentTouchesLand(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.CONT,
                0,
                "type",
                "CONT[0] (Water) never directly touches a land tile",
            ),
        )
    }

    test("validateWaterContinentTouchesLand returns no issues when WMAP is absent") {
        val file = fileWithSections(major = 12, listOf(TerrSection(fourTerrTypes), TileSection(List(8) { tileEntry() })))

        validateWaterContinentTouchesLand(file) shouldBe emptyList()
    }

    test("validateWaterContinentTouchesLand returns no issues when TERR is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4))), TileSection(List(8) { tileEntry() })),
        )

        validateWaterContinentTouchesLand(file) shouldBe emptyList()
    }

    test("validateWaterContinentTouchesLand returns no issues when TILE is absent") {
        val file = fileWithSections(
            major = 12,
            listOf(WmapSection(listOf(wmapEntry(width = 4, height = 4))), TerrSection(fourTerrTypes)),
        )

        validateWaterContinentTouchesLand(file) shouldBe emptyList()
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
