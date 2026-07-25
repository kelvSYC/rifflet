package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun cityEntry(x: Int, y: Int): CityEntry = CityEntry(
    hasWalls = 0,
    hasPalace = 0,
    name = "",
    ownerType = 2,
    buildingIds = emptyList(),
    culture = 0,
    owner = 0,
    size = 0,
    x = x,
    y = y,
    cityLevel = 0,
    borderLevel = 0,
    useAutoName = 0,
)

private fun fileWithCities(entries: List<CityEntry>): Civ3File =
    Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), listOf(CitySection(entries)))

class CityEntryValidationTest : FunSpec({

    test("returns no issues when x + y is even") {
        validateCityCoordinateParity(fileWithCities(listOf(cityEntry(x = 52, y = 20)))) shouldBe emptyList()
    }

    test("flags a CityEntry whose x + y is odd") {
        val file = fileWithCities(listOf(cityEntry(x = 52, y = 19)))

        validateCityCoordinateParity(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.CITY,
                0,
                "x/y",
                "x=52, y=19 sum to an odd value; Civ3's isometric tile grid expects x and y to share parity",
            ),
        )
    }

    test("returns no issues when CITY is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateCityCoordinateParity(file) shouldBe emptyList()
    }
})
