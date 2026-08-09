package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.LeadEntry
import com.kelvsyc.rifflet.civ3.PrtoDomain
import com.kelvsyc.rifflet.civ3.RaceCultureGroup
import com.kelvsyc.rifflet.civ3.RaceLeader
import com.kelvsyc.rifflet.civ3.UnitEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun unitEntry(
    ownerType: Int = 3,
    owner: Int = -1,
    unitType: Int = -1,
    aiStrategy: Int = -1,
    experienceLevel: Int = -1,
    useCivilizationKing: Int = 0,
    legacyName: String = "",
    ptwName: String = "",
): UnitEntry = UnitEntry(
    legacyName = legacyName, ownerType = ownerType, experienceLevel = experienceLevel, owner = owner,
    unitType = unitType, aiStrategy = aiStrategy, x = 10, y = 20, ptwName = ptwName,
    useCivilizationKing = useCivilizationKing,
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

private fun prto(name: String = "", aiStrategies: Int = 0): Prto = Prto(
    name = name, civilopediaEntry = "", iconIndex = 0, type = PrtoDomain.LAND, aiStrategies = aiStrategies,
)

private fun exprEntry(name: String = ""): ExperienceLevel = ExperienceLevel(name = name, baseHitPoints = 0, retreatBonus = 0)

class UnitEntryMappingTest : FunSpec({

    test("toDomain maps scalar fields straight across") {
        val entry = unitEntry(legacyName = "Phalanx", ptwName = "Legion")

        val unit = listOf(entry).toDomain(emptyList(), emptyList(), emptyList(), emptyList()).single()

        unit.x shouldBe 10
        unit.y shouldBe 20
        unit.legacyName shouldBe "Phalanx"
        unit.ptwName shouldBe "Legion"
        unit.name shouldBe "Legion"
    }

    test("toDomain resolves owner as a Civilization") {
        val r = race("Rome")
        val entry = unitEntry(ownerType = 2, owner = 1)

        val unit = listOf(entry).toDomain(listOf(race("Egypt"), r), emptyList(), emptyList(), emptyList()).single()

        unit.owner shouldBe Owner.Civilization(r, unresolvedIndex = 1)
    }

    test("toDomain resolves owner as a Player") {
        val lead = leadEntry("Caesar")
        val entry = unitEntry(ownerType = 3, owner = 0)

        val unit = listOf(entry).toDomain(emptyList(), listOf(lead), emptyList(), emptyList()).single()

        unit.owner shouldBe Owner.Player(lead, unresolvedIndex = 0)
    }

    test("toDomain resolves owner as Barbarian, preserving tribeIndex") {
        val entry = unitEntry(ownerType = 1, owner = 7)

        val unit = listOf(entry).toDomain(emptyList(), emptyList(), emptyList(), emptyList()).single()

        unit.owner shouldBe Owner.Barbarian(tribeIndex = 7)
    }

    test("toDomain resolves unitType against prtos") {
        val p = prto("Warrior")
        val entry = unitEntry(unitType = 0)

        val unit = listOf(entry).toDomain(emptyList(), emptyList(), listOf(p), emptyList()).single()

        unit.unitType shouldBe p
    }

    test("toDomain resolves unitType to null for a dangling index") {
        val entry = unitEntry(unitType = 5)

        val unit = listOf(entry).toDomain(emptyList(), emptyList(), emptyList(), emptyList()).single()

        unit.unitType shouldBe null
    }

    test("toDomain resolves experienceLevel against experienceLevels") {
        val expr = exprEntry("Veteran")
        val entry = unitEntry(experienceLevel = 0)

        val unit = listOf(entry).toDomain(emptyList(), emptyList(), emptyList(), listOf(expr)).single()

        unit.experienceLevel shouldBe expr
    }

    test("toDomain resolves experienceLevel to null for a dangling index") {
        val entry = unitEntry(experienceLevel = 5)

        val unit = listOf(entry).toDomain(emptyList(), emptyList(), emptyList(), emptyList()).single()

        unit.experienceLevel shouldBe null
    }

    test("toDomain resolves aiStrategy to null for -1") {
        val entry = unitEntry(aiStrategy = -1)

        val unit = listOf(entry).toDomain(emptyList(), emptyList(), emptyList(), emptyList()).single()

        unit.aiStrategy shouldBe null
    }

    test("toDomain resolves aiStrategy to the matching AiStrategy constant when the prototype's bit is set") {
        val p = prto(aiStrategies = 1 shl 3)
        val entry = unitEntry(unitType = 0, aiStrategy = 3)

        val unit = listOf(entry).toDomain(emptyList(), emptyList(), listOf(p), emptyList()).single()

        unit.aiStrategy shouldBe AiStrategy.EXPLORE
    }

    test("toDomain resolves aiStrategy even when unitType is dangling") {
        val entry = unitEntry(unitType = 5, aiStrategy = 3)

        val unit = listOf(entry).toDomain(emptyList(), emptyList(), emptyList(), emptyList()).single()

        unit.aiStrategy shouldBe AiStrategy.EXPLORE
    }

    test("toDomain maps useCivilizationKing to a Boolean") {
        val units = listOf(unitEntry(useCivilizationKing = 1), unitEntry(useCivilizationKing = 0))
            .toDomain(emptyList(), emptyList(), emptyList(), emptyList())

        units[0].useCivilizationKing shouldBe true
        units[1].useCivilizationKing shouldBe false
    }

    test("toDomain throws for a None ownerType") {
        val entry = unitEntry(ownerType = 0)

        shouldThrow<IllegalArgumentException> {
            listOf(entry).toDomain(emptyList(), emptyList(), emptyList(), emptyList())
        }
    }

    test("toDomain throws for the barbarian placeholder civ") {
        val entry = unitEntry(ownerType = 2, owner = 0)

        shouldThrow<IllegalArgumentException> {
            listOf(entry).toDomain(emptyList(), emptyList(), emptyList(), emptyList())
        }
    }

    test("toDomain throws when aiStrategy's bit is not set in the resolved prototype's aiStrategies") {
        val p = prto(aiStrategies = 1 shl 0)
        val entry = unitEntry(unitType = 0, aiStrategy = 3)

        shouldThrow<IllegalArgumentException> {
            listOf(entry).toDomain(emptyList(), emptyList(), listOf(p), emptyList())
        }
    }

    test("toDomain().toWire() round-trips scalar fields and a Civilization owner") {
        val r = race("Rome")
        val entries = listOf(unitEntry(ownerType = 2, owner = 1, legacyName = "Phalanx", ptwName = "Legion"))

        val roundTripped = entries.toDomain(listOf(race("Egypt"), r), emptyList(), emptyList(), emptyList())
            .toWire(listOf(race("Egypt"), r), emptyList(), emptyList(), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips a Player owner") {
        val lead = leadEntry("Caesar")
        val entries = listOf(unitEntry(ownerType = 3, owner = 0))

        val roundTripped = entries.toDomain(emptyList(), listOf(lead), emptyList(), emptyList())
            .toWire(emptyList(), listOf(lead), emptyList(), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips a Barbarian owner's tribeIndex") {
        val entries = listOf(unitEntry(ownerType = 1, owner = 7))

        val roundTripped = entries.toDomain(emptyList(), emptyList(), emptyList(), emptyList())
            .toWire(emptyList(), emptyList(), emptyList(), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips unitType") {
        val p = prto("Warrior")
        val entries = listOf(unitEntry(unitType = 0))

        val roundTripped = entries.toDomain(emptyList(), emptyList(), listOf(p), emptyList())
            .toWire(emptyList(), emptyList(), listOf(p), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() writes back -1 for a dangling unitType") {
        val entries = listOf(unitEntry(unitType = 5))

        val wire = entries.toDomain(emptyList(), emptyList(), emptyList(), emptyList())
            .toWire(emptyList(), emptyList(), emptyList(), emptyList())
            .single()

        wire.unitType shouldBe -1
    }

    test("toDomain().toWire() round-trips experienceLevel") {
        val expr = exprEntry("Veteran")
        val entries = listOf(unitEntry(experienceLevel = 0))

        val roundTripped = entries.toDomain(emptyList(), emptyList(), emptyList(), listOf(expr))
            .toWire(emptyList(), emptyList(), emptyList(), listOf(expr))

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips aiStrategy") {
        val p = prto(aiStrategies = 1 shl 5)
        val entries = listOf(unitEntry(unitType = 0, aiStrategy = 5))

        val roundTripped = entries.toDomain(emptyList(), emptyList(), listOf(p), emptyList())
            .toWire(emptyList(), emptyList(), listOf(p), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips useCivilizationKing") {
        val entries = listOf(unitEntry(useCivilizationKing = 1))

        val roundTripped = entries.toDomain(emptyList(), emptyList(), emptyList(), emptyList())
            .toWire(emptyList(), emptyList(), emptyList(), emptyList())

        roundTripped shouldBe entries
    }

    test("toWire throws on a dangling Civilization race reference") {
        val unit = PlacedUnit(x = 10, y = 20, owner = Owner.Civilization(race("Outsider")))

        shouldThrow<IllegalArgumentException> {
            listOf(unit).toWire(emptyList(), emptyList(), emptyList(), emptyList())
        }
    }

    test("toWire throws on a dangling Player lead reference") {
        val unit = PlacedUnit(x = 10, y = 20, owner = Owner.Player(leadEntry("Outsider")))

        shouldThrow<IllegalArgumentException> {
            listOf(unit).toWire(emptyList(), emptyList(), emptyList(), emptyList())
        }
    }

    test("toWire throws on a dangling unitType reference") {
        val unit = PlacedUnit(x = 10, y = 20, unitType = prto("Outsider"))

        shouldThrow<IllegalArgumentException> {
            listOf(unit).toWire(emptyList(), emptyList(), emptyList(), emptyList())
        }
    }

    test("toWire throws on a dangling experienceLevel reference") {
        val unit = PlacedUnit(x = 10, y = 20, experienceLevel = exprEntry("Outsider"))

        shouldThrow<IllegalArgumentException> {
            listOf(unit).toWire(emptyList(), emptyList(), emptyList(), emptyList())
        }
    }
})
