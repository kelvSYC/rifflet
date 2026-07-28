package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validUnitEntry(
    ownerType: Int = 0,
    owner: Int = 0,
    experienceLevel: Int = 0,
    unitType: Int = 0,
): UnitEntry = UnitEntry(
    legacyName = "",
    ownerType = ownerType,
    experienceLevel = experienceLevel,
    owner = owner,
    unitType = unitType,
    aiStrategy = 0,
    x = 0,
    y = 0,
    ptwName = "",
    useCivilizationKing = 0,
)

private fun validExprEntry(): ExprEntry = ExprEntry(name = "", baseHitPoints = 0, retreatBonus = 0)

private fun validPrtoEntry(): PrtoEntry = PrtoEntry(
    unitStatistics = PrtoUnitStatistics(
        zoneOfControl = 0, bombardStrength = 0, bombardRange = 0, capacity = 0, shieldCost = 0,
        defense = 0, attack = 0, operationalRange = 0, populationCost = 0, rateOfFire = 0,
        movement = 0, upgradeTo = 0, hpBonus = 0, bombardEffects = 0, requireSupport = 0,
        createCraters = 0, workerStrength = 0f, airDefense = 0,
    ),
    name = "",
    civilopediaEntry = "",
    iconIndex = 0,
    required = 0,
    requiredResource1 = 0,
    requiredResource2 = 0,
    requiredResource3 = 0,
    abilities = 0,
    aiStrategies = 0,
    availableTo = 0,
    flags2 = ByteString.of(*ByteArray(8)),
    type = 0,
    otherStrategy = 0,
    standardOrders = 0,
    specialActions = 0,
    workerActions = 0,
    airMissions = 0,
    flags4 = ByteString.of(*ByteArray(4)),
    ignoreMovementCost = ByteString.of(),
    unknown = ByteString.of(*ByteArray(16)),
    enslaveResults = 0,
    unknown2 = ByteString.of(0, 0, 0, 0),
    stealthTargetUnitTypes = emptyList(),
    unknown3 = ByteString.of(*ByteArray(8)),
    unknown4 = ByteString.of(0, 0, 0, 0),
)

class UnitEntryReferencesTest : FunSpec({

    test("resolveOwner delegates to the shared Owner resolution") {
        validUnitEntry(ownerType = 1).resolveOwner(emptyList()) shouldBe Owner.Barbarian
    }

    test("experienceLevelExpr resolves a resolving index") {
        val expr = validExprEntry()
        validUnitEntry(experienceLevel = 0).experienceLevelExpr(listOf(expr)) shouldBe expr
    }

    test("experienceLevelExpr returns null for a non-resolving index") {
        validUnitEntry(experienceLevel = 5).experienceLevelExpr(emptyList()) shouldBe null
    }

    test("unitTypePrto resolves a resolving index") {
        val prto = validPrtoEntry()
        validUnitEntry(unitType = 0).unitTypePrto(listOf(prto)) shouldBe prto
    }

    test("unitTypePrto returns null for a non-resolving index") {
        validUnitEntry(unitType = 5).unitTypePrto(emptyList()) shouldBe null
    }
})
