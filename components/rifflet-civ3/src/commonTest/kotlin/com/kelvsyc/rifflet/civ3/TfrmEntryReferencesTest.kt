package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validTfrmEntry(
    required: Int = 0,
    requiredResource1: Int = 0,
    requiredResource2: Int = 0,
): TfrmEntry = TfrmEntry(
    name = "",
    civilopediaEntry = "",
    turnsToComplete = 0,
    required = required,
    requiredResource1 = requiredResource1,
    requiredResource2 = requiredResource2,
    order = "",
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

private fun validGoodEntry(): GoodEntry = GoodEntry(
    name = "",
    civilopediaEntry = "",
    type = 0,
    appearanceRatio = 0,
    disappearanceProbability = 0,
    icon = 0,
    prerequisite = 0,
    foodBonus = 0,
    shieldsBonus = 0,
    commerceBonus = 0,
)

class TfrmEntryReferencesTest : FunSpec({

    test("requiredTech resolves against the TECH list") {
        val tech = validTechEntry()
        validTfrmEntry(required = 0).requiredTech(listOf(tech)) shouldBe tech
        validTfrmEntry(required = 5).requiredTech(emptyList()) shouldBe null
    }

    test("requiredResource1Good resolves against the GOOD list") {
        val good = validGoodEntry()
        validTfrmEntry(requiredResource1 = 0).requiredResource1Good(listOf(good)) shouldBe good
        validTfrmEntry(requiredResource1 = 5).requiredResource1Good(emptyList()) shouldBe null
    }

    test("requiredResource2Good resolves against the GOOD list") {
        val good = validGoodEntry()
        validTfrmEntry(requiredResource2 = 0).requiredResource2Good(listOf(good)) shouldBe good
        validTfrmEntry(requiredResource2 = 5).requiredResource2Good(emptyList()) shouldBe null
    }
})
