package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun unitEntry(x: Int, y: Int): UnitEntry = UnitEntry(
    legacyName = "",
    ownerType = 2,
    experienceLevel = 0,
    owner = 0,
    unitType = 0,
    aiStrategy = 0,
    x = x,
    y = y,
    ptwName = "",
    useCivilizationKing = 0,
)

private fun fileWithUnits(entries: List<UnitEntry>): Civ3File =
    Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), listOf(UnitSection(entries)))

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
})
