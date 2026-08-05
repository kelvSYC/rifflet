package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.PrtoDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PrtoUnitStatisticsTest : FunSpec({

    test("constructing with no args gives all-zero defaults") {
        val stats = PrtoUnitStatistics()

        stats.zoneOfControl shouldBe 0
        stats.attack shouldBe 0
        stats.upgradeTo shouldBe null
        stats.bombardEffects shouldBe 0
        stats.createCraters shouldBe 0.toByte()
        stats.workerStrength shouldBe 0f
    }

    test("fields are mutable after construction, including a self-reference to a Prto") {
        val stats = PrtoUnitStatistics()
        val upgrade = Prto(name = "Musketeers", civilopediaEntry = "", iconIndex = 0, type = PrtoDomain.LAND)

        stats.upgradeTo = upgrade

        stats.upgradeTo shouldBe upgrade
    }
})
