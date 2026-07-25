package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun wsizEntry(): WsizEntry = WsizEntry(
    optimalNumberOfCities = 0,
    techRate = 0,
    reserved = ByteString.of(*ByteArray(24)),
    name = "",
    height = 0,
    distanceBetweenCivs = 0,
    numberOfCivs = 0,
    width = 0,
)

private fun exprEntry(): ExprEntry = ExprEntry(name = "", baseHitPoints = 0, retreatBonus = 0)

private fun erasEntry(): ErasEntry = ErasEntry(
    name = "",
    civilopediaEntry = "",
    researcher1 = "",
    researcher2 = "",
    researcher3 = "",
    researcher4 = "",
    researcher5 = "",
    numberOfUsedResearcherNames = 0,
    unknown = ByteString.of(*ByteArray(4)),
)

private fun diffEntry(): DiffEntry = DiffEntry(
    name = "",
    numberOfCitizensBornContent = 0,
    maxGovernmentTransitionTime = 0,
    numberOfAiDefensiveStartingUnits = 0,
    numberOfAiOffensiveStartingUnits = 0,
    extraStartUnit1 = 0,
    extraStartUnit2 = 0,
    additionalFreeSupport = 0,
    unitSupportBonusForEachSettlement = 0,
    attackBonusAgainstBarbarians = 0,
    costFactor = 0,
    percentageOfOptimalCities = 0,
    aiToAiTradeRate = 0,
    corruptionPercentage = 0,
    militaryLaw = 0,
)

private fun fileWithSections(major: Int, sections: List<Civ3Section>): Civ3File =
    Civ3File(Civ3Header(major = major, minor = 0, description = "", title = ""), sections)

private fun terrEntryWithPollutionEffect(pollutionEffect: Int): TerrEntry = TerrEntry(
    numberOfPossibleResources = 0,
    possibleResources = ByteString.of(),
    name = "",
    civilopediaEntry = "",
    irrigationBonus = 0,
    miningBonus = 0,
    roadBonus = 0,
    defenseBonus = 0,
    movementCost = 0,
    food = 0,
    shields = 0,
    commerce = 0,
    workerJobAllowed = -1,
    pollutionEffect = pollutionEffect,
    allowCities = 0,
    allowColonies = 0,
    impassable = 0,
    impassableByWheeled = 0,
    allowAirfields = 0,
    allowForts = 0,
    allowOutposts = 0,
    allowRadarTowers = 0,
    unknown = ByteString.of(*ByteArray(4)),
    landmarkEnabled = 0,
    landmarkFood = 0,
    landmarkShields = 0,
    landmarkCommerce = 0,
    landmarkIrrigationBonus = 0,
    landmarkMiningBonus = 0,
    landmarkRoadBonus = 0,
    landmarkMovementBonus = 0,
    landmarkDefensiveBonus = 0,
    landmarkName = "",
    landmarkCivilopediaEntry = "",
    unknown2 = ByteString.of(*ByteArray(4)),
    terrainFlags = 0,
    diseaseStrength = 0,
)

class Civ3FileValidationTest : FunSpec({

    test("validate() surfaces the seed rule's issue for a file with an invalid pollutionEffect") {
        val file = Civ3File(
            Civ3Header(major = 12, minor = 0, description = "", title = ""),
            listOf(TerrSection(listOf(terrEntryWithPollutionEffect(pollutionEffect = 99)))),
        )

        file.validate() shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TERR,
                0,
                "pollutionEffect",
                "pollutionEffect=99 is not -1, not the base-terrain sentinel (1), and not a valid TERR index (0..<1)",
            ),
        )
    }

    test("validate() returns no issues for a file with no sections") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        file.validate() shouldBe emptyList()
    }

    test("validateWsizCardinality returns no issues for exactly 5 entries") {
        val file = fileWithSections(major = 12, listOf(WsizSection(List(5) { wsizEntry() })))

        validateWsizCardinality(file) shouldBe emptyList()
    }

    test("validateWsizCardinality flags a count other than 5") {
        val file = fileWithSections(major = 12, listOf(WsizSection(List(4) { wsizEntry() })))

        validateWsizCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.WSIZ,
                null,
                "entries",
                "WSIZ has 4 entries; the Rules Editor always produces exactly 5",
            ),
        )
    }

    test("validateWsizCardinality returns no issues when WSIZ is absent") {
        validateWsizCardinality(fileWithSections(major = 12, emptyList())) shouldBe emptyList()
    }

    test("validateExprCardinality returns no issues for exactly 4 entries") {
        val file = fileWithSections(major = 12, listOf(ExprSection(List(4) { exprEntry() })))

        validateExprCardinality(file) shouldBe emptyList()
    }

    test("validateExprCardinality flags a count other than 4") {
        val file = fileWithSections(major = 12, listOf(ExprSection(List(3) { exprEntry() })))

        validateExprCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.EXPR,
                null,
                "entries",
                "EXPR has 3 entries; the Rules Editor always produces exactly 4",
            ),
        )
    }

    test("validateExprCardinality returns no issues when EXPR is absent") {
        validateExprCardinality(fileWithSections(major = 12, emptyList())) shouldBe emptyList()
    }

    test("validateErasCardinality returns no issues for exactly 4 entries") {
        val file = fileWithSections(major = 12, listOf(ErasSection(List(4) { erasEntry() })))

        validateErasCardinality(file) shouldBe emptyList()
    }

    test("validateErasCardinality flags a count other than 4") {
        val file = fileWithSections(major = 12, listOf(ErasSection(List(5) { erasEntry() })))

        validateErasCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.ERAS,
                null,
                "entries",
                "ERAS has 5 entries; the Rules Editor always produces exactly 4",
            ),
        )
    }

    test("validateErasCardinality returns no issues when ERAS is absent") {
        validateErasCardinality(fileWithSections(major = 12, emptyList())) shouldBe emptyList()
    }

    test("validateDiffCardinality returns no issues for exactly 8 entries in Conquests") {
        val file = fileWithSections(major = 12, listOf(DiffSection(List(8) { diffEntry() })))

        validateDiffCardinality(file) shouldBe emptyList()
    }

    test("validateDiffCardinality returns no issues for more than 8 entries in Conquests") {
        val file = fileWithSections(major = 12, listOf(DiffSection(List(9) { diffEntry() })))

        validateDiffCardinality(file) shouldBe emptyList()
    }

    test("validateDiffCardinality flags fewer than 8 entries in Conquests") {
        val file = fileWithSections(major = 12, listOf(DiffSection(List(7) { diffEntry() })))

        validateDiffCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.DIFF,
                null,
                "entries",
                "DIFF has 7 entries; CONQUESTS requires at least 8",
            ),
        )
    }

    test("validateDiffCardinality returns no issues for exactly 6 entries in PTW") {
        val file = fileWithSections(major = 11, listOf(DiffSection(List(6) { diffEntry() })))

        validateDiffCardinality(file) shouldBe emptyList()
    }

    test("validateDiffCardinality flags a count other than 6 in PTW") {
        val file = fileWithSections(major = 11, listOf(DiffSection(List(8) { diffEntry() })))

        validateDiffCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.DIFF,
                null,
                "entries",
                "DIFF has 8 entries; PTW requires exactly 6",
            ),
        )
    }

    test("validateDiffCardinality flags a count other than 6 in vanilla") {
        val file = fileWithSections(major = 3, listOf(DiffSection(List(5) { diffEntry() })))

        validateDiffCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.DIFF,
                null,
                "entries",
                "DIFF has 5 entries; VANILLA requires exactly 6",
            ),
        )
    }

    test("validateDiffCardinality returns no issues when DIFF is absent") {
        validateDiffCardinality(fileWithSections(major = 12, emptyList())) shouldBe emptyList()
    }

    test("validate() surfaces a cardinality rule's issue alongside the seed rule's") {
        val file = Civ3File(
            Civ3Header(major = 12, minor = 0, description = "", title = ""),
            listOf(WsizSection(List(4) { wsizEntry() })),
        )

        file.validate() shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.WSIZ,
                null,
                "entries",
                "WSIZ has 4 entries; the Rules Editor always produces exactly 5",
            ),
        )
    }
})
