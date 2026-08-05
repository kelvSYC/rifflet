package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun clnyEntry(x: Int, y: Int, ownerType: Int = 2, owner: Int = 0): ClnyEntry = ClnyEntry(
    ownerType = ownerType,
    owner = owner,
    x = x,
    y = y,
    improvementType = ClnyImprovementType.COLONY,
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

    test("validateClnyOwnerRequiresRealNation returns no issues for ownerType 2 or 3") {
        val file = fileWithColonies(listOf(clnyEntry(x = 0, y = 0, ownerType = 2), clnyEntry(x = 0, y = 0, ownerType = 3)))

        validateClnyOwnerRequiresRealNation(file) shouldBe emptyList()
    }

    test("validateClnyOwnerRequiresRealNation flags ownerType=0") {
        val file = fileWithColonies(listOf(clnyEntry(x = 0, y = 0, ownerType = 0)))

        val issues = validateClnyOwnerRequiresRealNation(file)
        issues.size shouldBe 1
        issues.single().severity shouldBe ValidationSeverity.ERROR
        issues.single().field shouldBe "ownerType"
    }

    test("validateClnyOwnerRequiresRealNation flags ownerType=1") {
        val file = fileWithColonies(listOf(clnyEntry(x = 0, y = 0, ownerType = 1)))

        val issues = validateClnyOwnerRequiresRealNation(file)
        issues.size shouldBe 1
        issues.single().severity shouldBe ValidationSeverity.ERROR
    }

    test("validateClnyOwnerRequiresRealNation returns no issues when CLNY is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateClnyOwnerRequiresRealNation(file) shouldBe emptyList()
    }

    test("validateClnyOwnerNotBarbarianPlaceholderCiv returns no issues for a non-zero Civilization owner") {
        val file = fileWithColonies(listOf(clnyEntry(x = 0, y = 0, ownerType = 2, owner = 1)))

        validateClnyOwnerNotBarbarianPlaceholderCiv(file) shouldBe emptyList()
    }

    test("validateClnyOwnerNotBarbarianPlaceholderCiv flags ownerType=2, owner=0") {
        val file = fileWithColonies(listOf(clnyEntry(x = 0, y = 0, ownerType = 2, owner = 0)))

        val issues = validateClnyOwnerNotBarbarianPlaceholderCiv(file)
        issues.size shouldBe 1
        issues.single().severity shouldBe ValidationSeverity.ERROR
        issues.single().field shouldBe "owner"
    }

    test("validateClnyOwnerNotBarbarianPlaceholderCiv returns no issues when CLNY is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateClnyOwnerNotBarbarianPlaceholderCiv(file) shouldBe emptyList()
    }
})
