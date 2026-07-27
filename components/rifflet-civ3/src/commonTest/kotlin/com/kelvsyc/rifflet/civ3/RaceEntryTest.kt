package com.kelvsyc.rifflet.civ3

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validRaceEntry(unknown: ByteString = ByteString.of(0, 0, 0, 0)) = RaceEntry(
    cityNames = emptyList(),
    greatLeaderNames = emptyList(),
    leader = RaceLeader(name = "", title = "", gender = 0),
    civilopediaEntry = "",
    adjective = "",
    name = "Rome",
    noun = "",
    eras = emptyList(),
    cultureGroup = 0,
    civilizationGender = 0,
    personality = RacePersonality(favoriteGovernment = 0, shunnedGovernment = 0, aggressionLevel = 0),
    uniqueCivilizationCounter = 0,
    defaultColor = 0,
    uniqueColor = 0,
    freeTechs = listOf(0, 0, 0, 0),
    bonuses = 0,
    governor = RaceGovernor(settings = 0, buildNever = 0, buildOften = 0),
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
