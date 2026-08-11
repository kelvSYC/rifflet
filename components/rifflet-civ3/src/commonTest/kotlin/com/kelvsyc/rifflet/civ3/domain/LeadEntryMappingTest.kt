package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.GovtCorruption
import com.kelvsyc.rifflet.civ3.GovtHurrying
import com.kelvsyc.rifflet.civ3.GovtRulerTitles
import com.kelvsyc.rifflet.civ3.GovtUnitSupportCosts
import com.kelvsyc.rifflet.civ3.GovtWarWeariness
import com.kelvsyc.rifflet.civ3.LeadEntry
import com.kelvsyc.rifflet.civ3.LeadStartUnit
import com.kelvsyc.rifflet.civ3.PrtoDomain
import com.kelvsyc.rifflet.civ3.RaceCultureGroup
import com.kelvsyc.rifflet.civ3.RaceLeader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun leadEntry(
    name: String = "",
    startUnits: List<LeadStartUnit> = emptyList(),
    startingTechnologyIds: List<Int> = emptyList(),
    difficulty: Int = -2,
    initialEra: Int = 0,
    government: Int = -1,
    civ: Int = -3,
): LeadEntry = LeadEntry(
    customCivData = 0, humanPlayer = 0, name = name, unknown = ByteString.of(*ByteArray(8)),
    startUnits = startUnits, genderOfLeaderName = 0, startingTechnologyIds = startingTechnologyIds,
    difficulty = difficulty, initialEra = initialEra, startCash = 0, government = government,
    civ = civ, color = 0, skipFirstTurn = 0, unknown2 = ByteString.of(*ByteArray(4)), startEmbassies = 0,
)

private fun tech(name: String): Tech = Tech(name = name, civilopediaEntry = "", cost = 0, advanceIcon = 0, x = 0, y = 0)

private fun race(name: String): Race = Race(
    name = name, civilopediaEntry = "", adjective = "", noun = "",
    leader = RaceLeader(name = "", title = "", gender = Gender.MALE),
    cultureGroup = RaceCultureGroup.NONE, civilizationGender = Gender.MALE,
)

private fun government(name: String): Government = Government(
    name = name, civilopediaEntry = "",
    rulerTitles = GovtRulerTitles(
        male1 = "", female1 = "", male2 = "", female2 = "", male3 = "", female3 = "", male4 = "", female4 = "",
    ),
    corruption = GovtCorruption.MINIMAL,
    hurrying = GovtHurrying.CANNOT_HURRY,
    unitSupportCosts = GovtUnitSupportCosts(
        freeUnits = 0, freeUnitsPerTown = 0, freeUnitsPerCity = 0, freeUnitsPerMetropolis = 0, unitCost = 0,
    ),
    warWeariness = GovtWarWeariness.NONE,
)

private fun prto(name: String): Prto = Prto(name = name, civilopediaEntry = "", iconIndex = 0, type = PrtoDomain.LAND)

private fun difficulty(name: String): Difficulty = Difficulty(name = name)

private fun era(name: String): Era = Era(name = name)

class LeadEntryMappingTest : FunSpec({

    test("toDomain maps scalar fields straight across") {
        val entry = leadEntry(name = "Caesar")

        val leader = listOf(entry).toDomain(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()

        leader.name shouldBe "Caesar"
    }

    test("toDomain resolves civ: Random (-2), Unrestricted (-3), and a specific Race") {
        val rome = race("Rome")
        val entries = listOf(leadEntry(civ = -2), leadEntry(civ = -3), leadEntry(civ = 0))

        val leaders = entries.toDomain(emptyList(), emptyList(), listOf(rome), emptyList(), emptyList(), emptyList())

        leaders[0].civilization shouldBe LeaderCivilization.Random
        leaders[1].civilization shouldBe LeaderCivilization.Unrestricted
        leaders[2].civilization shouldBe LeaderCivilization.Preset(rome)
    }

    test("toDomain resolves civ to a Preset with null Race for a dangling positive index") {
        val entry = leadEntry(civ = 5)

        val leader = listOf(entry).toDomain(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()

        leader.civilization shouldBe LeaderCivilization.Preset(null)
    }

    test("toDomain resolves difficulty: Unrestricted (-2) and a specific Difficulty") {
        val hard = difficulty("Deity")
        val entries = listOf(leadEntry(difficulty = -2), leadEntry(difficulty = 0))

        val leaders = entries.toDomain(emptyList(), emptyList(), emptyList(), emptyList(), listOf(hard), emptyList())

        leaders[0].difficulty shouldBe LeaderDifficulty.Unrestricted
        leaders[1].difficulty shouldBe LeaderDifficulty.Preset(hard)
    }

    test("toDomain resolves government, initialEra against the supplied lists") {
        val despotism = government("Despotism")
        val ancient = era("Ancient Era")
        val entry = leadEntry(government = 0, initialEra = 0)

        val leader = listOf(entry).toDomain(emptyList(), listOf(despotism), emptyList(), emptyList(), emptyList(), listOf(ancient)).single()

        leader.government shouldBe despotism
        leader.initialEra shouldBe ancient
    }

    test("toDomain resolves startUnits against the supplied PRTO list") {
        val warrior = prto("Warrior")
        val entry = leadEntry(startUnits = listOf(LeadStartUnit(quantity = 2, unitType = 0)))

        val leader = listOf(entry).toDomain(emptyList(), emptyList(), emptyList(), listOf(warrior), emptyList(), emptyList()).single()

        leader.startUnits shouldBe mutableListOf(StartUnit(quantity = 2, unitType = warrior))
    }

    test("toDomain resolves startingTechnologies positionally, null for a dangling id") {
        val bronzeWorking = tech("Bronze Working")
        val entry = leadEntry(startingTechnologyIds = listOf(0, 5))

        val leader = listOf(entry).toDomain(listOf(bronzeWorking), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()

        leader.startingTechnologies shouldBe mutableListOf(bronzeWorking, null)
    }

    test("toDomain().toWire() round-trips") {
        val bronzeWorking = tech("Bronze Working")
        val despotism = government("Despotism")
        val rome = race("Rome")
        val warrior = prto("Warrior")
        val hard = difficulty("Deity")
        val ancient = era("Ancient Era")
        val entries = listOf(
            leadEntry(
                name = "Caesar", startUnits = listOf(LeadStartUnit(quantity = 2, unitType = 0)),
                startingTechnologyIds = listOf(0), difficulty = 0, initialEra = 0,
                government = 0, civ = 0,
            ),
        )

        val roundTripped = entries.toDomain(listOf(bronzeWorking), listOf(despotism), listOf(rome), listOf(warrior), listOf(hard), listOf(ancient))
            .toWire(listOf(bronzeWorking), listOf(despotism), listOf(rome), listOf(warrior), listOf(hard), listOf(ancient))

        roundTripped shouldBe entries
    }

    test("toWire writes -2 for Random civilization, and -2 for Unrestricted difficulty") {
        val leaders = listOf(Leader(name = "A", civilization = LeaderCivilization.Random))

        val wire = leaders.toWire(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()

        wire.civ shouldBe -2
        wire.difficulty shouldBe -2
    }

    test("toWire writes -3 for Unrestricted civ") {
        val leaders = listOf(Leader(name = "A", civilization = LeaderCivilization.Unrestricted))

        val wire = leaders.toWire(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()).single()

        wire.civ shouldBe -3
    }

    test("toWire throws on a dangling civilization Preset/difficulty Preset/government/startUnits reference") {
        val withRace = Leader(name = "A", civilization = LeaderCivilization.Preset(race("Outsider")))
        shouldThrow<IllegalArgumentException> { listOf(withRace).toWire(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()) }

        val withGovernment = Leader(name = "A", government = government("Outsider"))
        shouldThrow<IllegalArgumentException> { listOf(withGovernment).toWire(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()) }

        val withUnit = Leader(name = "A", startUnits = mutableListOf(StartUnit(quantity = 1, unitType = prto("Outsider"))))
        shouldThrow<IllegalArgumentException> { listOf(withUnit).toWire(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()) }

        val withDifficulty = Leader(name = "A", difficulty = LeaderDifficulty.Preset(difficulty("Outsider")))
        shouldThrow<IllegalArgumentException> { listOf(withDifficulty).toWire(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()) }
    }
})
