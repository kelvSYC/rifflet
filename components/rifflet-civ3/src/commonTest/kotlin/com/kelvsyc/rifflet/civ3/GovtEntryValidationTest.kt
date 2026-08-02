package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun govtEntry(
    corruption: Int = 0,
    defaultType: Int = 0,
    transitionType: Int = 0,
    prerequisiteTechnology: Int = -1,
): GovtEntry = GovtEntry(
    defaultType = defaultType,
    transitionType = transitionType,
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
    corruption = GovtCorruption.entries[corruption],
    immuneTo = -1,
    diplomatsAre = 0,
    spiesAre = 0,
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
    unknown = ByteString.of(*ByteArray(4)),
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

private fun fileWithGovts(entries: List<GovtEntry>, major: Int = 12): Civ3File = Civ3File(
    Civ3Header(major = major, minor = 0, description = "", title = ""),
    listOf(GovtSection(entries)),
)

class GovtEntryValidationTest : FunSpec({

    test("returns no issues for every documented corruption value (0-6)") {
        val file = fileWithGovts((0..6).map { govtEntry(corruption = it) })

        validateGovtCorruption(file) shouldBe emptyList()
    }

test("returns no issues when GOVT is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateGovtCorruption(file) shouldBe emptyList()
    }

    test("flags corruption=OFF (6) in PTW") {
        val file = fileWithGovts(listOf(govtEntry(corruption = 6)), major = 11)

        validateGovtCorruption(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.GOVT,
                0,
                "corruption",
                "corruption=6 (OFF) is Conquests-only; PTW's Rules Editor never offers it",
            ),
        )
    }

    test("flags corruption=OFF (6) in vanilla") {
        val file = fileWithGovts(listOf(govtEntry(corruption = 6)), major = 3)

        validateGovtCorruption(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.GOVT,
                0,
                "corruption",
                "corruption=6 (OFF) is Conquests-only; VANILLA's Rules Editor never offers it",
            ),
        )
    }

    test("returns no issues for corruption=OFF (6) in Conquests") {
        val file = fileWithGovts(listOf(govtEntry(corruption = 6)), major = 12)

        validateGovtCorruption(file) shouldBe emptyList()
    }

    test("returns no issues when exactly one entry has defaultType set") {
        val file = fileWithGovts(listOf(govtEntry(defaultType = 1), govtEntry()))

        validateGovtDefaultCardinality(file) shouldBe emptyList()
    }

    test("warns when no entry has defaultType set") {
        val file = fileWithGovts(listOf(govtEntry(), govtEntry()))

        validateGovtDefaultCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.GOVT,
                null,
                "defaultType",
                "no entry has defaultType set; a Default government is usually expected",
            ),
        )
    }

    test("flags more than one entry with defaultType set") {
        val file = fileWithGovts(listOf(govtEntry(defaultType = 1), govtEntry(defaultType = 1)))

        validateGovtDefaultCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.GOVT,
                null,
                "defaultType",
                "2 entries have defaultType set; at most one is expected",
            ),
        )
    }

    test("returns no issues for defaultType cardinality when GOVT is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateGovtDefaultCardinality(file) shouldBe emptyList()
    }

    test("returns no issues when exactly one entry has transitionType set") {
        val file = fileWithGovts(listOf(govtEntry(transitionType = 1), govtEntry()))

        validateGovtTransitionCardinality(file) shouldBe emptyList()
    }

    test("flags no entry having transitionType set") {
        val file = fileWithGovts(listOf(govtEntry(), govtEntry()))

        validateGovtTransitionCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.GOVT,
                null,
                "transitionType",
                "0 entries have transitionType set; exactly one is expected",
            ),
        )
    }

    test("flags more than one entry with transitionType set") {
        val file = fileWithGovts(listOf(govtEntry(transitionType = 1), govtEntry(transitionType = 1)))

        validateGovtTransitionCardinality(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.GOVT,
                null,
                "transitionType",
                "2 entries have transitionType set; exactly one is expected",
            ),
        )
    }

    test("returns no issues for transitionType cardinality when GOVT is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateGovtTransitionCardinality(file) shouldBe emptyList()
    }

    test("returns no issues when the Default government has no prerequisite") {
        val file = fileWithGovts(listOf(govtEntry(defaultType = 1, prerequisiteTechnology = -1)))

        validateGovtDefaultHasNoPrerequisite(file) shouldBe emptyList()
    }

    test("flags the Default government carrying a prerequisite") {
        val file = fileWithGovts(listOf(govtEntry(defaultType = 1, prerequisiteTechnology = 5)))

        validateGovtDefaultHasNoPrerequisite(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.GOVT,
                0,
                "prerequisiteTechnology",
                "defaultType is set but prerequisiteTechnology=5; -1 is expected",
            ),
        )
    }

    test("ignores a non-default entry's prerequisite") {
        val file = fileWithGovts(listOf(govtEntry(prerequisiteTechnology = 5)))

        validateGovtDefaultHasNoPrerequisite(file) shouldBe emptyList()
    }

    test("returns no issues for Default prerequisite check when GOVT is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateGovtDefaultHasNoPrerequisite(file) shouldBe emptyList()
    }

    test("returns no issues when the Transition government has no prerequisite") {
        val file = fileWithGovts(listOf(govtEntry(transitionType = 1, prerequisiteTechnology = -1)))

        validateGovtTransitionHasNoPrerequisite(file) shouldBe emptyList()
    }

    test("flags the Transition government carrying a prerequisite") {
        val file = fileWithGovts(listOf(govtEntry(transitionType = 1, prerequisiteTechnology = 5)))

        validateGovtTransitionHasNoPrerequisite(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.GOVT,
                0,
                "prerequisiteTechnology",
                "transitionType is set but prerequisiteTechnology=5; -1 is expected",
            ),
        )
    }

    test("ignores a non-transition entry's prerequisite") {
        val file = fileWithGovts(listOf(govtEntry(prerequisiteTechnology = 5)))

        validateGovtTransitionHasNoPrerequisite(file) shouldBe emptyList()
    }

    test("returns no issues for Transition prerequisite check when GOVT is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateGovtTransitionHasNoPrerequisite(file) shouldBe emptyList()
    }

    test("allows the same entry to be both Default and Transition") {
        val file = fileWithGovts(listOf(govtEntry(defaultType = 1, transitionType = 1)))

        validateGovtDefaultCardinality(file) shouldBe emptyList()
        validateGovtTransitionCardinality(file) shouldBe emptyList()
        validateGovtDefaultHasNoPrerequisite(file) shouldBe emptyList()
        validateGovtTransitionHasNoPrerequisite(file) shouldBe emptyList()
    }
})
