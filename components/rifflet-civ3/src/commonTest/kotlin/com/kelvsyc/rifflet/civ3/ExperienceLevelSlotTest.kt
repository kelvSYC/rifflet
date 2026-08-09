package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ExperienceLevelSlotTest : FunSpec({

    test("indices are stable, 0-3 in slot declaration order") {
        ExperienceLevelSlot.CONSCRIPT.index shouldBe 0
        ExperienceLevelSlot.REGULAR.index shouldBe 1
        ExperienceLevelSlot.VETERAN.index shouldBe 2
        ExperienceLevelSlot.ELITE.index shouldBe 3
    }

    test("every slot has a unique index, and the slot count matches EXPR's fixed cardinality") {
        val indices = ExperienceLevelSlot.entries.map { it.index }
        indices.toSet().size shouldBe indices.size
        indices.size shouldBe 4
    }
})
