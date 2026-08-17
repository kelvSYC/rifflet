package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FlavorSlotTest : FunSpec({

    test("indices are stable, 0-6 in slot declaration order") {
        FlavorSlot.FLAVOR_1.index shouldBe 0
        FlavorSlot.FLAVOR_2.index shouldBe 1
        FlavorSlot.FLAVOR_3.index shouldBe 2
        FlavorSlot.FLAVOR_4.index shouldBe 3
        FlavorSlot.FLAVOR_5.index shouldBe 4
        FlavorSlot.FLAVOR_6.index shouldBe 5
        FlavorSlot.FLAVOR_7.index shouldBe 6
    }

    test("every slot has a unique index, and the slot count matches FLAV's fixed cardinality") {
        val indices = FlavorSlot.entries.map { it.index }
        indices.toSet().size shouldBe indices.size
        indices.size shouldBe 7
    }
})
