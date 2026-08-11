package com.kelvsyc.rifflet.civ3.domain

import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.FunSpec
import okio.ByteString

private fun validTech(name: String = "Bronze Working"): Tech = Tech(
    name = name,
    civilopediaEntry = "",
    cost = 0,
    advanceIcon = 0,
    x = 0,
    y = 0,
)

class TechTest : FunSpec({

    test("constructing with only required params gives sensible defaults") {
        val tech = validTech()

        tech.prerequisite1 shouldBe null
        tech.prerequisite2 shouldBe null
        tech.prerequisite3 shouldBe null
        tech.prerequisite4 shouldBe null
        tech.era shouldBe null
        tech.flags shouldBe 0
        tech.flavors shouldBe 0
        tech.unknown shouldBe ByteString.of(0, 0, 0, 0)
    }

    test("fields are mutable after construction") {
        val tech = validTech()

        tech.name = "Iron Working"
        tech.cost = 20

        tech.name shouldBe "Iron Working"
        tech.cost shouldBe 20
    }

    test("prerequisite fields can hold a real Tech reference") {
        val bronzeWorking = validTech("Bronze Working")
        val ironWorking = validTech("Iron Working")
        ironWorking.prerequisite1 = bronzeWorking

        ironWorking.prerequisite1 shouldBe bronzeWorking
    }
})
