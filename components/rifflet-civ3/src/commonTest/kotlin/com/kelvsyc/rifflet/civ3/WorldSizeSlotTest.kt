package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class WorldSizeSlotTest : FunSpec({

    test("indices are stable, 0-4 in slot declaration order") {
        WorldSizeSlot.TINY.index shouldBe 0
        WorldSizeSlot.SMALL.index shouldBe 1
        WorldSizeSlot.STANDARD.index shouldBe 2
        WorldSizeSlot.LARGE.index shouldBe 3
        WorldSizeSlot.HUGE.index shouldBe 4
    }

    test("every slot has a unique index, and the slot count matches WSIZ's fixed cardinality") {
        val indices = WorldSizeSlot.entries.map { it.index }
        indices.toSet().size shouldBe indices.size
        indices.size shouldBe 5
    }
})
