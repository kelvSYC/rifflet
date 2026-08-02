package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validGovtEntry(
    prerequisiteTechnology: Int = 0,
    immuneTo: Int = 0,
    diplomatsAre: Int = 0,
    spiesAre: Int = 0,
): GovtEntry = GovtEntry(
    defaultType = 0,
    transitionType = 0,
    requiresMaintenance = 0,
    toggle1 = 0,
    tilePenalty = 0,
    tradeBonus = 0,
    name = "",
    civilopediaEntry = "",
    rulerTitles = GovtRulerTitles(
        male1 = "", female1 = "",
        male2 = "", female2 = "",
        male3 = "", female3 = "",
        male4 = "", female4 = "",
    ),
    corruption = GovtCorruption.MINIMAL,
    immuneTo = immuneTo,
    diplomatsAre = diplomatsAre,
    spiesAre = spiesAre,
    relationships = emptyList(),
    hurrying = GovtHurrying.CANNOT_HURRY,
    assimilationChance = 0,
    draftLimit = 0,
    militaryPoliceLimit = 0,
    rulerTitlePairsUsed = 0,
    prerequisiteTechnology = prerequisiteTechnology,
    scienceRateCap = 0,
    workerRate = 0,
    toggle2 = 0,
    toggle3 = 0,
    unknown = ByteString.of(0, 0, 0, 0),
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

private fun validExprEntry(): ExprEntry = ExprEntry(name = "", baseHitPoints = 0, retreatBonus = 0)

private fun validEspnEntry(): EspnEntry = EspnEntry(
    description = "",
    name = "",
    civilopediaEntry = "",
    missionFlags = 0,
    baseCost = 0,
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

class GovtEntryReferencesTest : FunSpec({

    test("prerequisiteTechnologyTech resolves against the TECH list") {
        val tech = validTechEntry()
        validGovtEntry(prerequisiteTechnology = 0).prerequisiteTechnologyTech(listOf(tech)) shouldBe tech
        validGovtEntry(prerequisiteTechnology = 5).prerequisiteTechnologyTech(emptyList()) shouldBe null
    }

    test("diplomatsAreExpr resolves against the EXPR list") {
        val expr = validExprEntry()
        validGovtEntry(diplomatsAre = 0).diplomatsAreExpr(listOf(expr)) shouldBe expr
        validGovtEntry(diplomatsAre = 5).diplomatsAreExpr(emptyList()) shouldBe null
    }

    test("spiesAreExpr resolves against the EXPR list") {
        val expr = validExprEntry()
        validGovtEntry(spiesAre = 0).spiesAreExpr(listOf(expr)) shouldBe expr
        validGovtEntry(spiesAre = 5).spiesAreExpr(emptyList()) shouldBe null
    }

    test("immuneToEspn resolves against the ESPN list") {
        val espn = validEspnEntry()
        validGovtEntry(immuneTo = 0).immuneToEspn(listOf(espn)) shouldBe espn
        validGovtEntry(immuneTo = -1).immuneToEspn(listOf(espn)) shouldBe null
    }
})
