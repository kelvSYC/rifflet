package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.PrtoDomain
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validPrto(name: String = "Warrior"): Prto = Prto(
    name = name, civilopediaEntry = "", iconIndex = 0, type = PrtoDomain.LAND,
)

class PrtoTest : FunSpec({

    test("constructing with only required params gives sensible defaults") {
        val prto = validPrto()

        prto.unitStatistics shouldBe PrtoUnitStatistics()
        prto.required shouldBe null
        prto.requiredResources shouldBe mutableListOf(null, null, null)
        prto.abilities shouldBe 0
        prto.aiStrategies shouldBe 0
        prto.availableTo shouldBe mutableSetOf()
        prto.enslaveResults shouldBe null
        prto.ignoreMovementCost shouldBe mutableSetOf()
        prto.stealthTargetUnitTypes shouldBe mutableSetOf()
        prto.skipTurn shouldBe false
        prto.explore shouldBe false
        prto.enslave shouldBe false
    }

    test("throws if requiredResources is not exactly 3 elements") {
        shouldThrow<IllegalArgumentException> {
            validPrto().copy(requiredResources = mutableListOf(null, null))
        }
    }

    test("fields are mutable after construction, including polymorphic-looking self-references") {
        val prto = validPrto()
        val upgrade = validPrto("Musketeers")
        prto.unitStatistics.upgradeTo = upgrade
        prto.enslaveResults = prto
        prto.stealthTargetUnitTypes = mutableSetOf(upgrade)

        prto.unitStatistics.upgradeTo shouldBe upgrade
        prto.enslaveResults shouldBe prto
        prto.stealthTargetUnitTypes shouldBe mutableSetOf(upgrade)
    }

    test("requiredResourcesOf builds a front-packed 3-element list") {
        val good = com.kelvsyc.rifflet.civ3.GoodEntry(
            name = "Wine", civilopediaEntry = "", type = com.kelvsyc.rifflet.civ3.GoodResourceType.LUXURY,
            appearanceRatio = 0, disappearanceProbability = 0, icon = 0, prerequisite = 0,
            foodBonus = 0, shieldsBonus = 0, commerceBonus = 0,
        )

        requiredResourcesOf(good) shouldBe mutableListOf(good, null, null)
    }
})
