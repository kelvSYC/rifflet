package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class WorkerJobSlotTest : FunSpec({

    test("indices 0-8 are stable across every era") {
        val stableSlots = listOf(
            WorkerJobSlot.MINE to 0, WorkerJobSlot.IRRIGATION to 1, WorkerJobSlot.FORTRESS to 2,
            WorkerJobSlot.ROAD to 3, WorkerJobSlot.RAILROAD to 4, WorkerJobSlot.PLANT_FOREST to 5,
            WorkerJobSlot.CLEAR_FOREST to 6, WorkerJobSlot.CLEAR_WETLANDS to 7, WorkerJobSlot.CLEAR_DAMAGE to 8,
        )
        for ((slot, expected) in stableSlots) {
            slot.index(Civ3FormatEra.VANILLA) shouldBe expected
            slot.index(Civ3FormatEra.PTW) shouldBe expected
            slot.index(Civ3FormatEra.CONQUESTS) shouldBe expected
        }
    }

    test("AIRFIELD/RADAR_TOWER/OUTPOST require at least PTW") {
        WorkerJobSlot.AIRFIELD.index(Civ3FormatEra.VANILLA) shouldBe null
        WorkerJobSlot.AIRFIELD.index(Civ3FormatEra.PTW) shouldBe 9
        WorkerJobSlot.AIRFIELD.index(Civ3FormatEra.CONQUESTS) shouldBe 9

        WorkerJobSlot.RADAR_TOWER.index(Civ3FormatEra.VANILLA) shouldBe null
        WorkerJobSlot.RADAR_TOWER.index(Civ3FormatEra.PTW) shouldBe 10
        WorkerJobSlot.RADAR_TOWER.index(Civ3FormatEra.CONQUESTS) shouldBe 10

        WorkerJobSlot.OUTPOST.index(Civ3FormatEra.VANILLA) shouldBe null
        WorkerJobSlot.OUTPOST.index(Civ3FormatEra.PTW) shouldBe 11
        WorkerJobSlot.OUTPOST.index(Civ3FormatEra.CONQUESTS) shouldBe 11
    }

    test("BARRICADE requires CONQUESTS") {
        WorkerJobSlot.BARRICADE.index(Civ3FormatEra.VANILLA) shouldBe null
        WorkerJobSlot.BARRICADE.index(Civ3FormatEra.PTW) shouldBe null
        WorkerJobSlot.BARRICADE.index(Civ3FormatEra.CONQUESTS) shouldBe 12
    }

    test("every slot valid for an era has a unique index, and the valid-slot count matches the era's TFRM cardinality") {
        for (era in Civ3FormatEra.entries) {
            val indices = WorkerJobSlot.entries.mapNotNull { it.index(era) }
            indices.toSet().size shouldBe indices.size
            val expectedCount = when (era) {
                Civ3FormatEra.VANILLA -> 9
                Civ3FormatEra.PTW -> 12
                Civ3FormatEra.CONQUESTS -> 13
            }
            indices.size shouldBe expectedCount
        }
    }
})
