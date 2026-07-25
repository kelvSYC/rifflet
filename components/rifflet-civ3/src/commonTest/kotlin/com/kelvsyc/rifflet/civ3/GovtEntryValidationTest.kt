package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun govtEntry(corruption: Int): GovtEntry = GovtEntry(
    defaultType = 0,
    transitionType = 0,
    requiresMaintenance = 0,
    toggle1 = 0,
    tilePenalty = 0,
    tradeBonus = 0,
    name = "",
    civilopediaEntry = "",
    maleRulerTitle1 = "",
    femaleRulerTitle1 = "",
    maleRulerTitle2 = "",
    femaleRulerTitle2 = "",
    maleRulerTitle3 = "",
    femaleRulerTitle3 = "",
    maleRulerTitle4 = "",
    femaleRulerTitle4 = "",
    corruption = corruption,
    immuneTo = -1,
    diplomatsAre = 0,
    spiesAre = 0,
    relationships = emptyList(),
    hurrying = 0,
    assimilationChance = 0,
    draftLimit = 0,
    militaryPoliceLimit = 0,
    rulerTitlePairsUsed = 0,
    prerequisiteTechnology = -1,
    scienceRateCap = 0,
    workerRate = 0,
    toggle2 = 0,
    toggle3 = 0,
    unknown = ByteString.of(*ByteArray(4)),
    freeUnits = 0,
    freeUnitsPerTown = 0,
    freeUnitsPerCity = 0,
    freeUnitsPerMetropolis = 0,
    unitCost = 0,
    warWeariness = 0,
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

    test("flags a corruption value outside the documented 0-6 range") {
        val file = fileWithGovts(listOf(govtEntry(corruption = 7)))

        validateGovtCorruption(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.GOVT,
                0,
                "corruption",
                "corruption=7 is not a valid GovtCorruption index (0..6)",
            ),
        )
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
})
