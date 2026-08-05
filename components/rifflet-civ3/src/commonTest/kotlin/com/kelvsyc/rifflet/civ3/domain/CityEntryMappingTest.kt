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
    ownerType: Int = 2,
    owner: Int = 0,
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
})
