package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun leadEntryForCardinalityCheck(name: String = ""): LeadEntry = LeadEntry(
    customCivData = 0, humanPlayer = 0, name = name, unknown = ByteString.of(*ByteArray(8)),
    startUnits = emptyList(), genderOfLeaderName = 0, startingTechnologyIds = emptyList(),
    difficulty = -2, initialEra = 0, startCash = 0, government = 0, civ = -3, color = 0,
    skipFirstTurn = 0, unknown2 = ByteString.of(*ByteArray(4)), startEmbassies = 0,
)

private fun wmapEntry(numberOfCivs: Int): WmapEntry = WmapEntry(
    resourceIds = emptyList(), numberOfContinents = 0, height = 0, distanceBetweenCivs = 0,
    numberOfCivs = numberOfCivs, unknown1 = ByteString.of(*ByteArray(8)), width = 0,
    unknown2 = ByteString.of(*ByteArray(128)), mapSeed = 0, flags = 0,
)

private fun fileWithLeadAndWmap(leadCount: Int, numberOfCivs: Int): Civ3File = Civ3File(
    Civ3Header(major = 12, minor = 0, description = "", title = ""),
    listOf(
        LeadSection(List(leadCount) { leadEntryForCardinalityCheck() }),
        WmapSection(listOf(wmapEntry(numberOfCivs))),
    ),
)

class LeadEntryValidationTest : FunSpec({

    test("returns no issues when LEAD count matches WMAP.numberOfCivs") {
        val file = fileWithLeadAndWmap(leadCount = 8, numberOfCivs = 8)

        validateLeadCountMatchesWmapNumberOfCivs(file) shouldBe emptyList()
    }

    test("flags a LEAD count that doesn't match WMAP.numberOfCivs") {
        val file = fileWithLeadAndWmap(leadCount = 7, numberOfCivs = 8)

        validateLeadCountMatchesWmapNumberOfCivs(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.LEAD,
                null,
                "entries",
                "LEAD has 7 entries; WMAP declares 8 civs",
            ),
        )
    }

    test("returns no issues when WMAP is absent") {
        val file = Civ3File(
            Civ3Header(major = 12, minor = 0, description = "", title = ""),
            sections = listOf(LeadSection(List(7) { leadEntryForCardinalityCheck() })),
        )

        validateLeadCountMatchesWmapNumberOfCivs(file) shouldBe emptyList()
    }

    test("returns no issues when LEAD is absent") {
        val file = Civ3File(
            Civ3Header(major = 12, minor = 0, description = "", title = ""),
            sections = listOf(WmapSection(listOf(wmapEntry(8)))),
        )

        validateLeadCountMatchesWmapNumberOfCivs(file) shouldBe emptyList()
    }
})
