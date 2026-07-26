package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validBldgEntry(
    requiredBuilding: Int = 0,
    requiredGovernment: Int = 0,
    requiredAdvance: Int = 0,
    renderedObsoleteBy: Int = 0,
    requiredResource1: Int = 0,
    requiredResource2: Int = 0,
    unitProduced: Int = 0,
    flags: ByteString = ByteString.of(*ByteArray(16)),
): BldgEntry = BldgEntry(
    description = "",
    name = "",
    civilopediaEntry = "",
    doublesHappiness = 0,
    gainInEveryCity = 0,
    gainInEveryCityOnContinent = 0,
    requiredBuilding = requiredBuilding,
    cost = 0,
    culture = 0,
    bombardDefense = 0,
    navalBombardDefense = 0,
    defenseBonus = 0,
    navalDefenseBonus = 0,
    maintenanceCost = 0,
    contentFacesAllCities = 0,
    contentFaces = 0,
    unhappyFacesAllCities = 0,
    unhappyFaces = 0,
    numberOfRequiredBuildings = 0,
    airPower = 0,
    navalPower = 0,
    pollution = 0,
    production = 0,
    requiredGovernment = requiredGovernment,
    spaceshipPart = 0,
    requiredAdvance = requiredAdvance,
    renderedObsoleteBy = renderedObsoleteBy,
    requiredResource1 = requiredResource1,
    requiredResource2 = requiredResource2,
    flags = flags,
    numberOfArmiesRequired = 0,
    flavors = 0,
    unknown = ByteString.of(*ByteArray(4)),
    unitProduced = unitProduced,
    unitFrequency = 0,
)

private fun validGovtEntry(): GovtEntry = GovtEntry(
    defaultType = 0,
    transitionType = 0,
    requiresMaintenance = 0,
    toggle1 = 0,
    tilePenalty = 0,
    tradeBonus = 0,
    name = "",
    civilopediaEntry = "",
    maleRulerTitle1 = "",
    femaleRulerTitle1 = "",
    maleRulerTitle2 = "",
    femaleRulerTitle2 = "",
    maleRulerTitle3 = "",
    femaleRulerTitle3 = "",
    maleRulerTitle4 = "",
    femaleRulerTitle4 = "",
    corruption = 0,
    immuneTo = 0,
    diplomatsAre = 0,
    spiesAre = 0,
    relationships = emptyList(),
    hurrying = 0,
    assimilationChance = 0,
    draftLimit = 0,
    militaryPoliceLimit = 0,
    rulerTitlePairsUsed = 0,
    prerequisiteTechnology = 0,
    scienceRateCap = 0,
    workerRate = 0,
    toggle2 = 0,
    toggle3 = 0,
    unknown = ByteString.of(0, 0, 0, 0),
    freeUnits = 0,
    freeUnitsPerTown = 0,
    freeUnitsPerCity = 0,
    freeUnitsPerMetropolis = 0,
    unitCost = 0,
    warWeariness = 0,
    xenophobic = 0,
    forceResettle = 0,
)

private fun validTechEntry(): TechEntry = TechEntry(
    name = "",
    civilopediaEntry = "",
    cost = 0,
    era = 0,
    advanceIcon = 0,
    x = 0,
    y = 0,
    prerequisite1 = 0,
    prerequisite2 = 0,
    prerequisite3 = 0,
    prerequisite4 = 0,
    flags = 0,
    flavors = 0,
    unknown = ByteString.of(0, 0, 0, 0),
)

private fun validGoodEntry(): GoodEntry = GoodEntry(
    name = "",
    civilopediaEntry = "",
    type = 0,
    appearanceRatio = 0,
    disappearanceProbability = 0,
    icon = 0,
    prerequisite = 0,
    foodBonus = 0,
    shieldsBonus = 0,
    commerceBonus = 0,
)

private fun validPrtoEntry(): PrtoEntry = PrtoEntry(
    zoneOfControl = 0,
    name = "",
    civilopediaEntry = "",
    bombardStrength = 0,
    bombardRange = 0,
    capacity = 0,
    shieldCost = 0,
    defense = 0,
    iconIndex = 0,
    attack = 0,
    operationalRange = 0,
    populationCost = 0,
    rateOfFire = 0,
    movement = 0,
    required = 0,
    upgradeTo = 0,
    requiredResource1 = 0,
    requiredResource2 = 0,
    requiredResource3 = 0,
    abilities = 0,
    aiStrategies = 0,
    availableTo = 0,
    flags2 = ByteString.of(*ByteArray(8)),
    type = 0,
    otherStrategy = 0,
    hpBonus = 0,
    standardOrders = 0,
    specialActions = 0,
    workerActions = 0,
    airMissions = 0,
    flags4 = ByteString.of(*ByteArray(4)),
    bombardEffects = 0,
    ignoreMovementCost = ByteString.of(),
    requireSupport = 0,
    unknown = ByteString.of(*ByteArray(16)),
    enslaveResults = 0,
    unknown2 = ByteString.of(0, 0, 0, 0),
    stealthTargetUnitTypes = emptyList(),
    unknown3 = ByteString.of(*ByteArray(8)),
    createCraters = 0,
    workerStrength = 0f,
    unknown4 = ByteString.of(0, 0, 0, 0),
    airDefense = 0,
)

class BldgEntryReferencesTest : FunSpec({

    test("requiredBuildingBldg resolves a self-reference") {
        val bldg = validBldgEntry()
        validBldgEntry(requiredBuilding = 0).requiredBuildingBldg(listOf(bldg)) shouldBe bldg
        validBldgEntry(requiredBuilding = 5).requiredBuildingBldg(emptyList()) shouldBe null
    }

    test("requiredGovernmentGovt resolves against the GOVT list") {
        val govt = validGovtEntry()
        validBldgEntry(requiredGovernment = 0).requiredGovernmentGovt(listOf(govt)) shouldBe govt
        validBldgEntry(requiredGovernment = 5).requiredGovernmentGovt(emptyList()) shouldBe null
    }

    test("requiredAdvanceTech resolves against the TECH list") {
        val tech = validTechEntry()
        validBldgEntry(requiredAdvance = 0).requiredAdvanceTech(listOf(tech)) shouldBe tech
        validBldgEntry(requiredAdvance = 5).requiredAdvanceTech(emptyList()) shouldBe null
    }

    test("renderedObsoleteByTech resolves against the TECH list") {
        val tech = validTechEntry()
        validBldgEntry(renderedObsoleteBy = 0).renderedObsoleteByTech(listOf(tech)) shouldBe tech
        validBldgEntry(renderedObsoleteBy = 5).renderedObsoleteByTech(emptyList()) shouldBe null
    }

    test("requiredResource1Good resolves against the GOOD list") {
        val good = validGoodEntry()
        validBldgEntry(requiredResource1 = 0).requiredResource1Good(listOf(good)) shouldBe good
        validBldgEntry(requiredResource1 = 5).requiredResource1Good(emptyList()) shouldBe null
    }

    test("requiredResource2Good resolves against the GOOD list") {
        val good = validGoodEntry()
        validBldgEntry(requiredResource2 = 0).requiredResource2Good(listOf(good)) shouldBe good
        validBldgEntry(requiredResource2 = 5).requiredResource2Good(emptyList()) shouldBe null
    }

    test("unitProducedPrto resolves against the PRTO list") {
        val prto = validPrtoEntry()
        validBldgEntry(unitProduced = 0).unitProducedPrto(listOf(prto)) shouldBe prto
        validBldgEntry(unitProduced = 5).unitProducedPrto(emptyList()) shouldBe null
    }

    test("requiredGoodsMustBeInCityRadius(era) reads smallWonders bit 9 for VANILLA/PTW and improvements bit 31 for CONQUESTS") {
        // smallWonders bit 9 -> byte offset 9 (byte 1 of the 4-byte smallWonders window), bit 1
        val ptwFlags = ByteString.of(*(ByteArray(9) + byteArrayOf(0b00000010) + ByteArray(6)))
        val entry = validBldgEntry(flags = ptwFlags)
        entry.requiredGoodsMustBeInCityRadius(Civ3FormatEra.VANILLA) shouldBe true
        entry.requiredGoodsMustBeInCityRadius(Civ3FormatEra.PTW) shouldBe true
        entry.requiredGoodsMustBeInCityRadius(Civ3FormatEra.CONQUESTS) shouldBe false

        // improvements bit 31 -> byte offset 3 (byte 3 of the 4-byte improvements window), bit 7
        val conquestsFlags = ByteString.of(*(ByteArray(3) + byteArrayOf(0b10000000.toByte()) + ByteArray(12)))
        val c3cEntry = validBldgEntry(flags = conquestsFlags)
        c3cEntry.requiredGoodsMustBeInCityRadius(Civ3FormatEra.CONQUESTS) shouldBe true
        c3cEntry.requiredGoodsMustBeInCityRadius(Civ3FormatEra.PTW) shouldBe false
    }
})
