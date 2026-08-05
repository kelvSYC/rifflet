package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.LeadEntry
import com.kelvsyc.rifflet.civ3.RaceCultureGroup
import com.kelvsyc.rifflet.civ3.RaceLeader
import com.kelvsyc.rifflet.civ3.SlocEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun slocEntry(ownerType: Int = 0, owner: Int = -1): SlocEntry = SlocEntry(
    ownerType = ownerType, owner = owner, x = 10, y = 20,
)

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

class SlocEntryMappingTest : FunSpec({

    test("toDomain maps scalar fields straight across") {
        val entry = slocEntry()

        val location = listOf(entry).toDomain(emptyList(), emptyList()).single()

        location.x shouldBe 10
        location.y shouldBe 20
    }

    test("toDomain resolves owner as None") {
        val entry = slocEntry(ownerType = 0, owner = -1)

        val location = listOf(entry).toDomain(emptyList(), emptyList()).single()

        location.owner shouldBe Owner.None
    }

    test("toDomain resolves owner as a Civilization") {
        val r = race("Rome")
        val entry = slocEntry(ownerType = 2, owner = 1)

        val location = listOf(entry).toDomain(listOf(race("Egypt"), r), emptyList()).single()

        location.owner shouldBe Owner.Civilization(r, unresolvedIndex = 1)
    }

    test("toDomain resolves owner as a Player") {
        val lead = leadEntry("Caesar")
        val entry = slocEntry(ownerType = 3, owner = 0)

        val location = listOf(entry).toDomain(emptyList(), listOf(lead)).single()

        location.owner shouldBe Owner.Player(lead, unresolvedIndex = 0)
    }

    test("toDomain resolves a Player owner against an empty leads list to a null payload") {
        val entry = slocEntry(ownerType = 3, owner = 0)

        val location = listOf(entry).toDomain(emptyList(), emptyList()).single()

        location.owner shouldBe Owner.Player(null, unresolvedIndex = 0)
    }

    test("toDomain resolves a Civilization owner against an empty races list to a null payload") {
        val entry = slocEntry(ownerType = 2, owner = 1)

        val location = listOf(entry).toDomain(emptyList(), emptyList()).single()

        location.owner shouldBe Owner.Civilization(null, unresolvedIndex = 1)
    }

    test("toDomain throws for an out-of-range ownerType") {
        val entry = slocEntry(ownerType = 4)

        shouldThrow<IllegalArgumentException> {
            listOf(entry).toDomain(emptyList(), emptyList())
        }
    }

    test("toDomain throws for a Barbarian ownerType") {
        val entry = slocEntry(ownerType = 1)

        shouldThrow<IllegalArgumentException> {
            listOf(entry).toDomain(emptyList(), emptyList())
        }
    }

    test("toDomain().toWire() round-trips scalar fields and a Civilization owner") {
        val r = race("Rome")
        val entries = listOf(slocEntry(ownerType = 2, owner = 1))

        val roundTripped = entries.toDomain(listOf(race("Egypt"), r), emptyList())
            .toWire(listOf(race("Egypt"), r), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips a Player owner") {
        val lead = leadEntry("Caesar")
        val entries = listOf(slocEntry(ownerType = 3, owner = 0))

        val roundTripped = entries.toDomain(emptyList(), listOf(lead)).toWire(emptyList(), listOf(lead))

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() writes back -1 for a None owner's sentinel") {
        val location = StartingLocation(x = 10, y = 20, owner = Owner.None)

        val wire = listOf(location).toWire(emptyList(), emptyList()).single()

        wire.ownerType shouldBe 0
        wire.owner shouldBe -1
    }

    test("toWire throws on a dangling Civilization race reference") {
        val location = StartingLocation(x = 10, y = 20, owner = Owner.Civilization(race("Outsider")))

        shouldThrow<IllegalArgumentException> {
            listOf(location).toWire(emptyList(), emptyList())
        }
    }

    test("toWire throws on a dangling Player lead reference") {
        val location = StartingLocation(x = 10, y = 20, owner = Owner.Player(leadEntry("Outsider")))

        shouldThrow<IllegalArgumentException> {
            listOf(location).toWire(emptyList(), emptyList())
        }
    }

    test("toWire preserves a Barbarian owner's tribeIndex when it isn't the default") {
        val location = StartingLocation(x = 10, y = 20, owner = Owner.Barbarian(tribeIndex = 5))

        val wire = listOf(location).toWire(emptyList(), emptyList()).single()

        wire.ownerType shouldBe 1
        wire.owner shouldBe 5
    }

    test("toDomain().toWire() round-trips a Civilization owner with no RACE data") {
        val entries = listOf(slocEntry(ownerType = 2, owner = 6))

        val roundTripped = entries.toDomain(emptyList(), emptyList()).toWire(emptyList(), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips a Player owner with no LEAD data") {
        val entries = listOf(slocEntry(ownerType = 3, owner = 4))

        val roundTripped = entries.toDomain(emptyList(), emptyList()).toWire(emptyList(), emptyList())

        roundTripped shouldBe entries
    }
})
