package com.kelvsyc.rifflet.civ3.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validTech(): Tech = Tech(
    name = "Bronze Working",
    civilopediaEntry = "",
    cost = 0,
    era = 0,
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

    test("flavor1 (bit 0) and flavor7 (bit 6, highest flavors bit) are independently settable") {
        val tech = validTech()

        tech.flavor1 = true
        tech.flavor7 = true
        tech.flavor1 = false

        tech.flavor1 shouldBe false
        tech.flavor7 shouldBe true
        tech.flavors shouldBe (1 shl 6)
    }
})
