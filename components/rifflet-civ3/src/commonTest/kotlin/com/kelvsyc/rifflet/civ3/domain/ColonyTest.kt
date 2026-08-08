package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.ClnyImprovementType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ColonyTest : FunSpec({

    test("constructing with only required params gives sensible defaults") {
        val colony = Colony(x = 10, y = 20)

        colony.owner shouldBe Owner.None
        colony.improvementType shouldBe ClnyImprovementType.COLONY
    }

    test("fields are mutable after construction") {
        val colony = Colony(x = 10, y = 20)

        colony.x = 30
        colony.y = 40
        colony.owner = Owner.Player()
        colony.improvementType = ClnyImprovementType.OUTPOST

        colony.x shouldBe 30
        colony.y shouldBe 40
        colony.owner shouldBe Owner.Player()
        colony.improvementType shouldBe ClnyImprovementType.OUTPOST
    }
})
