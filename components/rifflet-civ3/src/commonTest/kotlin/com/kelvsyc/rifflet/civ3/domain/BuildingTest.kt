package com.kelvsyc.rifflet.civ3.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validImprovement(name: String = "Granary"): Improvement = Improvement(
    description = "", name = name, civilopediaEntry = "",
    cost = 0, culture = 0, maintenanceCost = 0, pollution = 0, production = 0,
)

private fun validSpaceshipPart(name: String = "SS Structural"): SpaceshipPart = SpaceshipPart(
    description = "", name = name, civilopediaEntry = "",
    cost = 0, culture = 0, maintenanceCost = 0, pollution = 0, production = 0, partIndex = 0,
)

private fun validSmallWonder(name: String = "Pyramids"): SmallWonder = SmallWonder(
    description = "", name = name, civilopediaEntry = "",
    cost = 0, culture = 0, maintenanceCost = 0, pollution = 0, production = 0,
)

private fun validGreatWonder(name: String = "Oracle"): GreatWonder = GreatWonder(
    description = "", name = name, civilopediaEntry = "",
    cost = 0, culture = 0, maintenanceCost = 0, pollution = 0, production = 0,
)

class BuildingTest : FunSpec({

    test("Improvement constructing with only required params gives sensible defaults") {
        val building = validImprovement()

        building.requirements shouldBe BldgRequirements()
        building.requiredResources shouldBe BldgRequiredResources()
        building.improvements shouldBe 0
        building.combatValues shouldBe com.kelvsyc.rifflet.civ3.BldgCombatValues(0, 0, 0, 0, 0)
        building.unitsProduced shouldBe null
    }

    test("SpaceshipPart requires partIndex and carries no wonder fields") {
        val part = validSpaceshipPart()

        part.partIndex shouldBe 0
        part.requirements shouldBe BldgRequirements()
    }

    test("SmallWonder carries the shared wonders/smallWonders grid, defaulted to 0") {
        val wonder = validSmallWonder()

        wonder.wonders shouldBe 0
        wonder.smallWonders shouldBe 0
    }

    test("GreatWonder carries the 3 effect fields, defaulted to null") {
        val wonder = validGreatWonder()

        wonder.doublesHappiness shouldBe null
        wonder.gainInEveryCity shouldBe null
        wonder.gainInEveryCityOnContinent shouldBe null
    }

    test("fields are mutable after construction, including polymorphically via the Building interface") {
        val building: Building = validImprovement()
        building.name = "Granary"

        building.name shouldBe "Granary"
    }

    test("GreatWonder's effect fields can hold a real Building reference, including another variant") {
        val granary = validImprovement("Granary")
        val wonder = validGreatWonder("Pyramids")
        wonder.gainInEveryCity = granary

        wonder.gainInEveryCity shouldBe granary
    }
})
