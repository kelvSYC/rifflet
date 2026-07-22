package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun bytesFor(value: Int): List<Byte> = listOf(
    (value and 0xFF).toByte(),
    ((value shr 8) and 0xFF).toByte(),
    ((value shr 16) and 0xFF).toByte(),
    ((value shr 24) and 0xFF).toByte(),
)

private fun validBldgEntry(
    improvements: Int = 0,
    otherCharacteristics: Int = 0,
    smallWonders: Int = 0,
    wonders: Int = 0,
): BldgEntry = BldgEntry(
    description = "",
    name = "",
    civilopediaEntry = "",
    doublesHappiness = 0,
    gainInEveryCity = 0,
    gainInEveryCityOnContinent = 0,
    requiredBuilding = 0,
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
    requiredGovernment = 0,
    spaceshipPart = 0,
    requiredAdvance = 0,
    renderedObsoleteBy = 0,
    requiredResource1 = 0,
    requiredResource2 = 0,
    flags = ByteString.of(
        *(bytesFor(improvements) + bytesFor(otherCharacteristics) + bytesFor(smallWonders) + bytesFor(wonders)).toByteArray(),
    ),
    numberOfArmiesRequired = 0,
    flavors = 0,
    unknown = ByteString.of(*ByteArray(4)),
    unitProduced = 0,
    unitFrequency = 0,
)

class BldgEntryImprovementsFlagsTest : FunSpec({

    val properties: List<Pair<Int, (BldgEntry) -> Boolean>> = listOf(
        0 to BldgEntry::centerOfEmpire,
        1 to BldgEntry::veteranGroundUnits,
        2 to BldgEntry::plus50PercentResearchOutput,
        3 to BldgEntry::plus50PercentLuxuryOutput,
        4 to BldgEntry::plus50PercentTaxOutput,
        5 to BldgEntry::removesPopulationPollution,
        6 to BldgEntry::reducesBuildingPollution,
        7 to BldgEntry::resistantToBribery,
        8 to BldgEntry::improvementsReducesCorruption,
        9 to BldgEntry::doublesCityGrowthRate,
        10 to BldgEntry::increasesLuxuryTrade,
        11 to BldgEntry::allowsCitySizeLevel2,
        12 to BldgEntry::allowsCitySizeLevel3,
        13 to BldgEntry::replacesOtherBuildings,
        14 to BldgEntry::mustBeNearWater,
        15 to BldgEntry::mustBeNearARiver,
        16 to BldgEntry::canExplodeOrMeltdown,
        17 to BldgEntry::veteranSeaUnits,
        18 to BldgEntry::veteranAirUnits,
        19 to BldgEntry::capitalization,
        20 to BldgEntry::allowsWaterTrade,
        21 to BldgEntry::allowsAirTrade,
        22 to BldgEntry::reducesWarWeariness,
        23 to BldgEntry::increasesShieldsInWater,
        24 to BldgEntry::increasesFoodInWater,
        25 to BldgEntry::increasesTradeInWater,
        27 to BldgEntry::stealthAttackBarrier,
        29 to BldgEntry::doublesSacrifice,
        30 to BldgEntry::producesUnits,
        31 to BldgEntry::requiredGoodsMustBeInCityRadius,
    )

    test("improvements extracts the first 4-byte window as a little-endian Int") {
        validBldgEntry(improvements = 0x01020304).improvements shouldBe 0x01020304
    }

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validBldgEntry(improvements = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validBldgEntry(improvements = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validBldgEntry(improvements = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class BldgEntryOtherCharacteristicsFlagsTest : FunSpec({

    val properties: List<Pair<Int, (BldgEntry) -> Boolean>> = listOf(
        0 to BldgEntry::coastalInstallation,
        1 to BldgEntry::militaristic,
        2 to BldgEntry::wonder,
        3 to BldgEntry::smallWonder,
        4 to BldgEntry::continentalMoodEffects,
        5 to BldgEntry::scientific,
        6 to BldgEntry::commercial,
        7 to BldgEntry::expansionist,
        8 to BldgEntry::religious,
        9 to BldgEntry::industrious,
        10 to BldgEntry::agricultural,
        11 to BldgEntry::seaFaring,
    )

    test("otherCharacteristics extracts the second 4-byte window as a little-endian Int") {
        validBldgEntry(otherCharacteristics = 0x01020304).otherCharacteristics shouldBe 0x01020304
    }

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validBldgEntry(otherCharacteristics = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validBldgEntry(otherCharacteristics = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validBldgEntry(otherCharacteristics = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class BldgEntrySmallWondersFlagsTest : FunSpec({

    val properties: List<Pair<Int, (BldgEntry) -> Boolean>> = listOf(
        0 to BldgEntry::increasesChanceOfLeaderAppearance,
        1 to BldgEntry::buildArmiesWithoutLeader,
        2 to BldgEntry::buildLargerArmies,
        3 to BldgEntry::treasuryEarns5Percent,
        4 to BldgEntry::buildSpaceshipParts,
        5 to BldgEntry::smallWondersReducesCorruption,
        6 to BldgEntry::decreasesSuccessOfMissileAttacks,
        7 to BldgEntry::allowsSpyMissions,
        8 to BldgEntry::allowsHealingInEnemyTerritory,
        9 to BldgEntry::ptwRequiredGoodsMustBeInCityRadius,
        10 to BldgEntry::requiresVictoriousArmy,
        11 to BldgEntry::requiresEliteNavalUnits,
    )

    test("smallWonders extracts the third 4-byte window as a little-endian Int") {
        validBldgEntry(smallWonders = 0x01020304).smallWonders shouldBe 0x01020304
    }

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validBldgEntry(smallWonders = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validBldgEntry(smallWonders = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validBldgEntry(smallWonders = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class BldgEntryWondersFlagsTest : FunSpec({

    val properties: List<Pair<Int, (BldgEntry) -> Boolean>> = listOf(
        0 to BldgEntry::safeSeaTravel,
        1 to BldgEntry::gainAnyAdvancesOwnedBy2Civs,
        2 to BldgEntry::doubleCombatStrengthVsBarbarians,
        3 to BldgEntry::plus1ShipMovement,
        4 to BldgEntry::doublesResearchOutput,
        5 to BldgEntry::plus1TradeInEachTradeProducingTile,
        6 to BldgEntry::halvesUnitUpgradeCost,
        7 to BldgEntry::paysMaintenanceForTradeInstallations,
        8 to BldgEntry::allowsAllCivsToBuildNuclears,
        9 to BldgEntry::cityGrowthCausesPlus2Citizens,
        10 to BldgEntry::plus2FreeAdvances,
        11 to BldgEntry::reducesWarWearinessInAllCities,
        12 to BldgEntry::doublesCityDefenses,
        13 to BldgEntry::allowsDiplomaticVictory,
        16 to BldgEntry::increasedArmyValue,
        14 to BldgEntry::plus2ShipMovement,
        17 to BldgEntry::touristAttraction,
    )

    test("wonders extracts the fourth 4-byte window as a little-endian Int") {
        validBldgEntry(wonders = 0x01020304).wonders shouldBe 0x01020304
    }

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validBldgEntry(wonders = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validBldgEntry(wonders = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validBldgEntry(wonders = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})
