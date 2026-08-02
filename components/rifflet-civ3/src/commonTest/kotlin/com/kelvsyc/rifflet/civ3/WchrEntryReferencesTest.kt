package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun wchrEntry(worldSize: Int): WchrEntry = WchrEntry(
    selectedClimate = Climate.NORMAL,
    actualClimate = Climate.NORMAL,
    selectedBarbarianActivity = BarbarianActivity.NO_BARBARIANS,
    actualBarbarianActivity = BarbarianActivity.NO_BARBARIANS,
    selectedLandform = Landform.CONTINENTS,
    actualLandform = Landform.CONTINENTS,
    selectedOceanCoverage = OceanCoverage.SEVENTY_PERCENT,
    actualOceanCoverage = OceanCoverage.SEVENTY_PERCENT,
    selectedTemperature = Temperature.TEMPERATE,
    actualTemperature = Temperature.TEMPERATE,
    selectedAge = Age.FOUR_BILLION_YEARS,
    actualAge = Age.FOUR_BILLION_YEARS,
    worldSize = worldSize,
)

private fun wsizEntry(name: String): WsizEntry = WsizEntry(
    optimalNumberOfCities = 0,
    techRate = 0,
    reserved = ByteString.of(*ByteArray(24)),
    name = name,
    height = 0,
    distanceBetweenCivs = 0,
    numberOfCivs = 0,
    width = 0,
)

class WchrEntryReferencesTest : FunSpec({

    test("worldSizeWsiz resolves each index to the matching WSIZ entry") {
        val wsiz = listOf("Tiny", "Small", "Standard", "Large", "Huge").map { wsizEntry(it) }
        wsiz.forEachIndexed { index, expected ->
            wchrEntry(worldSize = index).worldSizeWsiz(wsiz) shouldBe expected
        }
    }

    test("worldSizeWsiz returns null for an out-of-range value") {
        val wsiz = listOf("Tiny", "Small", "Standard", "Large", "Huge").map { wsizEntry(it) }
        wchrEntry(worldSize = 5).worldSizeWsiz(wsiz) shouldBe null
    }

    test("worldSizeWsiz returns null when WSIZ is empty") {
        wchrEntry(worldSize = 0).worldSizeWsiz(emptyList()) shouldBe null
    }
})
