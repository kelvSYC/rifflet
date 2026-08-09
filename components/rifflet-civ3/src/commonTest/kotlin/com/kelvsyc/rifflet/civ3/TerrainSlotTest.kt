package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TerrainSlotTest : FunSpec({

    test("indices 0-8 are stable across every era") {
        val stableSlots = listOf(
            TerrainSlot.DESERT to 0, TerrainSlot.PLAINS to 1, TerrainSlot.GRASSLAND to 2,
            TerrainSlot.TUNDRA to 3, TerrainSlot.FLOOD_PLAIN to 4, TerrainSlot.HILLS to 5,
            TerrainSlot.MOUNTAINS to 6, TerrainSlot.FOREST to 7, TerrainSlot.JUNGLE to 8,
        )
        for ((slot, expected) in stableSlots) {
            slot.index(Civ3FormatEra.VANILLA) shouldBe expected
            slot.index(Civ3FormatEra.PTW) shouldBe expected
            slot.index(Civ3FormatEra.CONQUESTS) shouldBe expected
        }
    }

    test("MARSH and VOLCANO exist only in CONQUESTS") {
        TerrainSlot.MARSH.index(Civ3FormatEra.VANILLA) shouldBe null
        TerrainSlot.MARSH.index(Civ3FormatEra.PTW) shouldBe null
        TerrainSlot.MARSH.index(Civ3FormatEra.CONQUESTS) shouldBe 9

        TerrainSlot.VOLCANO.index(Civ3FormatEra.VANILLA) shouldBe null
        TerrainSlot.VOLCANO.index(Civ3FormatEra.PTW) shouldBe null
        TerrainSlot.VOLCANO.index(Civ3FormatEra.CONQUESTS) shouldBe 10
    }

    test("COAST/SEA/OCEAN sit at 9/10/11 in legacy eras and shift to 11/12/13 in CONQUESTS") {
        TerrainSlot.COAST.index(Civ3FormatEra.VANILLA) shouldBe 9
        TerrainSlot.SEA.index(Civ3FormatEra.VANILLA) shouldBe 10
        TerrainSlot.OCEAN.index(Civ3FormatEra.VANILLA) shouldBe 11

        TerrainSlot.COAST.index(Civ3FormatEra.PTW) shouldBe 9
        TerrainSlot.SEA.index(Civ3FormatEra.PTW) shouldBe 10
        TerrainSlot.OCEAN.index(Civ3FormatEra.PTW) shouldBe 11

        TerrainSlot.COAST.index(Civ3FormatEra.CONQUESTS) shouldBe 11
        TerrainSlot.SEA.index(Civ3FormatEra.CONQUESTS) shouldBe 12
        TerrainSlot.OCEAN.index(Civ3FormatEra.CONQUESTS) shouldBe 13
    }

    test("every slot valid for an era has a unique index, and the valid-slot count matches the era's TERR cardinality") {
        for (era in Civ3FormatEra.entries) {
            val indices = TerrainSlot.entries.mapNotNull { it.index(era) }
            indices.toSet().size shouldBe indices.size
            val expectedCount = if (era == Civ3FormatEra.CONQUESTS) 14 else 12
            indices.size shouldBe expectedCount
        }
    }
})
