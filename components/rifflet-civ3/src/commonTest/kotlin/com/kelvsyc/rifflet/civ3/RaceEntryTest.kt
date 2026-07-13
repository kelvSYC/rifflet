package com.kelvsyc.rifflet.civ3

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validRaceEntry(unknown: ByteString = ByteString.of(0, 0, 0, 0)) = RaceEntry(
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
    bonuses = 0,
    governorSettings = 0,
    buildNever = 0,
    buildOften = 0,
    plurality = 0,
    unitTypeForKing = 0,
    flavors = 0,
    unknown = unknown,
    diplomacyTextIndex = 0,
    scientificLeaderNames = emptyList(),
)

class RaceEntryTest : FunSpec({

    test("a 4-byte unknown field is accepted") {
        validRaceEntry().unknown.size shouldBe 4
    }

    test("an unknown field of any other size throws IllegalArgumentException") {
        shouldThrow<IllegalArgumentException> { validRaceEntry(unknown = ByteString.of(1, 2)) }
    }
})
