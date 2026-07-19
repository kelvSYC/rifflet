package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validRaceEntry(
    bonuses: Int = 0,
    governorSettings: Int = 0,
    buildNever: Int = 0,
    buildOften: Int = 0,
): RaceEntry = RaceEntry(
    cityNames = emptyList(),
    greatLeaderNames = emptyList(),
    leaderName = "",
    leaderTitle = "",
    civilopediaEntry = "",
    adjective = "",
    name = "Rome",
    noun = "",
    eras = emptyList(),
    cultureGroup = 0,
    leaderGender = 0,
    civilizationGender = 0,
    aggressionLevel = 0,
    uniqueCivilizationCounter = 0,
    shunnedGovernment = 0,
    favoriteGovernment = 0,
    defaultColor = 0,
    uniqueColor = 0,
    freeTech1 = 0,
    freeTech2 = 0,
    freeTech3 = 0,
    freeTech4 = 0,
    bonuses = bonuses,
    governorSettings = governorSettings,
    buildNever = buildNever,
    buildOften = buildOften,
    plurality = 0,
    unitTypeForKing = 0,
    flavors = 0,
    unknown = ByteString.of(0, 0, 0, 0),
    diplomacyTextIndex = 0,
    scientificLeaderNames = emptyList(),
)

class RaceEntryBonusesFlagsTest : FunSpec({

    val properties: List<Pair<Int, (RaceEntry) -> Boolean>> = listOf(
        0 to RaceEntry::militaristic,
        1 to RaceEntry::commercial,
        2 to RaceEntry::expansionist,
        3 to RaceEntry::scientific,
        4 to RaceEntry::religious,
        5 to RaceEntry::industrious,
        6 to RaceEntry::agricultural,
        7 to RaceEntry::seaFaring,
    )

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validRaceEntry(bonuses = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validRaceEntry(bonuses = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validRaceEntry(bonuses = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class RaceEntryGovernorSettingsFlagsTest : FunSpec({

    val properties: List<Pair<Int, (RaceEntry) -> Boolean>> = listOf(
        0 to RaceEntry::manageCitizens,
        1 to RaceEntry::emphasizeFood,
        2 to RaceEntry::emphasizeShields,
        3 to RaceEntry::emphasizeTrade,
        4 to RaceEntry::manageProduction,
        5 to RaceEntry::noWonders,
        6 to RaceEntry::noSmallWonders,
    )

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validRaceEntry(governorSettings = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validRaceEntry(governorSettings = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validRaceEntry(governorSettings = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})
