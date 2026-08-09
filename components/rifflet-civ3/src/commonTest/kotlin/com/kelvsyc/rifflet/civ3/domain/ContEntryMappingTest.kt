package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.ContEntry
import com.kelvsyc.rifflet.civ3.ContType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ContEntryMappingTest : FunSpec({

    test("toDomain maps scalar fields straight across") {
        val entry = ContEntry(type = ContType.LAND, numberOfTiles = 42)

        val continent = listOf(entry).toDomain().single()

        continent.type shouldBe ContType.LAND
        continent.numberOfTiles shouldBe 42
    }

    test("toDomain().toWire() round-trips") {
        val entries = listOf(
            ContEntry(type = ContType.LAND, numberOfTiles = 42),
            ContEntry(type = ContType.WATER, numberOfTiles = 7),
        )

        val roundTripped = entries.toDomain().toWire()

        roundTripped shouldBe entries
    }
})
