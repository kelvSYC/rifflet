package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun unitEntry(x: Int, y: Int, ownerType: Int = 2, owner: Int = 0): UnitEntry = UnitEntry(
    legacyName = "",
    ownerType = ownerType,
    experienceLevel = 0,
    owner = owner,
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
})
