package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.CityEntry
import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.LeadEntry
import com.kelvsyc.rifflet.civ3.RaceCultureGroup
import com.kelvsyc.rifflet.civ3.RaceLeader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun cityEntry(
    name: String = "",
    hasWalls: Byte = 0,
    hasPalace: Byte = 0,
    ownerType: Int = 0,
    owner: Int = -1,
    buildingIds: List<Int> = emptyList(),
): CityEntry = CityEntry(
    hasWalls = hasWalls, hasPalace = hasPalace, name = name, ownerType = ownerType,
    buildingIds = buildingIds, culture = 0, owner = owner, size = 0, x = 10, y = 20,
    cityLevel = 0, borderLevel = 0, useAutoName = 0,
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

private fun improvement(name: String = ""): Improvement = Improvement(
    description = "", name = name, civilopediaEntry = "", cost = 0, culture = 0,
    maintenanceCost = 0, pollution = 0, production = 0,
)

class CityEntryMappingTest : FunSpec({

    test("toDomain maps scalar fields straight across") {
        val entry = cityEntry(name = "Rome", hasWalls = 1, hasPalace = 1)

        val city = listOf(entry).toDomain(emptyList(), emptyList(), emptyList()).single()

        city.name shouldBe "Rome"
        city.hasWalls shouldBe true
        city.hasPalace shouldBe true
        city.x shouldBe 10
        city.y shouldBe 20
    }

    test("toDomain resolves owner as a Civilization") {
        val r = race("Rome")
        val entry = cityEntry(ownerType = 2, owner = 0)

        val city = listOf(entry).toDomain(listOf(r), emptyList(), emptyList()).single()

        city.owner shouldBe Owner.Civilization(r)
    }

    test("toDomain resolves owner as a Player") {
        val lead = leadEntry("Caesar")
        val entry = cityEntry(ownerType = 3, owner = 0)

        val city = listOf(entry).toDomain(emptyList(), listOf(lead), emptyList()).single()

        city.owner shouldBe Owner.Player(lead)
    }

    test("toDomain resolves buildings preserving position and nulls for dangling ids") {
        val granary = improvement("Granary")
        val entry = cityEntry(buildingIds = listOf(0, 99))

        val city = listOf(entry).toDomain(emptyList(), emptyList(), listOf(granary)).single()

        city.buildings shouldBe mutableListOf(granary, null)
    }

    test("toDomain throws for an out-of-range ownerType") {
        val entry = cityEntry(ownerType = 4)

        shouldThrow<IllegalArgumentException> {
            listOf(entry).toDomain(emptyList(), emptyList(), emptyList())
        }
    }

    test("toDomain().toWire() round-trips scalar fields and a Civilization owner") {
        val r = race("Rome")
        val entries = listOf(cityEntry(name = "Rome", hasWalls = 1, ownerType = 2, owner = 0))

        val roundTripped = entries.toDomain(listOf(r), emptyList(), emptyList())
            .toWire(listOf(r), emptyList(), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips a Player owner") {
        val lead = leadEntry("Caesar")
        val entries = listOf(cityEntry(ownerType = 3, owner = 0))

        val roundTripped = entries.toDomain(emptyList(), listOf(lead), emptyList())
            .toWire(emptyList(), listOf(lead), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips buildings preserving position and duplicates") {
        val granary = improvement("Granary")
        val entries = listOf(cityEntry(buildingIds = listOf(0, 0)))

        val roundTripped = entries.toDomain(emptyList(), emptyList(), listOf(granary))
            .toWire(emptyList(), emptyList(), listOf(granary))

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() writes back -1 for a None owner's sentinel") {
        val city = City(name = "Ruins", x = 0, y = 0, owner = Owner.None)

        val wire = listOf(city).toWire(emptyList(), emptyList(), emptyList()).single()

        wire.ownerType shouldBe 0
        wire.owner shouldBe -1
    }

    test("toDomain().toWire() writes back -1 for a Barbarian owner's sentinel") {
        val city = City(name = "Camp", x = 0, y = 0, owner = Owner.Barbarian)

        val wire = listOf(city).toWire(emptyList(), emptyList(), emptyList()).single()

        wire.ownerType shouldBe 1
        wire.owner shouldBe -1
    }

    test("toWire throws on a dangling Civilization race reference") {
        val city = City(name = "Rome", x = 0, y = 0, owner = Owner.Civilization(race("Outsider")))

        shouldThrow<IllegalArgumentException> {
            listOf(city).toWire(emptyList(), emptyList(), emptyList())
        }
    }

    test("toWire throws on a dangling Player lead reference") {
        val city = City(name = "Rome", x = 0, y = 0, owner = Owner.Player(leadEntry("Outsider")))

        shouldThrow<IllegalArgumentException> {
            listOf(city).toWire(emptyList(), emptyList(), emptyList())
        }
    }

    test("toWire throws on a dangling building reference") {
        val city = City(name = "Rome", x = 0, y = 0, buildings = mutableListOf(improvement("Outsider")))

        shouldThrow<IllegalArgumentException> {
            listOf(city).toWire(emptyList(), emptyList(), emptyList())
        }
    }

    test("toWire writes -1 for a null building slot") {
        val city = City(name = "Rome", x = 0, y = 0, buildings = mutableListOf(null))

        val wire = listOf(city).toWire(emptyList(), emptyList(), emptyList()).single()

        wire.buildingIds shouldBe listOf(-1)
    }
})
