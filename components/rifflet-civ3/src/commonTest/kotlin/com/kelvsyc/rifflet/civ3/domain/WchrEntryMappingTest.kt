package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Age
import com.kelvsyc.rifflet.civ3.BarbarianActivity
import com.kelvsyc.rifflet.civ3.Climate
import com.kelvsyc.rifflet.civ3.Landform
import com.kelvsyc.rifflet.civ3.OceanCoverage
import com.kelvsyc.rifflet.civ3.Temperature
import com.kelvsyc.rifflet.civ3.WchrEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun wchrEntry(worldSize: Int = 0): WchrEntry = WchrEntry(
    selectedClimate = Climate.ARID, actualClimate = Climate.WET,
    selectedBarbarianActivity = BarbarianActivity.SEDENTARY, actualBarbarianActivity = BarbarianActivity.ROAMING,
    selectedLandform = Landform.PANGAEA, actualLandform = Landform.CONTINENTS,
    selectedOceanCoverage = OceanCoverage.SIXTY_PERCENT, actualOceanCoverage = OceanCoverage.SEVENTY_PERCENT,
    selectedTemperature = Temperature.COOL, actualTemperature = Temperature.WARM,
    selectedAge = Age.THREE_BILLION_YEARS, actualAge = Age.FIVE_BILLION_YEARS,
    worldSize = worldSize,
)

private fun worldSizePreset(name: String): WorldSizePreset = WorldSizePreset(name = name)

class WchrEntryMappingTest : FunSpec({

    test("toDomain maps every selected/actual pair straight across") {
        val entry = wchrEntry()

        val settings = listOf(entry).toDomain(emptyList()).single()

        settings.climate shouldBe GeneratedChoice(Climate.ARID, Climate.WET)
        settings.barbarianActivity shouldBe GeneratedChoice(BarbarianActivity.SEDENTARY, BarbarianActivity.ROAMING)
        settings.landform shouldBe GeneratedChoice(Landform.PANGAEA, Landform.CONTINENTS)
        settings.oceanCoverage shouldBe GeneratedChoice(OceanCoverage.SIXTY_PERCENT, OceanCoverage.SEVENTY_PERCENT)
        settings.temperature shouldBe GeneratedChoice(Temperature.COOL, Temperature.WARM)
        settings.age shouldBe GeneratedChoice(Age.THREE_BILLION_YEARS, Age.FIVE_BILLION_YEARS)
    }

    test("toDomain resolves worldSize against the supplied WSIZ list, null for a dangling index") {
        val standard = worldSizePreset("Standard")
        val entries = listOf(wchrEntry(worldSize = 0), wchrEntry(worldSize = 5))

        val settingsList = entries.toDomain(listOf(standard))

        settingsList[0].worldSize shouldBe standard
        settingsList[1].worldSize shouldBe null
    }

    test("toDomain().toWire() round-trips") {
        val standard = worldSizePreset("Standard")
        val entries = listOf(wchrEntry(worldSize = 0))

        val roundTripped = entries.toDomain(listOf(standard)).toWire(listOf(standard))

        roundTripped shouldBe entries
    }

    test("toWire writes -1 for a null worldSize") {
        val entry = wchrEntry(worldSize = -1)

        val settings = listOf(entry).toDomain(emptyList()).single()
        val wire = listOf(settings).toWire(emptyList()).single()

        wire.worldSize shouldBe -1
    }

    test("toWire throws on a dangling worldSize reference") {
        val standard = worldSizePreset("Standard")
        val settings = listOf(wchrEntry(worldSize = 0)).toDomain(listOf(standard)).single()
        val outsider = worldSizePreset("Outsider")
        settings.worldSize = outsider

        shouldThrow<IllegalArgumentException> { listOf(settings).toWire(listOf(standard)) }
    }
})
