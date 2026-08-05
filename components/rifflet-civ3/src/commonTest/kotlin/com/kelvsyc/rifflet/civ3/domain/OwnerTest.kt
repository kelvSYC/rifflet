package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.LeadEntry
import com.kelvsyc.rifflet.civ3.RaceCultureGroup
import com.kelvsyc.rifflet.civ3.RaceLeader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun race(name: String = ""): Race = Race(
    name = name, civilopediaEntry = "", adjective = "", noun = "",
    leader = RaceLeader(name = "", title = "", gender = Gender.MALE),
    cultureGroup = RaceCultureGroup.NONE, civilizationGender = Gender.MALE,
)

private fun leadEntry(name: String = ""): LeadEntry = LeadEntry(
    customCivData = 0, humanPlayer = 0, name = name, unknown = ByteString.of(*ByteArray(8)),
    startUnits = emptyList(), genderOfLeaderName = 0, startingTechnologyIds = emptyList(),
    difficulty = -2, initialEra = 0, startCash = 0, government = 0, civ = -2, color = 0,
    skipFirstTurn = 0, unknown2 = ByteString.of(*ByteArray(4)), startEmbassies = 0,
)

class OwnerTest : FunSpec({

    test("resolveOwner resolves None") {
        resolveOwner(0, 0, emptyList(), emptyList()) shouldBe Owner.None
    }

    test("resolveOwner resolves Barbarian, preserving the raw owner as tribeIndex") {
        resolveOwner(1, 7, emptyList(), emptyList()) shouldBe Owner.Barbarian(tribeIndex = 7)
    }

    test("resolveOwner resolves a Civilization with a matching Race") {
        val r = race("Rome")

        resolveOwner(2, 0, listOf(r), emptyList()) shouldBe Owner.Civilization(r, unresolvedIndex = 0)
    }

    test("resolveOwner resolves a Civilization with a dangling Race index, preserving it as unresolvedIndex") {
        resolveOwner(2, 5, emptyList(), emptyList()) shouldBe Owner.Civilization(null, unresolvedIndex = 5)
    }

    test("resolveOwner resolves a Player with a matching LeadEntry") {
        val lead = leadEntry("Caesar")

        resolveOwner(3, 0, emptyList(), listOf(lead)) shouldBe Owner.Player(lead, unresolvedIndex = 0)
    }

    test("resolveOwner resolves a Player with a dangling LeadEntry index, preserving it as unresolvedIndex") {
        resolveOwner(3, 5, emptyList(), emptyList()) shouldBe Owner.Player(null, unresolvedIndex = 5)
    }

    test("resolveOwner throws for an out-of-range ownerType") {
        shouldThrow<IllegalArgumentException> {
            resolveOwner(4, 0, emptyList(), emptyList())
        }
    }
})
