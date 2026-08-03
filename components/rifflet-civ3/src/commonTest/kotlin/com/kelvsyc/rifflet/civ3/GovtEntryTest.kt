package com.kelvsyc.rifflet.civ3

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validGovtEntry(
    toggle1: ByteString = ByteString.of(*ByteArray(4)),
    unknown: ByteString = ByteString.of(*ByteArray(12)),
) = GovtEntry(
    defaultType = 0,
    transitionType = 0,
    requiresMaintenance = 1,
    toggle1 = toggle1,
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
    corruption = GovtCorruption.MINIMAL,
    immuneTo = 0,
    diplomatsAre = 0,
    spiesAre = 0,
    relationships = emptyList(),
    hurrying = GovtHurrying.CANNOT_HURRY,
    assimilationChance = 0,
    draftLimit = 0,
    militaryPoliceLimit = 0,
    rulerTitlePairsUsed = 0,
    prerequisiteTechnology = 0,
    scienceRateCap = 0,
    workerRate = 0,
    unknown = unknown,
    unitSupportCosts = GovtUnitSupportCosts(
        freeUnits = 0,
        freeUnitsPerTown = 0,
        freeUnitsPerCity = 0,
        freeUnitsPerMetropolis = 0,
        unitCost = 0,
    ),
    warWeariness = GovtWarWeariness.NONE,
    xenophobic = 0,
    forceResettle = 0,
)

class GovtEntryTest : FunSpec({

    test("a 4-byte toggle1 field is accepted") {
        validGovtEntry().toggle1.size shouldBe 4
    }

    test("a toggle1 field of any other size throws IllegalArgumentException") {
        shouldThrow<IllegalArgumentException> { validGovtEntry(toggle1 = ByteString.of(1, 2)) }
    }

    test("a 12-byte unknown field is accepted") {
        validGovtEntry().unknown.size shouldBe 12
    }

    test("an unknown field of any other size throws IllegalArgumentException") {
        shouldThrow<IllegalArgumentException> { validGovtEntry(unknown = ByteString.of(1, 2)) }
    }
})
