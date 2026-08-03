package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.GovtCorruption
import com.kelvsyc.rifflet.civ3.GovtHurrying
import com.kelvsyc.rifflet.civ3.GovtRelationship
import com.kelvsyc.rifflet.civ3.GovtRulerTitles
import com.kelvsyc.rifflet.civ3.GovtUnitSupportCosts
import com.kelvsyc.rifflet.civ3.GovtWarWeariness
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validGovernment(): Government = Government(
    name = "Despotism",
    civilopediaEntry = "",
    rulerTitles = GovtRulerTitles(
        male1 = "", female1 = "",
        male2 = "", female2 = "",
        male3 = "", female3 = "",
        male4 = "", female4 = "",
    ),
    corruption = GovtCorruption.RAMPANT,
    hurrying = GovtHurrying.FORCED_LABOR,
    unitSupportCosts = GovtUnitSupportCosts(
        freeUnits = 0,
        freeUnitsPerTown = 0,
        freeUnitsPerCity = 0,
        freeUnitsPerMetropolis = 0,
        unitCost = 0,
    ),
    warWeariness = GovtWarWeariness.NONE,
)

class GovernmentTest : FunSpec({

    test("constructing with only required params gives sensible defaults") {
        val government = validGovernment()

        government.defaultType shouldBe false
        government.transitionType shouldBe false
        government.requiresMaintenance shouldBe false
        government.toggle1 shouldBe 0
        government.toggle2 shouldBe 0
        government.toggle3 shouldBe 0
        government.tilePenalty shouldBe 0
        government.tradeBonus shouldBe 0
        government.assimilationChance shouldBe 0
        government.draftLimit shouldBe 0
        government.militaryPoliceLimit shouldBe 0
        government.rulerTitlePairsUsed shouldBe 0
        government.scienceRateCap shouldBe 0
        government.workerRate shouldBe 0
        government.unknown shouldBe ByteString.of(0, 0, 0, 0)
        government.xenophobic shouldBe false
        government.forceResettle shouldBe false
        government.prerequisiteTechnology shouldBe null
        government.immuneTo shouldBe null
        government.diplomatsAre shouldBe null
        government.spiesAre shouldBe null
        government.relationships shouldBe emptyMap()
    }

    test("fields are mutable after construction") {
        val government = validGovernment()

        government.name = "Anarchy"
        government.toggle1 = 42

        government.name shouldBe "Anarchy"
        government.toggle1 shouldBe 42
    }

    test("relationships can hold a self-reference and a sibling cycle without stack-overflowing") {
        val gov1 = validGovernment()
        val gov2 = validGovernment()
        val relationship = GovtRelationship(canBribe = 0, propagandaModifier = 0, resistanceModifier = 0)

        gov1.relationships[gov1] = relationship
        gov1.relationships[gov2] = relationship
        gov2.relationships[gov1] = relationship

        // A data class's generated equals/hashCode/toString would recurse through this cycle and
        // stack-overflow; a plain class's reference-identity equals/hashCode/toString does not.
        gov1.toString()
        (gov1 == gov1) shouldBe true
        (gov1 == gov2) shouldBe false
    }
})
