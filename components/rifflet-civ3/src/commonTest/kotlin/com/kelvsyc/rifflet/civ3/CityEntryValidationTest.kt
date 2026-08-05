package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun cityEntry(
    x: Int,
    y: Int,
    name: String = "",
    hasWalls: Byte = 0,
    hasPalace: Byte = 0,
    ownerType: Int = 2,
    owner: Int = 0,
    buildingIds: List<Int> = emptyList(),
): CityEntry = CityEntry(
    hasWalls = hasWalls,
    hasPalace = hasPalace,
    name = name,
    ownerType = ownerType,
    buildingIds = buildingIds,
    culture = 0,
    owner = owner,
    size = 0,
    x = x,
    y = y,
    cityLevel = 0,
    borderLevel = 0,
    useAutoName = 0,
)

private fun fileWithCities(entries: List<CityEntry>): Civ3File =
    Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), listOf(CitySection(entries)))

private fun bytesFor(value: Int): List<Byte> = listOf(
    (value and 0xFF).toByte(),
    ((value shr 8) and 0xFF).toByte(),
    ((value shr 16) and 0xFF).toByte(),
    ((value shr 24) and 0xFF).toByte(),
)

private fun bldgEntry(name: String = "", improvements: Int = 0, otherCharacteristics: Int = 0): BldgEntry = BldgEntry(
    description = "",
    name = name,
    civilopediaEntry = "",
    doublesHappiness = -1,
    gainInEveryCity = -1,
    gainInEveryCityOnContinent = -1,
    requirements = BldgRequirements(requiredBuilding = -1, requiredGovernment = -1, requiredAdvance = -1),
    cost = 0,
    culture = 0,
    combatValues = BldgCombatValues(bombardDefense = 0, navalBombardDefense = 0, defenseBonus = 0, airPower = 0, navalPower = 0),
    navalDefenseBonus = 0,
    maintenanceCost = 0,
    happiness = BldgHappiness(contentFacesAllCities = 0, contentFaces = 0, unhappyFacesAllCities = 0, unhappyFaces = 0),
    numberOfRequiredBuildings = 1,
    pollution = 0,
    production = 0,
    spaceshipPart = -1,
    renderedObsoleteBy = -1,
    requiredResources = BldgRequiredResources(requiredResource1 = -1, requiredResource2 = -1),
    flags = ByteString.of(*(bytesFor(improvements) + bytesFor(otherCharacteristics) + bytesFor(0) + bytesFor(0)).toByteArray()),
    numberOfArmiesRequired = 0,
    flavors = 0,
    unknown = ByteString.of(*ByteArray(4)),
    unitsProduced = BldgUnitsProduced(unitProduced = -1, unitFrequency = 0),
)

private fun fileWithCitiesAndBldgs(cities: List<CityEntry>, buildings: List<BldgEntry>): Civ3File = Civ3File(
    Civ3Header(major = 12, minor = 0, description = "", title = ""),
    listOf(CitySection(cities), BldgSection(buildings)),
)

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

    test("validateCityHasPalaceMatchesCenterOfEmpire returns no issues when they agree") {
        val palace = bldgEntry(name = "Palace", improvements = 1 shl 0)
        val agreesTrue = cityEntry(x = 0, y = 0, hasPalace = 1, buildingIds = listOf(0))
        val agreesFalse = cityEntry(x = 0, y = 0, hasPalace = 0, buildingIds = emptyList())
        val file = fileWithCitiesAndBldgs(listOf(agreesTrue, agreesFalse), listOf(palace))

        validateCityHasPalaceMatchesCenterOfEmpire(file) shouldBe emptyList()
    }

    test("validateCityHasPalaceMatchesCenterOfEmpire flags a disagreement") {
        val palace = bldgEntry(name = "Palace", improvements = 1 shl 0)
        val city = cityEntry(x = 0, y = 0, hasPalace = 0, buildingIds = listOf(0))
        val file = fileWithCitiesAndBldgs(listOf(city), listOf(palace))

        val issues = validateCityHasPalaceMatchesCenterOfEmpire(file)
        issues.size shouldBe 1
        issues.single().severity shouldBe ValidationSeverity.ERROR
        issues.single().field shouldBe "hasPalace"
    }

    test("validateCityHasPalaceMatchesCenterOfEmpire returns no issues when BLDG is absent") {
        val file = fileWithCities(listOf(cityEntry(x = 0, y = 0, hasPalace = 1)))

        validateCityHasPalaceMatchesCenterOfEmpire(file) shouldBe emptyList()
    }

    test("validateCityGreatWonderUniqueGlobally returns no issues when a Great Wonder is in one city") {
        val wonder = bldgEntry(name = "Oracle", otherCharacteristics = 1 shl 2)
        val city = cityEntry(x = 0, y = 0, buildingIds = listOf(0))
        val file = fileWithCitiesAndBldgs(listOf(city), listOf(wonder))

        validateCityGreatWonderUniqueGlobally(file) shouldBe emptyList()
    }

    test("validateCityGreatWonderUniqueGlobally flags a Great Wonder in more than one city") {
        val wonder = bldgEntry(name = "Oracle", otherCharacteristics = 1 shl 2)
        val city1 = cityEntry(x = 0, y = 0, buildingIds = listOf(0))
        val city2 = cityEntry(x = 2, y = 0, buildingIds = listOf(0))
        val file = fileWithCitiesAndBldgs(listOf(city1, city2), listOf(wonder))

        val issues = validateCityGreatWonderUniqueGlobally(file)
        issues.size shouldBe 1
        issues.single().severity shouldBe ValidationSeverity.ERROR
    }

    test("validateCityGreatWonderUniqueGlobally returns no issues when BLDG is absent") {
        val file = fileWithCities(listOf(cityEntry(x = 0, y = 0, buildingIds = listOf(0))))

        validateCityGreatWonderUniqueGlobally(file) shouldBe emptyList()
    }

    test("validateCitySmallWonderUniquePerNation returns no issues for the same Small Wonder under different nations") {
        val wonder = bldgEntry(name = "Pyramids", otherCharacteristics = 1 shl 3)
        val city1 = cityEntry(x = 0, y = 0, ownerType = 2, owner = 0, buildingIds = listOf(0))
        val city2 = cityEntry(x = 2, y = 0, ownerType = 2, owner = 1, buildingIds = listOf(0))
        val file = fileWithCitiesAndBldgs(listOf(city1, city2), listOf(wonder))

        validateCitySmallWonderUniquePerNation(file) shouldBe emptyList()
    }

    test("validateCitySmallWonderUniquePerNation flags a Small Wonder in more than one city for the same nation") {
        val wonder = bldgEntry(name = "Pyramids", otherCharacteristics = 1 shl 3)
        val city1 = cityEntry(x = 0, y = 0, ownerType = 2, owner = 0, buildingIds = listOf(0))
        val city2 = cityEntry(x = 2, y = 0, ownerType = 2, owner = 0, buildingIds = listOf(0))
        val file = fileWithCitiesAndBldgs(listOf(city1, city2), listOf(wonder))

        val issues = validateCitySmallWonderUniquePerNation(file)
        issues.size shouldBe 1
        issues.single().severity shouldBe ValidationSeverity.ERROR
    }

    test("validateCitySmallWonderUniquePerNation flags a Small Wonder in more than one city for the same player") {
        val wonder = bldgEntry(name = "Pyramids", otherCharacteristics = 1 shl 3)
        val city1 = cityEntry(x = 0, y = 0, ownerType = 3, owner = 0, buildingIds = listOf(0))
        val city2 = cityEntry(x = 2, y = 0, ownerType = 3, owner = 0, buildingIds = listOf(0))
        val file = fileWithCitiesAndBldgs(listOf(city1, city2), listOf(wonder))

        val issues = validateCitySmallWonderUniquePerNation(file)
        issues.size shouldBe 1
    }

    test("validateCitySmallWonderUniquePerNation returns no issues when BLDG is absent") {
        val file = fileWithCities(listOf(cityEntry(x = 0, y = 0, buildingIds = listOf(0))))

        validateCitySmallWonderUniquePerNation(file) shouldBe emptyList()
    }

    test("validateCityOwnerTypeRecognized returns no issues for ownerType in 0..3") {
        val file = fileWithCities(listOf(cityEntry(x = 0, y = 0, ownerType = 0), cityEntry(x = 0, y = 0, ownerType = 3)))

        validateCityOwnerTypeRecognized(file) shouldBe emptyList()
    }

    test("validateCityOwnerTypeRecognized flags an out-of-range ownerType") {
        val file = fileWithCities(listOf(cityEntry(x = 0, y = 0, ownerType = 4)))

        val issues = validateCityOwnerTypeRecognized(file)
        issues.size shouldBe 1
        issues.single().severity shouldBe ValidationSeverity.ERROR
        issues.single().field shouldBe "ownerType"
    }
})
