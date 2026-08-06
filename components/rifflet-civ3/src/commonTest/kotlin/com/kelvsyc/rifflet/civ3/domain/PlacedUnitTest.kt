package com.kelvsyc.rifflet.civ3.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PlacedUnitTest : FunSpec({

    test("constructing with only required params gives sensible defaults") {
        val unit = PlacedUnit(x = 10, y = 20)

        unit.legacyName shouldBe ""
        unit.ptwName shouldBe ""
        unit.owner shouldBe Owner.None
        unit.unitType shouldBe null
        unit.experienceLevel shouldBe null
        unit.aiStrategy shouldBe null
        unit.useCivilizationKing shouldBe false
    }

    test("name prefers ptwName when non-blank") {
        val unit = PlacedUnit(x = 10, y = 20, legacyName = "OldName", ptwName = "NewName")

        unit.name shouldBe "NewName"
    }

    test("name falls back to legacyName when ptwName is blank") {
        val unit = PlacedUnit(x = 10, y = 20, legacyName = "OldName", ptwName = "")

        unit.name shouldBe "OldName"
    }

    test("fields are mutable after construction") {
        val unit = PlacedUnit(x = 10, y = 20)

        unit.x = 30
        unit.y = 40
        unit.owner = Owner.Barbarian()
        unit.aiStrategy = AiStrategy.SETTLE
        unit.useCivilizationKing = true

        unit.x shouldBe 30
        unit.y shouldBe 40
        unit.owner shouldBe Owner.Barbarian()
        unit.aiStrategy shouldBe AiStrategy.SETTLE
        unit.useCivilizationKing shouldBe true
    }
})
