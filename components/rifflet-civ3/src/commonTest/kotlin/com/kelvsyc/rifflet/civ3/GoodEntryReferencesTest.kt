package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validGoodEntry(prerequisite: Int = 0, type: GoodResourceType = GoodResourceType.BONUS): GoodEntry = GoodEntry(
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
})
