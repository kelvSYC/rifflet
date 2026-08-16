package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.DiffEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun diffEntry(name: String = "", militaryLaw: Int = 0): DiffEntry = DiffEntry(
    name = name, numberOfCitizensBornContent = 1, maxGovernmentTransitionTime = 2,
    numberOfAiDefensiveStartingUnits = 3, numberOfAiOffensiveStartingUnits = 4,
    extraStartUnit1 = 5, extraStartUnit2 = 6, additionalFreeSupport = 7,
    unitSupportBonusForEachSettlement = 8, attackBonusAgainstBarbarians = 9, costFactor = 10,
    percentageOfOptimalCities = 11, aiToAiTradeRate = 12, corruptionPercentage = 13, militaryLaw = militaryLaw,
)

class DiffEntryMappingTest : FunSpec({

    test("toDomain maps scalar fields straight across") {
        val entry = diffEntry(name = "Deity", militaryLaw = 14)

        val difficulty = listOf(entry).toDomain().single()

        difficulty.name shouldBe "Deity"
        difficulty.numberOfCitizensBornContent shouldBe 1
        difficulty.maxGovernmentTransitionTime shouldBe 2
        difficulty.numberOfAiDefensiveStartingUnits shouldBe 3
        difficulty.numberOfAiOffensiveStartingUnits shouldBe 4
        difficulty.extraStartUnit1 shouldBe 5
        difficulty.extraStartUnit2 shouldBe 6
        difficulty.additionalFreeSupport shouldBe 7
        difficulty.unitSupportBonusForEachSettlement shouldBe 8
        difficulty.attackBonusAgainstBarbarians shouldBe 9
        difficulty.costFactor shouldBe 10
        difficulty.percentageOfOptimalCities shouldBe 11
        difficulty.aiToAiTradeRate shouldBe 12
        difficulty.corruptionPercentage shouldBe 13
        difficulty.militaryLaw shouldBe 14
    }

    test("toDomain().toWire() round-trips") {
        val entries = listOf(diffEntry(name = "Chieftain"), diffEntry(name = "Deity", militaryLaw = 5))

        val roundTripped = entries.toDomain().toWire()

        roundTripped shouldBe entries
    }
})
