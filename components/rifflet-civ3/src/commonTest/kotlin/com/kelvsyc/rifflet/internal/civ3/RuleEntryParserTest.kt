package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.RuleEntry
import com.kelvsyc.rifflet.core.RiffletParseException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString

/** Writes [text] into [fieldSize] bytes, null-padding the remainder. */
private fun Buffer.writePaddedField(text: String, fieldSize: Int) {
    val start = size
    writeString(text, Charsets.US_ASCII)
    write(ByteArray((fieldSize - (size - start)).toInt()))
}

/**
 * Builds a well-formed RULE item body (length prefix excluded, as with prior sections), with
 * `upgradeCost` present. Uses small (2-element) dynamic-array sizes for both
 * `spaceshipPartQuantities` and `cultureLevelNames` to prove both dynamic reads are genuine,
 * not hardcoded.
 *
 * [includeFlagUnitType] controls whether `flagUnitType` (and, nested inside it, `upgradeCost` —
 * real files never have `upgradeCost` without `flagUnitType`) is written — `false` produces the
 * real vanilla shape confirmed against real files: neither field is present.
 */
private fun ruleItemBinary(upgradeCost: Int? = 15, includeFlagUnitType: Boolean = true): Buffer = Buffer().apply {
    writePaddedField("Town", 32) // citySizeLevel1Name
    writePaddedField("City", 32) // citySizeLevel2Name
    writePaddedField("Metropolis", 32) // citySizeLevel3Name
    writeIntLe(2) // numberOfSpaceshipParts
    writeIntLe(4) // spaceshipPartQuantities[0]
    writeIntLe(2) // spaceshipPartQuantities[1]
    writeIntLe(10) // advancedBarbarianUnitType
    writeIntLe(9) // basicBarbarianUnitType
    writeIntLe(11) // barbarianSeaUnitType
    writeIntLe(2) // citiesNeededToSupportAnArmy
    writeIntLe(5) // chanceOfRioting
    writeIntLe(1) // turnPenaltyForEachDraftedCitizen
    writeIntLe(2) // shieldCostPerGold
    writeIntLe(50) // fortressDefensiveBonus
    writeIntLe(1) // citizensAffectedByEachHappyFace
    write(ByteArray(8)) // unknown
    writeIntLe(1) // forestValueInShields
    writeIntLe(2) // shieldValueInGold
    writeIntLe(1) // citizenValueInShields
    writeIntLe(2) // defaultDifficultyLevel
    writeIntLe(15) // battleCreatedUnit
    writeIntLe(16) // buildArmyUnit
    writeIntLe(50) // buildingDefensiveBonus
    writeIntLe(25) // citizenDefensiveBonus
    writeIntLe(0) // defaultMoneyResource
    writeIntLe(25) // chanceToInterceptEnemyAirMissions
    writeIntLe(25) // chanceToInterceptEnemyStealthMissions
    writeIntLe(50) // startingTreasury
    write(ByteArray(4)) // unknown2
    writeIntLe(2) // foodConsumptionPerCitizen
    writeIntLe(25) // riverDefensiveBonus
    writeIntLe(1) // turnPenaltyForEachHurrySacrifice
    writeIntLe(17) // scout
    writeIntLe(18) // slave
    writeIntLe(3) // movementAlongRoads
    writeIntLe(1) // startUnit1
    writeIntLe(2) // startUnit2
    writeIntLe(6) // minimumPopulationForWeLoveTheKing
    writeIntLe(25) // townDefenseBonus
    writeIntLe(50) // cityDefenseBonus
    writeIntLe(100) // metropolisDefenseBonus
    writeIntLe(8) // maximumLevel1CitySize
    writeIntLe(16) // maximumLevel2CitySize
    write(ByteArray(4)) // unknown3
    writeIntLe(25) // fortificationsDefensiveBonus
    writeIntLe(2) // numberOfCultureLevels
    writePaddedField("Emerging", 64) // cultureLevelNames[0]
    writePaddedField("Legendary", 64) // cultureLevelNames[1]
    writeIntLe(2) // borderExpansionMultiplier
    writeIntLe(3) // borderFactor
    writeIntLe(1000) // futureTechCost
    writeIntLe(16) // goldenAgeDuration
    writeIntLe(4) // maximumResearchTime
    writeIntLe(1) // minimumResearchTime
    if (includeFlagUnitType) {
        writeIntLe(20) // flagUnitType
        if (upgradeCost != null) writeIntLe(upgradeCost)
    }
}

class RuleEntryParserTest : FunSpec({

    test("well-formed item with upgradeCost present is parsed into all fields") {
        val entry = RuleEntryParser.parse(ruleItemBinary())
        entry shouldBe RuleEntry(
            citySizeLevel1Name = "Town",
            citySizeLevel2Name = "City",
            citySizeLevel3Name = "Metropolis",
            spaceshipPartQuantities = listOf(4, 2),
            advancedBarbarianUnitType = 10,
            basicBarbarianUnitType = 9,
            barbarianSeaUnitType = 11,
            citiesNeededToSupportAnArmy = 2,
            chanceOfRioting = 5,
            turnPenaltyForEachDraftedCitizen = 1,
            shieldCostPerGold = 2,
            fortressDefensiveBonus = 50,
            citizensAffectedByEachHappyFace = 1,
            unknown = ByteString.of(*ByteArray(8)),
            forestValueInShields = 1,
            shieldValueInGold = 2,
            citizenValueInShields = 1,
            defaultDifficultyLevel = 2,
            battleCreatedUnit = 15,
            buildArmyUnit = 16,
            buildingDefensiveBonus = 50,
            citizenDefensiveBonus = 25,
            defaultMoneyResource = 0,
            chanceToInterceptEnemyAirMissions = 25,
            chanceToInterceptEnemyStealthMissions = 25,
            startingTreasury = 50,
            unknown2 = ByteString.of(*ByteArray(4)),
            foodConsumptionPerCitizen = 2,
            riverDefensiveBonus = 25,
            turnPenaltyForEachHurrySacrifice = 1,
            scout = 17,
            slave = 18,
            movementAlongRoads = 3,
            startUnit1 = 1,
            startUnit2 = 2,
            minimumPopulationForWeLoveTheKing = 6,
            townDefenseBonus = 25,
            cityDefenseBonus = 50,
            metropolisDefenseBonus = 100,
            maximumLevel1CitySize = 8,
            maximumLevel2CitySize = 16,
            unknown3 = ByteString.of(*ByteArray(4)),
            fortificationsDefensiveBonus = 25,
            cultureLevelNames = listOf("Emerging", "Legendary"),
            borderExpansionMultiplier = 2,
            borderFactor = 3,
            futureTechCost = 1000,
            goldenAgeDuration = 16,
            maximumResearchTime = 4,
            minimumResearchTime = 1,
            flagUnitType = 20,
            upgradeCost = 15,
        )
    }

    test("well-formed item with upgradeCost absent defaults it to zero") {
        val entry = RuleEntryParser.parse(ruleItemBinary(upgradeCost = null))
        entry.upgradeCost shouldBe 0
        entry.flagUnitType shouldBe 20
    }

    test("vanilla-shape item (flagUnitType and upgradeCost both absent, confirmed real vanilla shape) defaults both to zero") {
        val entry = RuleEntryParser.parse(ruleItemBinary(includeFlagUnitType = false))
        entry.flagUnitType shouldBe 0
        entry.upgradeCost shouldBe 0
    }

    test("RuleEntry rejects an unknown field that is not exactly 8 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedRuleEntry(unknown = ByteString.of(0, 0, 0))
        }
    }

    test("RuleEntry rejects an unknown2 field that is not exactly 4 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedRuleEntry(unknown2 = ByteString.of(0, 0))
        }
    }

    test("RuleEntry rejects an unknown3 field that is not exactly 4 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedRuleEntry(unknown3 = ByteString.of(0, 0))
        }
    }

    test("an implausibly large numberOfSpaceshipParts throws RiffletParseException before attempting to allocate") {
        val buffer = Buffer().apply {
            write(ByteArray(96)) // citySizeLevel1Name..3Name (3 * 32)
            writeIntLe(Int.MAX_VALUE) // numberOfSpaceshipParts
        }
        shouldThrow<RiffletParseException> { RuleEntryParser.parse(buffer) }
    }

    test("an implausibly large numberOfCultureLevels throws RiffletParseException before attempting to allocate") {
        val buffer = Buffer().apply {
            write(ByteArray(96)) // citySizeLevel1Name..3Name (3 * 32)
            writeIntLe(0) // numberOfSpaceshipParts
            // advancedBarbarianUnitType..fortificationsDefensiveBonus: 9 ints (36B) + unknown (8B) +
            // 12 ints (48B) + unknown2 (4B) + 14 ints (56B) + unknown3 (4B) + 1 int (4B) = 160B
            write(ByteArray(160))
            writeIntLe(Int.MAX_VALUE) // numberOfCultureLevels
        }
        shouldThrow<RiffletParseException> { RuleEntryParser.parse(buffer) }
    }
})

/** Builds a well-formed [RuleEntry] with all-zero/empty values, for domain-invariant tests that
 * only care about overriding one `unknown*` field. */
private fun wellFormedRuleEntry(
    unknown: ByteString = ByteString.of(*ByteArray(8)),
    unknown2: ByteString = ByteString.of(*ByteArray(4)),
    unknown3: ByteString = ByteString.of(*ByteArray(4)),
): RuleEntry = RuleEntry(
    citySizeLevel1Name = "", citySizeLevel2Name = "", citySizeLevel3Name = "",
    spaceshipPartQuantities = emptyList(),
    advancedBarbarianUnitType = 0, basicBarbarianUnitType = 0, barbarianSeaUnitType = 0,
    citiesNeededToSupportAnArmy = 0, chanceOfRioting = 0, turnPenaltyForEachDraftedCitizen = 0,
    shieldCostPerGold = 0, fortressDefensiveBonus = 0, citizensAffectedByEachHappyFace = 0,
    unknown = unknown,
    forestValueInShields = 0, shieldValueInGold = 0, citizenValueInShields = 0,
    defaultDifficultyLevel = 0, battleCreatedUnit = 0, buildArmyUnit = 0,
    buildingDefensiveBonus = 0, citizenDefensiveBonus = 0, defaultMoneyResource = 0,
    chanceToInterceptEnemyAirMissions = 0, chanceToInterceptEnemyStealthMissions = 0,
    startingTreasury = 0,
    unknown2 = unknown2,
    foodConsumptionPerCitizen = 0, riverDefensiveBonus = 0, turnPenaltyForEachHurrySacrifice = 0,
    scout = 0, slave = 0, movementAlongRoads = 0, startUnit1 = 0, startUnit2 = 0,
    minimumPopulationForWeLoveTheKing = 0, townDefenseBonus = 0, cityDefenseBonus = 0,
    metropolisDefenseBonus = 0, maximumLevel1CitySize = 0, maximumLevel2CitySize = 0,
    unknown3 = unknown3,
    fortificationsDefensiveBonus = 0, cultureLevelNames = emptyList(),
    borderExpansionMultiplier = 0, borderFactor = 0, futureTechCost = 0, goldenAgeDuration = 0,
    maximumResearchTime = 0, minimumResearchTime = 0, flagUnitType = 0, upgradeCost = 0,
)
