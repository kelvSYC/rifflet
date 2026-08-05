package com.kelvsyc.rifflet.civ3.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CityTest : FunSpec({

    test("constructing with only required params gives sensible defaults") {
        val city = City(name = "Rome", x = 10, y = 20)

        city.owner shouldBe Owner.None
        city.buildings shouldBe mutableListOf()
        city.culture shouldBe 0
        city.size shouldBe 0
        city.cityLevel shouldBe 0
        city.borderLevel shouldBe 0
        city.hasWalls shouldBe false
        city.hasPalace shouldBe false
        city.useAutoName shouldBe false
    }

    test("fields are mutable after construction, including a duplicate-preserving buildings list") {
        val city = City(name = "Rome", x = 10, y = 20)
        val granary = Improvement(
            description = "", name = "Granary", civilopediaEntry = "", cost = 0, culture = 0,
            maintenanceCost = 0, pollution = 0, production = 0,
        )
        city.owner = Owner.Barbarian
        city.buildings = mutableListOf(granary, granary, null)

        city.owner shouldBe Owner.Barbarian
        city.buildings shouldBe mutableListOf(granary, granary, null)
    }
})
