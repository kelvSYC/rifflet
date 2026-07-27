package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validCityEntry(
    ownerType: Int = 0,
    owner: Int = 0,
    buildingIds: List<Int> = emptyList(),
): CityEntry = CityEntry(
    hasWalls = 0,
    hasPalace = 0,
    name = "",
    ownerType = ownerType,
    buildingIds = buildingIds,
    culture = 0,
    owner = owner,
    size = 0,
    x = 0,
    y = 0,
    cityLevel = 0,
    borderLevel = 0,
    useAutoName = 0,
)

private fun validBldgEntry(): BldgEntry = BldgEntry(
    description = "",
    name = "",
    civilopediaEntry = "",
    doublesHappiness = 0,
    gainInEveryCity = 0,
    gainInEveryCityOnContinent = 0,
    requirements = BldgRequirements(requiredBuilding = 0, requiredGovernment = 0, requiredAdvance = 0),
    cost = 0,
    culture = 0,
    combatValues = BldgCombatValues(
        bombardDefense = 0, navalBombardDefense = 0, defenseBonus = 0, airPower = 0, navalPower = 0,
    ),
    navalDefenseBonus = 0,
    maintenanceCost = 0,
    happiness = BldgHappiness(
        contentFacesAllCities = 0, contentFaces = 0, unhappyFacesAllCities = 0, unhappyFaces = 0,
    ),
    numberOfRequiredBuildings = 0,
    pollution = 0,
    production = 0,
    spaceshipPart = 0,
    renderedObsoleteBy = 0,
    requiredResources = BldgRequiredResources(requiredResource1 = 0, requiredResource2 = 0),
    flags = ByteString.of(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    numberOfArmiesRequired = 0,
    flavors = 0,
    unknown = ByteString.of(0, 0, 0, 0),
    unitsProduced = BldgUnitsProduced(unitProduced = 0, unitFrequency = 0),
)

class CityEntryReferencesTest : FunSpec({

    test("resolveOwner delegates to the shared Owner resolution") {
        validCityEntry(ownerType = 1).resolveOwner(emptyList<RaceEntry>()) shouldBe Owner.Barbarian
    }

    test("buildingsBldg resolves each id, preserving position and length") {
        val bldg = validBldgEntry()
        val entry = validCityEntry(buildingIds = listOf(0, 5))
        entry.buildingsBldg(listOf(bldg)) shouldBe listOf(bldg, null)
    }
})
