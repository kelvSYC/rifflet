package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.CultEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun cultEntry(name: String = "", continuedResistanceChance: Int = 0): CultEntry = CultEntry(
    name = name, chanceOfSuccessfulPropaganda = 1, cultureRatioPercentage = 2,
    cultureRatioDenominator = 3, cultureRatioNumerator = 4, initialResistanceChance = 5,
    continuedResistanceChance = continuedResistanceChance,
)

class CultEntryMappingTest : FunSpec({

    test("toDomain maps scalar fields straight across") {
        val entry = cultEntry(name = "Repressive", continuedResistanceChance = 6)

        val level = listOf(entry).toDomain().single()

        level.name shouldBe "Repressive"
        level.chanceOfSuccessfulPropaganda shouldBe 1
        level.cultureRatioPercentage shouldBe 2
        level.cultureRatioDenominator shouldBe 3
        level.cultureRatioNumerator shouldBe 4
        level.initialResistanceChance shouldBe 5
        level.continuedResistanceChance shouldBe 6
    }

    test("toDomain().toWire() round-trips") {
        val entries = listOf(
            cultEntry(name = "Repressive"),
            cultEntry(name = "Tolerant", continuedResistanceChance = 9),
        )

        val roundTripped = entries.toDomain().toWire()

        roundTripped shouldBe entries
    }
})
