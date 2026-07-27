package com.kelvsyc.rifflet.civ3

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validGovtEntry(unknown: ByteString = ByteString.of(0, 0, 0, 0)) = GovtEntry(
    defaultType = 0,
    transitionType = 0,
    requiresMaintenance = 1,
    toggle1 = 0,
    tilePenalty = 0,
    tradeBonus = 0,
    name = "Despotism",
    civilopediaEntry = "",
    rulerTitles = GovtRulerTitles(
        male1 = "", female1 = "",
        male2 = "", female2 = "",
        male3 = "", female3 = "",
        male4 = "", female4 = "",
    ),
    corruption = 0,
    immuneTo = 0,
    diplomatsAre = 0,
    spiesAre = 0,
    relationships = emptyList(),
    hurrying = 0,
    assimilationChance = 0,
    draftLimit = 0,
    militaryPoliceLimit = 0,
    rulerTitlePairsUsed = 0,
    prerequisiteTechnology = 0,
    scienceRateCap = 0,
    workerRate = 0,
    toggle2 = -1,
    toggle3 = 0,
    unknown = unknown,
    unitSupportCosts = GovtUnitSupportCosts(
        freeUnits = 0,
        freeUnitsPerTown = 0,
        freeUnitsPerCity = 0,
        freeUnitsPerMetropolis = 0,
        unitCost = 0,
    ),
    warWeariness = 0,
    xenophobic = 0,
    forceResettle = 0,
)

class GovtEntryTest : FunSpec({

    test("a 4-byte unknown field is accepted") {
        validGovtEntry().unknown.size shouldBe 4
    }

    test("an unknown field of any other size throws IllegalArgumentException") {
        shouldThrow<IllegalArgumentException> { validGovtEntry(unknown = ByteString.of(1, 2)) }
    }
})
