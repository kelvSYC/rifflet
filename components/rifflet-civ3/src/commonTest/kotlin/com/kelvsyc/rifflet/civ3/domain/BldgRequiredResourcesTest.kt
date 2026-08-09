package com.kelvsyc.rifflet.civ3.domain

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
        val resource = Resource(name = "Wine", type = GoodResourceType.LUXURY)

        resources.requiredResource1 = resource

        resources.requiredResource1 shouldBe resource
    }
})
