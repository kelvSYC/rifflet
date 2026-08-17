package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.WorldSizeSlot
import com.kelvsyc.rifflet.civ3.WsizEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun wsizEntry(
    name: String = "",
    optimalNumberOfCities: Int = 0,
    techRate: Int = 0,
    height: Int = 0,
    distanceBetweenCivs: Int = 0,
    numberOfCivs: Int = 0,
    width: Int = 0,
    reserved: ByteString = ByteString.of(*ByteArray(24)),
): WsizEntry = WsizEntry(
    optimalNumberOfCities = optimalNumberOfCities, techRate = techRate, reserved = reserved,
    name = name, height = height, distanceBetweenCivs = distanceBetweenCivs,
    numberOfCivs = numberOfCivs, width = width,
)

private fun fiveWsizEntries(): List<WsizEntry> = listOf(
    wsizEntry(name = "Tiny"),
    wsizEntry(name = "Small"),
    wsizEntry(name = "Standard"),
    wsizEntry(name = "Large"),
    wsizEntry(name = "Huge"),
)

class WsizEntryMappingTest : FunSpec({

    test("toDomain requires exactly 5 entries") {
        shouldThrow<IllegalArgumentException> {
            fiveWsizEntries().drop(1).toDomain()
        }
        fiveWsizEntries().toDomain().size shouldBe 5
    }

    test("toDomain maps scalar fields straight across, keyed by WorldSizeSlot") {
        val byIndex = fiveWsizEntries().toDomain()

        val standard = byIndex.getValue(WorldSizeSlot.STANDARD)
        standard.name shouldBe "Standard"
    }

    test("toDomain().toWire() round-trips") {
        val entries = listOf(
            wsizEntry(
                name = "Tiny", optimalNumberOfCities = 1, techRate = 2, height = 3,
                distanceBetweenCivs = 4, numberOfCivs = 5, width = 6,
            ),
            wsizEntry(name = "Small"),
            wsizEntry(name = "Standard"),
            wsizEntry(name = "Large"),
            wsizEntry(name = "Huge"),
        )

        val roundTripped = entries.toDomain().toWire()

        roundTripped shouldBe entries
    }

    test("toWire requires exactly the 5 slot keys") {
        val incomplete = fiveWsizEntries().toDomain().filterKeys { it != WorldSizeSlot.HUGE }

        shouldThrow<IllegalArgumentException> {
            incomplete.toWire()
        }
    }

    test("toOrderedList returns WorldSizePreset values ordered by wire index") {
        val byIndex = fiveWsizEntries().toDomain()

        val ordered = byIndex.toOrderedList()

        ordered.size shouldBe 5
        ordered[0] shouldBe byIndex.getValue(WorldSizeSlot.TINY)
        ordered[4] shouldBe byIndex.getValue(WorldSizeSlot.HUGE)
    }
})
