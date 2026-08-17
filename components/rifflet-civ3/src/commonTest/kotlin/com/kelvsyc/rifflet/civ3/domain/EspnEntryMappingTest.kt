package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.EspnEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun espnEntry(name: String = "", missionFlags: Int = 0): EspnEntry = EspnEntry(
    description = "desc", name = name, civilopediaEntry = "", missionFlags = missionFlags, baseCost = 10,
)

class EspnEntryMappingTest : FunSpec({

    test("toDomain maps scalar fields straight across") {
        val entry = espnEntry(name = "Establish Embassy", missionFlags = 3)

        val mission = listOf(entry).toDomain().single()

        mission.name shouldBe "Establish Embassy"
        mission.description shouldBe "desc"
        mission.missionFlags shouldBe 3
        mission.baseCost shouldBe 10
    }

    test("toDomain().toWire() round-trips") {
        val entries = listOf(
            espnEntry(name = "Establish Embassy"),
            espnEntry(name = "Investigate City", missionFlags = 2),
        )

        val roundTripped = entries.toDomain().toWire()

        roundTripped shouldBe entries
    }
})
