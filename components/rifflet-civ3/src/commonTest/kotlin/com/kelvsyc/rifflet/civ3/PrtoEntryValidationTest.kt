package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun prtoEntry(type: Int): PrtoEntry = PrtoEntry(
    zoneOfControl = 0,
    name = "",
    civilopediaEntry = "",
    bombardStrength = 0,
    bombardRange = 0,
    capacity = 0,
    shieldCost = 0,
    defense = 0,
    iconIndex = 0,
    attack = 0,
    operationalRange = 0,
    populationCost = 0,
    rateOfFire = 0,
    movement = 0,
    required = -1,
    upgradeTo = -1,
    requiredResource1 = -1,
    requiredResource2 = -1,
    requiredResource3 = -1,
    abilities = 0,
    aiStrategies = 0,
    availableTo = 0,
    flags2 = ByteString.of(*ByteArray(8)),
    type = type,
    otherStrategy = 0,
    hpBonus = 0,
    standardOrders = 0,
    specialActions = 0,
    workerActions = 0,
    airMissions = 0,
    flags4 = ByteString.of(*ByteArray(4)),
    bombardEffects = 0,
    ignoreMovementCost = ByteString.of(),
    requireSupport = 0,
    unknown = ByteString.of(*ByteArray(16)),
    enslaveResults = -1,
    unknown2 = ByteString.of(*ByteArray(4)),
    stealthTargetUnitTypes = emptyList(),
    unknown3 = ByteString.of(*ByteArray(8)),
    createCraters = 0,
    workerStrength = 0f,
    unknown4 = ByteString.of(*ByteArray(4)),
    airDefense = 0,
)

private fun fileWithPrtos(entries: List<PrtoEntry>): Civ3File = Civ3File(
    Civ3Header(major = 12, minor = 0, description = "", title = ""),
    listOf(PrtoSection(entries)),
)

class PrtoEntryValidationTest : FunSpec({

    test("returns no issues for every documented type value (0-2)") {
        val file = fileWithPrtos((0..2).map { prtoEntry(type = it) })

        validatePrtoDomain(file) shouldBe emptyList()
    }

    test("flags a type value outside the documented 0-2 range") {
        val file = fileWithPrtos(listOf(prtoEntry(type = 3)))

        validatePrtoDomain(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "type",
                "type=3 is not a valid PrtoDomain index (0..2)",
            ),
        )
    }

    test("returns no issues when PRTO is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validatePrtoDomain(file) shouldBe emptyList()
    }
})
