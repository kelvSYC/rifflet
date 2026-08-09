package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.ExperienceLevelSlot
import com.kelvsyc.rifflet.civ3.ExprEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun exprEntry(
    name: String = "",
    baseHitPoints: Int = 0,
    retreatBonus: Int = 0,
): ExprEntry = ExprEntry(name = name, baseHitPoints = baseHitPoints, retreatBonus = retreatBonus)

private fun fourExprEntries(): List<ExprEntry> = listOf(
    exprEntry(name = "Conscript", baseHitPoints = 1, retreatBonus = 0),
    exprEntry(name = "Regular", baseHitPoints = 2, retreatBonus = 10),
    exprEntry(name = "Veteran", baseHitPoints = 3, retreatBonus = 20),
    exprEntry(name = "Elite", baseHitPoints = 4, retreatBonus = 30),
)

class ExprEntryMappingTest : FunSpec({

    test("toDomain requires exactly 4 entries") {
        shouldThrow<IllegalArgumentException> {
            fourExprEntries().drop(1).toDomain()
        }
        fourExprEntries().toDomain().size shouldBe 4
    }

    test("toDomain maps scalar fields straight across, keyed by ExperienceLevelSlot") {
        val byIndex = fourExprEntries().toDomain()

        val veteran = byIndex.getValue(ExperienceLevelSlot.VETERAN)
        veteran.name shouldBe "Veteran"
        veteran.baseHitPoints shouldBe 3
        veteran.retreatBonus shouldBe 20
    }

    test("toDomain().toWire() round-trips") {
        val entries = fourExprEntries()

        val roundTripped = entries.toDomain().toWire()

        roundTripped shouldBe entries
    }

    test("toWire requires exactly the 4 slot keys") {
        val incomplete = fourExprEntries().toDomain().filterKeys { it != ExperienceLevelSlot.ELITE }

        shouldThrow<IllegalArgumentException> {
            incomplete.toWire()
        }
    }

    test("toOrderedList returns ExperienceLevel values ordered by wire index") {
        val byIndex = fourExprEntries().toDomain()

        val ordered = byIndex.toOrderedList()

        ordered.size shouldBe 4
        ordered[0] shouldBe byIndex.getValue(ExperienceLevelSlot.CONSCRIPT)
        ordered[3] shouldBe byIndex.getValue(ExperienceLevelSlot.ELITE)
    }
})
