package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.ClnyEntry
import com.kelvsyc.rifflet.civ3.ClnyImprovementType
import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.LeadEntry
import com.kelvsyc.rifflet.civ3.RaceCultureGroup
import com.kelvsyc.rifflet.civ3.RaceLeader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun clnyEntry(
    ownerType: Int = 3,
    owner: Int = -1,
    improvementType: ClnyImprovementType = ClnyImprovementType.COLONY,
): ClnyEntry = ClnyEntry(ownerType = ownerType, owner = owner, x = 10, y = 20, improvementType = improvementType)

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

class ClnyEntryMappingTest : FunSpec({

    test("toDomain maps scalar fields straight across") {
        val entry = clnyEntry(improvementType = ClnyImprovementType.AIRFIELD)

        val colony = listOf(entry).toDomain(emptyList(), emptyList()).single()

        colony.x shouldBe 10
        colony.y shouldBe 20
        colony.improvementType shouldBe ClnyImprovementType.AIRFIELD
    }

    test("toDomain resolves owner as a Civilization") {
        val r = race("Rome")
        val entry = clnyEntry(ownerType = 2, owner = 1)

        val colony = listOf(entry).toDomain(listOf(race("Egypt"), r), emptyList()).single()

        colony.owner shouldBe Owner.Civilization(r, unresolvedIndex = 1)
    }

    test("toDomain resolves owner as a Player") {
        val lead = leadEntry("Caesar")
        val entry = clnyEntry(ownerType = 3, owner = 0)

        val colony = listOf(entry).toDomain(emptyList(), listOf(lead)).single()

        colony.owner shouldBe Owner.Player(lead, unresolvedIndex = 0)
    }

    test("toDomain resolves a Civilization owner against an empty races list to a null payload") {
        val entry = clnyEntry(ownerType = 2, owner = 1)

        val colony = listOf(entry).toDomain(emptyList(), emptyList()).single()

        colony.owner shouldBe Owner.Civilization(null, unresolvedIndex = 1)
    }

    test("toDomain resolves a Player owner against an empty leads list to a null payload") {
        val entry = clnyEntry(ownerType = 3, owner = 0)

        val colony = listOf(entry).toDomain(emptyList(), emptyList()).single()

        colony.owner shouldBe Owner.Player(null, unresolvedIndex = 0)
    }

    test("toDomain throws for a None ownerType") {
        val entry = clnyEntry(ownerType = 0)

        shouldThrow<IllegalArgumentException> {
            listOf(entry).toDomain(emptyList(), emptyList())
        }
    }

    test("toDomain throws for a Barbarian ownerType") {
        val entry = clnyEntry(ownerType = 1)

        shouldThrow<IllegalArgumentException> {
            listOf(entry).toDomain(emptyList(), emptyList())
        }
    }

    test("toDomain throws for the barbarian placeholder civ") {
        val entry = clnyEntry(ownerType = 2, owner = 0)

        shouldThrow<IllegalArgumentException> {
            listOf(entry).toDomain(emptyList(), emptyList())
        }
    }
})
