package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.GoodEntry
import com.kelvsyc.rifflet.civ3.GoodResourceType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BldgRequiredResourcesTest : FunSpec({

    test("constructing with no args gives all-null defaults") {
        val resources = BldgRequiredResources()

        resources.requiredResource1 shouldBe null
        resources.requiredResource2 shouldBe null
    }

    test("fields are mutable after construction") {
        val resources = BldgRequiredResources()
        val good = GoodEntry(
            name = "Wine", civilopediaEntry = "", type = GoodResourceType.LUXURY,
            appearanceRatio = 0, disappearanceProbability = 0, icon = 0, prerequisite = 0,
            foodBonus = 0, shieldsBonus = 0, commerceBonus = 0,
        )

        resources.requiredResource1 = good

        resources.requiredResource1 shouldBe good
    }
})
