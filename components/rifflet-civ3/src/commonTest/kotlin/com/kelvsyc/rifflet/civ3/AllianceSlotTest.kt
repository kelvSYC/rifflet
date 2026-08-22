package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AllianceSlotTest : FunSpec({

    test("indices are stable, 0-4 in slot declaration order") {
        AllianceSlot.NONE.index shouldBe 0
        AllianceSlot.ALLIANCE_1.index shouldBe 1
        AllianceSlot.ALLIANCE_2.index shouldBe 2
        AllianceSlot.ALLIANCE_3.index shouldBe 3
        AllianceSlot.ALLIANCE_4.index shouldBe 4
    }

    test("every slot has a unique index, and the slot count matches GameLockedAlliance's fixed cardinality") {
        val indices = AllianceSlot.entries.map { it.index }
        indices.toSet().size shouldBe indices.size
        indices.size shouldBe 5
    }
})
