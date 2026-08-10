package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.ClnyEntry
import com.kelvsyc.rifflet.civ3.ClnyImprovementType
import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.RaceCultureGroup
import com.kelvsyc.rifflet.civ3.RaceLeader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

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

private fun leadEntry(name: String = ""): Leader = Leader(name = name)

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

    test("toDomain().toWire() round-trips scalar fields and a Civilization owner") {
        val r = race("Rome")
        val entries = listOf(clnyEntry(ownerType = 2, owner = 1, improvementType = ClnyImprovementType.RADAR_TOWER))

        val roundTripped = entries.toDomain(listOf(race("Egypt"), r), emptyList())
            .toWire(listOf(race("Egypt"), r), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips a Player owner") {
        val lead = leadEntry("Caesar")
        val entries = listOf(clnyEntry(ownerType = 3, owner = 0))

        val roundTripped = entries.toDomain(emptyList(), listOf(lead)).toWire(emptyList(), listOf(lead))

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips a Civilization owner with no RACE data") {
        val entries = listOf(clnyEntry(ownerType = 2, owner = 6))

        val roundTripped = entries.toDomain(emptyList(), emptyList()).toWire(emptyList(), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips a Player owner with no LEAD data") {
        val entries = listOf(clnyEntry(ownerType = 3, owner = 4))

        val roundTripped = entries.toDomain(emptyList(), emptyList()).toWire(emptyList(), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips each ClnyImprovementType value") {
        val entries = ClnyImprovementType.entries.map { clnyEntry(improvementType = it) }

        val roundTripped = entries.toDomain(emptyList(), emptyList()).toWire(emptyList(), emptyList())

        roundTripped shouldBe entries
    }

    test("toWire writes back -1 for a None owner's sentinel") {
        val colony = Colony(x = 10, y = 20, owner = Owner.None)

        val wire = listOf(colony).toWire(emptyList(), emptyList()).single()

        wire.ownerType shouldBe 0
        wire.owner shouldBe -1
    }

    test("toWire preserves a Barbarian owner's tribeIndex when it isn't the default") {
        val colony = Colony(x = 10, y = 20, owner = Owner.Barbarian(tribeIndex = 5))

        val wire = listOf(colony).toWire(emptyList(), emptyList()).single()

        wire.ownerType shouldBe 1
        wire.owner shouldBe 5
    }

    test("toWire throws on a dangling Civilization race reference") {
        val colony = Colony(x = 10, y = 20, owner = Owner.Civilization(race("Outsider")))

        shouldThrow<IllegalArgumentException> {
            listOf(colony).toWire(emptyList(), emptyList())
        }
    }

    test("toWire throws on a dangling Player lead reference") {
        val colony = Colony(x = 10, y = 20, owner = Owner.Player(leadEntry("Outsider")))

        shouldThrow<IllegalArgumentException> {
            listOf(colony).toWire(emptyList(), emptyList())
        }
    }
})
