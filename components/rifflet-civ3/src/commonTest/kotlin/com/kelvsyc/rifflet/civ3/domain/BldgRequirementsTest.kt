package com.kelvsyc.rifflet.civ3.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BldgRequirementsTest : FunSpec({

    test("constructing with no args gives all-null defaults") {
        val requirements = BldgRequirements()

        requirements.requiredBuilding shouldBe null
        requirements.requiredGovernment shouldBe null
        requirements.requiredAdvance shouldBe null
    }

    test("fields are mutable after construction") {
        val requirements = BldgRequirements()
        val government = Government(
            name = "Despotism",
            civilopediaEntry = "",
            rulerTitles = com.kelvsyc.rifflet.civ3.GovtRulerTitles(
                male1 = "", female1 = "", male2 = "", female2 = "", male3 = "", female3 = "", male4 = "", female4 = "",
            ),
            corruption = com.kelvsyc.rifflet.civ3.GovtCorruption.RAMPANT,
            hurrying = com.kelvsyc.rifflet.civ3.GovtHurrying.CANNOT_HURRY,
            unitSupportCosts = com.kelvsyc.rifflet.civ3.GovtUnitSupportCosts(
                freeUnits = 0, freeUnitsPerTown = 0, freeUnitsPerCity = 0, freeUnitsPerMetropolis = 0, unitCost = 0,
            ),
            warWeariness = com.kelvsyc.rifflet.civ3.GovtWarWeariness.NONE,
        )

        requirements.requiredGovernment = government

        requirements.requiredGovernment shouldBe government
    }
})
