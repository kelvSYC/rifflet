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
