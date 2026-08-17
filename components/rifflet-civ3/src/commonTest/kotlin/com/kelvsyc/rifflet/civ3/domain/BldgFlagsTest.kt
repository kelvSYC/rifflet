package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.kotlin.core.collections.mutableEnumSetOf
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import com.kelvsyc.rifflet.civ3.FlavorSlot

private fun validImprovement(): Improvement = Improvement(
    description = "", name = "Granary", civilopediaEntry = "",
    cost = 0, culture = 0, maintenanceCost = 0, pollution = 0, production = 0,
)

private fun validSmallWonder(): SmallWonder = SmallWonder(
    description = "", name = "Pyramids", civilopediaEntry = "",
    cost = 0, culture = 0, maintenanceCost = 0, pollution = 0, production = 0,
)

class BldgFlagsTest : FunSpec({

    test("centerOfEmpire (bit 0) and requiredGoodsMustBeInCityRadius (bit 31, highest improvements bit) are independently settable") {
        val building: Building = validImprovement()

        building.centerOfEmpire = true
        building.requiredGoodsMustBeInCityRadius = true
        building.centerOfEmpire = false

        building.centerOfEmpire shouldBe false
        building.requiredGoodsMustBeInCityRadius shouldBe true
        building.improvements shouldBe (1 shl 31)
    }

    test("coastalInstallation (bit 0) and seafaring (bit 11, highest otherCharacteristics bit) are independently settable") {
        val building: Building = validImprovement()

        building.coastalInstallation = true
        building.seafaring = true
        building.coastalInstallation = false

        building.coastalInstallation shouldBe false
        building.seafaring shouldBe true
        building.otherCharacteristics shouldBe (1 shl 11)
    }

    test("increasesChanceOfLeaderAppearance (bit 0) and requiresEliteNavalUnits (bit 11, highest smallWonders bit) are independently settable, declared once against Wonder") {
        val wonder: Wonder = validSmallWonder()

        wonder.increasesChanceOfLeaderAppearance = true
        wonder.requiresEliteNavalUnits = true
        wonder.increasesChanceOfLeaderAppearance = false

        wonder.increasesChanceOfLeaderAppearance shouldBe false
        wonder.requiresEliteNavalUnits shouldBe true
        wonder.smallWonders shouldBe (1 shl 11)
    }

    test("safeSeaTravel (bit 0) and touristAttraction (bit 17, highest wonders bit) are independently settable, declared once against Wonder") {
        val wonder: Wonder = validSmallWonder()

        wonder.safeSeaTravel = true
        wonder.touristAttraction = true
        wonder.safeSeaTravel = false

        wonder.safeSeaTravel shouldBe false
        wonder.touristAttraction shouldBe true
        wonder.wonders shouldBe (1 shl 17)
    }

    test("flavorSlots reads and writes the full FLAV membership set, backed by flavors") {
        val building: Building = validImprovement()

        building.flavorSlots shouldBe mutableEnumSetOf<FlavorSlot>()

        building.flavorSlots = mutableEnumSetOf(FlavorSlot.FLAVOR_1, FlavorSlot.FLAVOR_7)

        building.flavorSlots shouldBe mutableEnumSetOf(FlavorSlot.FLAVOR_1, FlavorSlot.FLAVOR_7)
        building.flavors shouldBe (1 or (1 shl 6))
    }
})
