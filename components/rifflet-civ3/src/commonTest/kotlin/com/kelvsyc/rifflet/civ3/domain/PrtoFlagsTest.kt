package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.PrtoDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validPrto(): Prto = Prto(name = "Warrior", civilopediaEntry = "", iconIndex = 0, type = PrtoDomain.LAND)

class PrtoFlagsTest : FunSpec({

    test("wheeledAbility (bit 0) and requiresEscortAbility (bit 30, highest abilities bit) are independently settable") {
        val prto = validPrto()

        prto.wheeledAbility = true
        prto.requiresEscortAbility = true
        prto.wheeledAbility = false

        prto.wheeledAbility shouldBe false
        prto.requiresEscortAbility shouldBe true
        prto.abilities shouldBe (1 shl 30)
    }

    test("offenseStrategy (bit 0) and kingStrategy (bit 19, highest aiStrategies bit) are independently settable") {
        val prto = validPrto()

        prto.offenseStrategy = true
        prto.kingStrategy = true
        prto.offenseStrategy = false

        prto.offenseStrategy shouldBe false
        prto.kingStrategy shouldBe true
        prto.aiStrategies shouldBe (1 shl 19)
    }

    test("aiStrategies genuinely holds multiple simultaneous bits, unlike a single wire PrtoEntry") {
        val prto = validPrto()

        prto.offenseStrategy = true
        prto.defenseStrategy = true

        prto.aiStrategies shouldBe ((1 shl 0) or (1 shl 1))
    }
})
