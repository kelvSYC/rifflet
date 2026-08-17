package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.kotlin.core.collections.mutableEnumSetOf
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import com.kelvsyc.rifflet.civ3.FlavorSlot

private fun validTech(): Tech = Tech(
    name = "Bronze Working",
    civilopediaEntry = "",
    cost = 0,
    advanceIcon = 0,
    x = 0,
    y = 0,
)

class TechFlagsTest : FunSpec({

    test("enablesDiplomats (bit 0) and revealsMap (bit 22, highest flags bit) are independently settable") {
        val tech = validTech()

        tech.enablesDiplomats = true
        tech.revealsMap = true
        tech.enablesDiplomats = false

        tech.enablesDiplomats shouldBe false
        tech.revealsMap shouldBe true
        tech.flags shouldBe (1 shl 22)
    }

    test("flavorSlots reads and writes the full FLAV membership set") {
        val tech = validTech()

        tech.flavorSlots shouldBe mutableEnumSetOf<FlavorSlot>()

        tech.flavorSlots = mutableEnumSetOf(FlavorSlot.FLAVOR_1, FlavorSlot.FLAVOR_7)

        tech.flavorSlots shouldBe mutableEnumSetOf(FlavorSlot.FLAVOR_1, FlavorSlot.FLAVOR_7)
        tech.flavors shouldBe (1 or (1 shl 6))
    }

    test("flavorSlots setter replaces the whole set, not just adds") {
        val tech = validTech()
        tech.flavorSlots = mutableEnumSetOf(FlavorSlot.FLAVOR_1, FlavorSlot.FLAVOR_2)

        tech.flavorSlots = mutableEnumSetOf(FlavorSlot.FLAVOR_7)

        tech.flavorSlots shouldBe mutableEnumSetOf(FlavorSlot.FLAVOR_7)
        tech.flavors shouldBe (1 shl 6)
    }
})
