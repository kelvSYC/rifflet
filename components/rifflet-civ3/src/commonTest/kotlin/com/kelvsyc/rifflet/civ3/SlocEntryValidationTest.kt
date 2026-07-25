package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun slocEntry(x: Int, y: Int): SlocEntry = SlocEntry(ownerType = 2, owner = 0, x = x, y = y)

private fun fileWithStartLocations(entries: List<SlocEntry>): Civ3File =
    Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), listOf(SlocSection(entries)))

class SlocEntryValidationTest : FunSpec({

    test("returns no issues when x + y is even") {
        validateSlocCoordinateParity(fileWithStartLocations(listOf(slocEntry(x = 53, y = 19)))) shouldBe emptyList()
    }

    test("flags a SlocEntry whose x + y is odd (the real Sengoku Uesugi case)") {
        val file = fileWithStartLocations(listOf(slocEntry(x = 52, y = 19)))

        validateSlocCoordinateParity(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.SLOC,
                0,
                "x/y",
                "x=52, y=19 sum to an odd value; Civ3's isometric tile grid expects x and y to share parity",
            ),
        )
    }

    test("returns no issues when SLOC is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateSlocCoordinateParity(file) shouldBe emptyList()
    }
})
