package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import com.kelvsyc.rifflet.civ3.PrtoDomain
import com.kelvsyc.rifflet.civ3.PrtoUnitStatistics
import okio.ByteString

private fun unitEntry(
    x: Int,
    y: Int,
    ownerType: Int = 2,
    owner: Int = 0,
    unitType: Int = 0,
    aiStrategy: Int = 0,
): UnitEntry = UnitEntry(
    legacyName = "",
    ownerType = ownerType,
    experienceLevel = 0,
    owner = owner,
    unitType = unitType,
    aiStrategy = aiStrategy,
    x = x,
    y = y,
    ptwName = "",
    useCivilizationKing = 0,
)

private fun fileWithUnits(entries: List<UnitEntry>): Civ3File =
    Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), listOf(UnitSection(entries)))

private fun prtoEntry(aiStrategies: Int = 0): PrtoEntry = PrtoEntry(
    unitStatistics = PrtoUnitStatistics(
        zoneOfControl = 0, bombardStrength = 0, bombardRange = 0, capacity = 0, shieldCost = 0,
        defense = 0, attack = 0, operationalRange = 0, populationCost = 0, rateOfFire = 0,
        movement = 0, upgradeTo = -1, hpBonus = 0, bombardEffects = 0, requireSupport = 0,
        createCraters = 0, workerStrength = 0f, airDefense = 0,
    ),
    name = "",
    civilopediaEntry = "",
    iconIndex = 0,
    required = -1,
    requiredResource1 = -1,
    requiredResource2 = -1,
    requiredResource3 = -1,
    abilities = 0,
    aiStrategies = aiStrategies,
    availableTo = 0,
    flags2 = ByteString.of(*ByteArray(8)),
    type = PrtoDomain.LAND,
    otherStrategy = -1,
    standardOrders = 0,
    specialActions = 0,
    workerActions = 0,
    airMissions = 0,
    flags4 = ByteString.of(0, 0, 0, 0),
    ignoreMovementCost = ByteString.of(),
    unknown = ByteString.of(*ByteArray(16)),
    enslaveResults = -1,
    unknown2 = ByteString.of(*ByteArray(4)),
    stealthTargetUnitTypes = emptyList(),
    unknown3 = ByteString.of(*ByteArray(8)),
    unknown4 = ByteString.of(*ByteArray(4)),
)

private fun fileWithUnitsAndPrtos(units: List<UnitEntry>, prtos: List<PrtoEntry>): Civ3File = Civ3File(
    Civ3Header(major = 12, minor = 0, description = "", title = ""),
    listOf(UnitSection(units), PrtoSection(prtos)),
)

class UnitEntryValidationTest : FunSpec({

    test("returns no issues when x + y is even") {
        validateUnitCoordinateParity(fileWithUnits(listOf(unitEntry(x = 52, y = 20)))) shouldBe emptyList()
    }

    test("flags a UnitEntry whose x + y is odd") {
        val file = fileWithUnits(listOf(unitEntry(x = 52, y = 19)))

        validateUnitCoordinateParity(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.UNIT,
                0,
                "x/y",
                "x=52, y=19 sum to an odd value; Civ3's isometric tile grid expects x and y to share parity",
            ),
        )
    }

    test("returns no issues when UNIT is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateUnitCoordinateParity(file) shouldBe emptyList()
    }

    test("validateUnitOwnerNotNone returns no issues for ownerType 1, 2, or 3") {
        val file = fileWithUnits(
            listOf(
                unitEntry(x = 0, y = 0, ownerType = 1),
                unitEntry(x = 0, y = 0, ownerType = 2),
                unitEntry(x = 0, y = 0, ownerType = 3),
            ),
        )

        validateUnitOwnerNotNone(file) shouldBe emptyList()
    }

    test("validateUnitOwnerNotNone flags ownerType=0") {
        val file = fileWithUnits(listOf(unitEntry(x = 0, y = 0, ownerType = 0)))

        val issues = validateUnitOwnerNotNone(file)
        issues.size shouldBe 1
        issues.single().severity shouldBe ValidationSeverity.ERROR
        issues.single().field shouldBe "ownerType"
    }

    test("validateUnitOwnerNotNone returns no issues when UNIT is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateUnitOwnerNotNone(file) shouldBe emptyList()
    }

    test("validateUnitOwnerNotBarbarianPlaceholderCiv returns no issues for a non-zero Civilization owner") {
        val file = fileWithUnits(listOf(unitEntry(x = 0, y = 0, ownerType = 2, owner = 1)))

        validateUnitOwnerNotBarbarianPlaceholderCiv(file) shouldBe emptyList()
    }

    test("validateUnitOwnerNotBarbarianPlaceholderCiv flags ownerType=2, owner=0") {
        val file = fileWithUnits(listOf(unitEntry(x = 0, y = 0, ownerType = 2, owner = 0)))

        val issues = validateUnitOwnerNotBarbarianPlaceholderCiv(file)
        issues.size shouldBe 1
        issues.single().severity shouldBe ValidationSeverity.ERROR
        issues.single().field shouldBe "owner"
    }

    test("validateUnitOwnerNotBarbarianPlaceholderCiv returns no issues when UNIT is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateUnitOwnerNotBarbarianPlaceholderCiv(file) shouldBe emptyList()
    }

    test("validateUnitAiStrategyMatchesPrtoStrategy returns no issues when aiStrategy is -1") {
        val proto = prtoEntry(aiStrategies = 1 shl 0)
        val file = fileWithUnitsAndPrtos(listOf(unitEntry(x = 0, y = 0, unitType = 0, aiStrategy = -1)), listOf(proto))

        validateUnitAiStrategyMatchesPrtoStrategy(file) shouldBe emptyList()
    }

    test("validateUnitAiStrategyMatchesPrtoStrategy returns no issues when the bit is set") {
        val proto = prtoEntry(aiStrategies = 1 shl 3)
        val file = fileWithUnitsAndPrtos(listOf(unitEntry(x = 0, y = 0, unitType = 0, aiStrategy = 3)), listOf(proto))

        validateUnitAiStrategyMatchesPrtoStrategy(file) shouldBe emptyList()
    }

    test("validateUnitAiStrategyMatchesPrtoStrategy flags a bit not set in the prototype") {
        val proto = prtoEntry(aiStrategies = 1 shl 0)
        val file = fileWithUnitsAndPrtos(listOf(unitEntry(x = 0, y = 0, unitType = 0, aiStrategy = 3)), listOf(proto))

        val issues = validateUnitAiStrategyMatchesPrtoStrategy(file)
        issues.size shouldBe 1
        issues.single().severity shouldBe ValidationSeverity.ERROR
        issues.single().field shouldBe "aiStrategy"
    }

    test("validateUnitAiStrategyMatchesPrtoStrategy returns no issues for a dangling unitType") {
        val file = fileWithUnitsAndPrtos(listOf(unitEntry(x = 0, y = 0, unitType = 5, aiStrategy = 3)), emptyList())

        validateUnitAiStrategyMatchesPrtoStrategy(file) shouldBe emptyList()
    }

    test("validateUnitAiStrategyMatchesPrtoStrategy returns no issues when UNIT is absent") {
        val file = Civ3File(
            Civ3Header(major = 12, minor = 0, description = "", title = ""),
            sections = listOf(PrtoSection(listOf(prtoEntry()))),
        )

        validateUnitAiStrategyMatchesPrtoStrategy(file) shouldBe emptyList()
    }

    test("validateUnitAiStrategyMatchesPrtoStrategy returns no issues when PRTO is absent") {
        val file = fileWithUnits(listOf(unitEntry(x = 0, y = 0, aiStrategy = 3)))

        validateUnitAiStrategyMatchesPrtoStrategy(file) shouldBe emptyList()
    }
})
