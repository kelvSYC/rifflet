package com.kelvsyc.rifflet.civ3.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BldgUnitsProducedTest : FunSpec({

    test("constructing with no args gives sensible defaults") {
        val unitsProduced = BldgUnitsProduced()

        unitsProduced.unitProduced shouldBe null
        unitsProduced.unitFrequency shouldBe 0
    }

    test("fields are mutable after construction") {
        val unitsProduced = BldgUnitsProduced()

        unitsProduced.unitFrequency = 5

        unitsProduced.unitFrequency shouldBe 5
    }
})
