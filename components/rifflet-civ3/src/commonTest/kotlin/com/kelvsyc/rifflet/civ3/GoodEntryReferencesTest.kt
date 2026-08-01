package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validGoodEntry(prerequisite: Int = 0, type: Int = 0): GoodEntry = GoodEntry(
    name = "",
    civilopediaEntry = "",
    type = type,
    appearanceRatio = 0,
    disappearanceProbability = 0,
    icon = 0,
    prerequisite = prerequisite,
    foodBonus = 0,
    shieldsBonus = 0,
    commerceBonus = 0,
)

private fun validTechEntry(): TechEntry = TechEntry(
    name = "",
    civilopediaEntry = "",
    cost = 0,
    era = 0,
    advanceIcon = 0,
    x = 0,
    y = 0,
    prerequisite1 = 0,
    prerequisite2 = 0,
    prerequisite3 = 0,
    prerequisite4 = 0,
    flags = 0,
    flavors = 0,
    unknown = ByteString.of(0, 0, 0, 0),
)

class GoodEntryReferencesTest : FunSpec({

    test("prerequisiteTech resolves against the TECH list") {
        val tech = validTechEntry()
        validGoodEntry(prerequisite = 0).prerequisiteTech(listOf(tech)) shouldBe tech
        validGoodEntry(prerequisite = 5).prerequisiteTech(emptyList()) shouldBe null
    }

    test("typeEnum decodes 0/1/2 into BONUS/LUXURY/STRATEGIC") {
        validGoodEntry(type = 0).typeEnum shouldBe GoodResourceType.BONUS
        validGoodEntry(type = 1).typeEnum shouldBe GoodResourceType.LUXURY
        validGoodEntry(type = 2).typeEnum shouldBe GoodResourceType.STRATEGIC
    }

    test("typeEnum returns null for a type value outside the documented 0..2 range") {
        validGoodEntry(type = 3).typeEnum shouldBe null
        validGoodEntry(type = -1).typeEnum shouldBe null
    }
})
