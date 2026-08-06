package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun slocEntry(x: Int = 0, y: Int = 0, ownerType: Int = 2, owner: Int = 0): SlocEntry =
    SlocEntry(ownerType = ownerType, owner = owner, x = x, y = y)

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

    test("validateSlocOwnerTypeRecognized returns no issues for ownerType in range") {
        val file = fileWithStartLocations(listOf(slocEntry(ownerType = 0), slocEntry(ownerType = 3)))

        validateSlocOwnerTypeRecognized(file) shouldBe emptyList()
    }

    test("validateSlocOwnerTypeRecognized flags an out-of-range ownerType") {
        val file = fileWithStartLocations(listOf(slocEntry(ownerType = 4)))

        validateSlocOwnerTypeRecognized(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.SLOC,
                0,
                "ownerType",
                "ownerType=4 is not a recognized value (0..3)",
            ),
        )
    }

    test("validateSlocOwnerTypeRecognized returns no issues when SLOC is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateSlocOwnerTypeRecognized(file) shouldBe emptyList()
    }

    test("validateSlocOwnerNotBarbarian returns no issues for a non-Barbarian ownerType") {
        val file = fileWithStartLocations(listOf(slocEntry(ownerType = 2)))

        validateSlocOwnerNotBarbarian(file) shouldBe emptyList()
    }

    test("validateSlocOwnerNotBarbarian flags ownerType=1") {
        val file = fileWithStartLocations(listOf(slocEntry(ownerType = 1)))

        validateSlocOwnerNotBarbarian(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.SLOC,
                0,
                "ownerType",
                "ownerType=1 (Barbarian) is not allowed for SLOC entries; the Rules/Scenario editor " +
                    "refuses to assign a starting location to Barbarians",
            ),
        )
    }

    test("validateSlocOwnerNotBarbarian returns no issues when SLOC is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateSlocOwnerNotBarbarian(file) shouldBe emptyList()
    }

    test("validateSlocUniqueOwner returns no issues when owners are distinct") {
        val file = fileWithStartLocations(
            listOf(
                slocEntry(x = 0, y = 0, ownerType = 2, owner = 0),
                slocEntry(x = 2, y = 0, ownerType = 2, owner = 1),
            ),
        )

        validateSlocUniqueOwner(file) shouldBe emptyList()
    }

    test("validateSlocUniqueOwner returns no issues for multiple None-owned entries") {
        val file = fileWithStartLocations(
            listOf(
                slocEntry(x = 0, y = 0, ownerType = 0, owner = 0),
                slocEntry(x = 2, y = 0, ownerType = 0, owner = 0),
            ),
        )

        validateSlocUniqueOwner(file) shouldBe emptyList()
    }

    test("validateSlocUniqueOwner flags two entries sharing the same Civilization owner") {
        val file = fileWithStartLocations(
            listOf(
                slocEntry(x = 0, y = 0, ownerType = 2, owner = 0),
                slocEntry(x = 2, y = 0, ownerType = 2, owner = 0),
            ),
        )

        validateSlocUniqueOwner(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.SLOC,
                null,
                "owner",
                "ownerType=2/owner=0 has more than one starting location: [0, 1]",
            ),
        )
    }

    test("validateSlocUniqueOwner returns no issues when SLOC is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateSlocUniqueOwner(file) shouldBe emptyList()
    }

    test("validateSlocOwnerNotBarbarianPlaceholderCiv returns no issues for a non-zero Civilization owner") {
        val file = fileWithStartLocations(listOf(slocEntry(ownerType = 2, owner = 1)))

        validateSlocOwnerNotBarbarianPlaceholderCiv(file) shouldBe emptyList()
    }

    test("validateSlocOwnerNotBarbarianPlaceholderCiv flags ownerType=2, owner=0") {
        val file = fileWithStartLocations(listOf(slocEntry(ownerType = 2, owner = 0)))

        val issues = validateSlocOwnerNotBarbarianPlaceholderCiv(file)
        issues.size shouldBe 1
        issues.single().severity shouldBe ValidationSeverity.ERROR
        issues.single().field shouldBe "owner"
    }

    test("validateSlocOwnerNotBarbarianPlaceholderCiv returns no issues when SLOC is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateSlocOwnerNotBarbarianPlaceholderCiv(file) shouldBe emptyList()
    }
})
