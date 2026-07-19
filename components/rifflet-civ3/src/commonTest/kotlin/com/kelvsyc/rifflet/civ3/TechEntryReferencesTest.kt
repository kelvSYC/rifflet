package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validTechEntry(
    prerequisite1: Int = 0,
    prerequisite2: Int = 0,
    prerequisite3: Int = 0,
    prerequisite4: Int = 0,
): TechEntry = TechEntry(
    name = "",
    civilopediaEntry = "",
    cost = 0,
    era = 0,
    advanceIcon = 0,
    x = 0,
    y = 0,
    prerequisite1 = prerequisite1,
    prerequisite2 = prerequisite2,
    prerequisite3 = prerequisite3,
    prerequisite4 = prerequisite4,
    flags = 0,
    flavors = 0,
    unknown = ByteString.of(0, 0, 0, 0),
)

class TechEntryReferencesTest : FunSpec({

    test("prerequisite1Tech resolves a self-reference") {
        val tech = validTechEntry()
        validTechEntry(prerequisite1 = 0).prerequisite1Tech(listOf(tech)) shouldBe tech
        validTechEntry(prerequisite1 = 5).prerequisite1Tech(emptyList()) shouldBe null
    }

    test("prerequisite2Tech resolves a self-reference") {
        val tech = validTechEntry()
        validTechEntry(prerequisite2 = 0).prerequisite2Tech(listOf(tech)) shouldBe tech
        validTechEntry(prerequisite2 = 5).prerequisite2Tech(emptyList()) shouldBe null
    }

    test("prerequisite3Tech resolves a self-reference") {
        val tech = validTechEntry()
        validTechEntry(prerequisite3 = 0).prerequisite3Tech(listOf(tech)) shouldBe tech
        validTechEntry(prerequisite3 = 5).prerequisite3Tech(emptyList()) shouldBe null
    }

    test("prerequisite4Tech resolves a self-reference") {
        val tech = validTechEntry()
        validTechEntry(prerequisite4 = 0).prerequisite4Tech(listOf(tech)) shouldBe tech
        validTechEntry(prerequisite4 = 5).prerequisite4Tech(emptyList()) shouldBe null
    }
})
