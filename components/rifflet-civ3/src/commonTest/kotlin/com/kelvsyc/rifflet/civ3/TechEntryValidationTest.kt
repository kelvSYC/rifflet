package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun techEntry(
    name: String = "",
    era: Int = 0,
    prerequisite1: Int = -1,
    prerequisite2: Int = -1,
    prerequisite3: Int = -1,
    prerequisite4: Int = -1,
): TechEntry = TechEntry(
    name = name,
    civilopediaEntry = "",
    cost = 0,
    era = era,
    advanceIcon = 0,
    x = 0,
    y = 0,
    prerequisite1 = prerequisite1,
    prerequisite2 = prerequisite2,
    prerequisite3 = prerequisite3,
    prerequisite4 = prerequisite4,
    flags = 0,
    flavors = 0,
    unknown = ByteString.of(0, 0, 0, 0),
)

private fun fileWithTechs(entries: List<TechEntry>, major: Int = 12): Civ3File = Civ3File(
    Civ3Header(major = major, minor = 0, description = "", title = ""),
    listOf(TechSection(entries)),
)

class TechEntryValidationTest : FunSpec({

    test("findTechPrerequisiteCycle returns null for an acyclic graph") {
        val techs = listOf(
            techEntry(name = "Bronze Working"),
            techEntry(name = "Iron Working", prerequisite1 = 0),
        )

        findTechPrerequisiteCycle(techs) shouldBe null
    }

    test("findTechPrerequisiteCycle finds a 2-node cycle") {
        val techs = listOf(
            techEntry(name = "A", prerequisite1 = 1),
            techEntry(name = "B", prerequisite1 = 0),
        )

        val cycle = findTechPrerequisiteCycle(techs)
        cycle?.map { it.name } shouldBe listOf("A", "B", "A")
    }

    test("findTechPrerequisiteCycle finds a longer cycle") {
        val techs = listOf(
            techEntry(name = "A", prerequisite1 = 1),
            techEntry(name = "B", prerequisite1 = 2),
            techEntry(name = "C", prerequisite1 = 0),
        )

        val cycle = findTechPrerequisiteCycle(techs)
        cycle?.map { it.name } shouldBe listOf("A", "B", "C", "A")
    }

    test("findTechPrerequisiteCycle finds a self-loop") {
        val techs = listOf(techEntry(name = "A", prerequisite1 = 0))

        val cycle = findTechPrerequisiteCycle(techs)
        cycle?.map { it.name } shouldBe listOf("A", "A")
    }

    test("validateTechPrerequisitesSameEra returns no issues for a well-formed same-era graph") {
        val file = fileWithTechs(
            listOf(
                techEntry(name = "Bronze Working", era = 0),
                techEntry(name = "Iron Working", era = 0, prerequisite1 = 0),
            ),
        )

        validateTechPrerequisitesSameEra(file) shouldBe emptyList()
    }

    test("validateTechPrerequisitesSameEra returns no issues when TECH is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateTechPrerequisitesSameEra(file) shouldBe emptyList()
    }

    test("validateTechPrerequisitesSameEra flags a cross-era prerequisite") {
        val file = fileWithTechs(
            listOf(
                techEntry(name = "Bronze Working", era = 0),
                techEntry(name = "Gunpowder", era = 2, prerequisite1 = 0),
            ),
        )

        validateTechPrerequisitesSameEra(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TECH,
                1,
                "prerequisite1",
                "prerequisite1 resolves to a different-era tech (this entry's era=2, prerequisite1=0 resolves to era=0)",
            ),
        )
    }

    test("validateTechPrerequisitesAcyclic returns no issues for an acyclic graph") {
        val file = fileWithTechs(
            listOf(
                techEntry(name = "Bronze Working"),
                techEntry(name = "Iron Working", prerequisite1 = 0),
            ),
        )

        validateTechPrerequisitesAcyclic(file) shouldBe emptyList()
    }

    test("validateTechPrerequisitesAcyclic returns no issues when TECH is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateTechPrerequisitesAcyclic(file) shouldBe emptyList()
    }

    test("validateTechPrerequisitesAcyclic flags a cycle") {
        val file = fileWithTechs(
            listOf(
                techEntry(name = "A", prerequisite1 = 1),
                techEntry(name = "B", prerequisite1 = 0),
            ),
        )

        validateTechPrerequisitesAcyclic(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TECH,
                null,
                "prerequisite1/prerequisite2/prerequisite3/prerequisite4",
                "prerequisite graph contains a cycle: A -> B -> A",
            ),
        )
    }
})
