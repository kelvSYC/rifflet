package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validRaceEntry(): RaceEntry = RaceEntry(
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
    unknown = okio.ByteString.of(0, 0, 0, 0),
    diplomacyTextIndex = 0,
    scientificLeaderNames = emptyList(),
)

class OwnerTest : FunSpec({

    test("ownerType 0 resolves to Owner.None") {
        resolveOwner(ownerType = 0, owner = 99, races = emptyList()) shouldBe Owner.None
    }

    test("ownerType 1 resolves to Owner.Barbarian") {
        resolveOwner(ownerType = 1, owner = 99, races = emptyList()) shouldBe Owner.Barbarian
    }

    test("ownerType 3 resolves to Owner.Player with the raw index") {
        resolveOwner(ownerType = 3, owner = 5, races = emptyList()) shouldBe Owner.Player(5)
    }

    test("ownerType 2 with a resolving index resolves to Owner.Civilization with the race") {
        val race = validRaceEntry()
        resolveOwner(ownerType = 2, owner = 0, races = listOf(race)) shouldBe Owner.Civilization(race)
    }

    test("ownerType 2 with a non-resolving index resolves to Owner.Civilization(null)") {
        resolveOwner(ownerType = 2, owner = 5, races = emptyList()) shouldBe Owner.Civilization(null)
    }

    test("an ownerType outside 0..3 resolves to Owner.Unrecognized preserving both raw values") {
        resolveOwner(ownerType = 9, owner = 5, races = emptyList()) shouldBe Owner.Unrecognized(9, 5)
    }
})
