package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class EraSlotTest : FunSpec({

    test("indices are stable, 0-3 in slot declaration order") {
        EraSlot.ANCIENT_TIMES.index shouldBe 0
        EraSlot.MIDDLE_AGES.index shouldBe 1
        EraSlot.INDUSTRIAL_AGES.index shouldBe 2
        EraSlot.MODERN_TIMES.index shouldBe 3
    }

    test("every slot has a unique index, and the slot count matches ERAS's fixed cardinality") {
        val indices = EraSlot.entries.map { it.index }
        indices.toSet().size shouldBe indices.size
        indices.size shouldBe 4
    }
})
