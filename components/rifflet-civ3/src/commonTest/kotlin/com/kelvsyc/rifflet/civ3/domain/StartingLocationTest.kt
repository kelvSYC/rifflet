package com.kelvsyc.rifflet.civ3.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StartingLocationTest : FunSpec({

    test("constructing with only required params gives a sensible default") {
        val location = StartingLocation(x = 10, y = 20)

        location.owner shouldBe Owner.None
    }

    test("fields are mutable after construction") {
        val location = StartingLocation(x = 10, y = 20)

        location.x = 30
        location.y = 40
        location.owner = Owner.Barbarian

        location.x shouldBe 30
        location.y shouldBe 40
        location.owner shouldBe Owner.Barbarian
    }
})
