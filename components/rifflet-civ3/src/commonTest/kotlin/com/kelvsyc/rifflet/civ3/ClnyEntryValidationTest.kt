package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun clnyEntry(x: Int, y: Int): ClnyEntry = ClnyEntry(
    ownerType = 2,
    owner = 0,
    x = x,
    y = y,
    improvementType = 0,
)

private fun fileWithColonies(entries: List<ClnyEntry>): Civ3File =
    Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), listOf(ClnySection(entries)))

class ClnyEntryValidationTest : FunSpec({

    test("returns no issues when x + y is even") {
        validateClnyCoordinateParity(fileWithColonies(listOf(clnyEntry(x = 52, y = 20)))) shouldBe emptyList()
    }

    test("flags a ClnyEntry whose x + y is odd") {
        val file = fileWithColonies(listOf(clnyEntry(x = 52, y = 19)))

        validateClnyCoordinateParity(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.CLNY,
                0,
                "x/y",
                "x=52, y=19 sum to an odd value; Civ3's isometric tile grid expects x and y to share parity",
            ),
        )
    }

    test("returns no issues when CLNY is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateClnyCoordinateParity(file) shouldBe emptyList()
    }
})
