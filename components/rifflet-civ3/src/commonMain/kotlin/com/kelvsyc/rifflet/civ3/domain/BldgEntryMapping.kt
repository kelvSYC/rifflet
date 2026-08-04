package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.*

/**
 * Converts a parsed `BLDG` section to its domain-layer form. Each entry becomes one of
 * [Improvement], [SpaceshipPart], [SmallWonder], or [GreatWonder], discriminated in priority
 * order: `spaceshipPart != -1` → [SpaceshipPart]; else `wonder` → [GreatWonder]; else
 * `smallWonder` → [SmallWonder]; else → [Improvement]. This order is unambiguous for every real
 * file — `validateBldgSpaceshipPartInvariants` and `validateBldgNotBothWonderAndSmallWonder`
 * (both `ERROR`-level, zero real exceptions) together guarantee the three conditions never
 * overlap.
 *
 * [governments]/[techs] are the already domain-converted `GOVT`/`TECH` lists; [goods]/[units]
 * stay wire types (`GOOD`/`PRTO` don't have domain types yet).
 *
 * Throws [IllegalArgumentException] if this list's `requiredBuilding` graph, or any of the 3
 * `GreatWonder`-only effect-field graphs, contains a cycle — checked via [findSelfReferenceCycle]
 * before constructing any [Building], exactly like `TechEntry.toDomain()`'s cycle guard. This is
 * what makes every [Building] variant safe as a `data class`.
 */
fun List<BldgEntry>.toDomain(
    governments: List<Government>,
    techs: List<Tech>,
    goods: List<GoodEntry>,
    units: List<PrtoEntry>,
): List<Building> {
    val requiredBuildingCycle = findSelfReferenceCycle(this) { it.requirements.requiredBuilding }
    require(requiredBuildingCycle == null) {
        "BldgEntry requiredBuilding graph contains a cycle: ${requiredBuildingCycle?.joinToString(" -> ") { it.name }}"
    }
    val doublesHappinessCycle = findSelfReferenceCycle(this) { it.doublesHappiness }
    require(doublesHappinessCycle == null) {
        "BldgEntry doublesHappiness graph contains a cycle: ${doublesHappinessCycle?.joinToString(" -> ") { it.name }}"
    }
    val gainInEveryCityCycle = findSelfReferenceCycle(this) { it.gainInEveryCity }
    require(gainInEveryCityCycle == null) {
        "BldgEntry gainInEveryCity graph contains a cycle: ${gainInEveryCityCycle?.joinToString(" -> ") { it.name }}"
    }
    val gainInEveryCityOnContinentCycle = findSelfReferenceCycle(this) { it.gainInEveryCityOnContinent }
    require(gainInEveryCityOnContinentCycle == null) {
        "BldgEntry gainInEveryCityOnContinent graph contains a cycle: " +
            "${gainInEveryCityOnContinentCycle?.joinToString(" -> ") { it.name }}"
    }

    val buildings = map { entry ->
        when {
            entry.spaceshipPart != -1 -> SpaceshipPart(
                description = entry.description,
                name = entry.name,
                civilopediaEntry = entry.civilopediaEntry,
                cost = entry.cost,
                culture = entry.culture,
                maintenanceCost = entry.maintenanceCost,
                pollution = entry.pollution,
                production = entry.production,
                partIndex = entry.spaceshipPart,
                improvements = entry.improvements,
                otherCharacteristics = entry.otherCharacteristics,
                flavors = entry.flavors,
                unknown = entry.unknown,
            )
            entry.wonder -> GreatWonder(
                description = entry.description,
                name = entry.name,
                civilopediaEntry = entry.civilopediaEntry,
                cost = entry.cost,
                culture = entry.culture,
                maintenanceCost = entry.maintenanceCost,
                pollution = entry.pollution,
                production = entry.production,
                improvements = entry.improvements,
                otherCharacteristics = entry.otherCharacteristics,
                flavors = entry.flavors,
                unknown = entry.unknown,
                combatValues = entry.combatValues,
                navalDefenseBonus = entry.navalDefenseBonus,
                happiness = entry.happiness,
                numberOfRequiredBuildings = entry.numberOfRequiredBuildings,
                numberOfArmiesRequired = entry.numberOfArmiesRequired,
                wonders = entry.wonders,
                smallWonders = entry.smallWonders,
            )
            entry.smallWonder -> SmallWonder(
                description = entry.description,
                name = entry.name,
                civilopediaEntry = entry.civilopediaEntry,
                cost = entry.cost,
                culture = entry.culture,
                maintenanceCost = entry.maintenanceCost,
                pollution = entry.pollution,
                production = entry.production,
                improvements = entry.improvements,
                otherCharacteristics = entry.otherCharacteristics,
                flavors = entry.flavors,
                unknown = entry.unknown,
                combatValues = entry.combatValues,
                navalDefenseBonus = entry.navalDefenseBonus,
                happiness = entry.happiness,
                numberOfRequiredBuildings = entry.numberOfRequiredBuildings,
                numberOfArmiesRequired = entry.numberOfArmiesRequired,
                wonders = entry.wonders,
                smallWonders = entry.smallWonders,
            )
            else -> Improvement(
                description = entry.description,
                name = entry.name,
                civilopediaEntry = entry.civilopediaEntry,
                cost = entry.cost,
                culture = entry.culture,
                maintenanceCost = entry.maintenanceCost,
                pollution = entry.pollution,
                production = entry.production,
                improvements = entry.improvements,
                otherCharacteristics = entry.otherCharacteristics,
                flavors = entry.flavors,
                unknown = entry.unknown,
                combatValues = entry.combatValues,
                navalDefenseBonus = entry.navalDefenseBonus,
                happiness = entry.happiness,
                numberOfRequiredBuildings = entry.numberOfRequiredBuildings,
                numberOfArmiesRequired = entry.numberOfArmiesRequired,
            )
        }
    }

    forEachIndexed { index, entry ->
        val building = buildings[index]
        building.requirements = BldgRequirements(
            requiredBuilding = buildings.getOrNull(entry.requirements.requiredBuilding),
            requiredGovernment = governments.getOrNull(entry.requirements.requiredGovernment),
            requiredAdvance = techs.getOrNull(entry.requirements.requiredAdvance),
        )
        building.requiredResources = BldgRequiredResources(
            requiredResource1 = goods.getOrNull(entry.requiredResources.requiredResource1),
            requiredResource2 = goods.getOrNull(entry.requiredResources.requiredResource2),
        )
        if (building is StandardBuilding) {
            building.renderedObsoleteBy = techs.getOrNull(entry.renderedObsoleteBy)
            building.unitsProduced = entry.unitsProduced?.let {
                BldgUnitsProduced(unitProduced = units.getOrNull(it.unitProduced), unitFrequency = it.unitFrequency)
            }
        }
        if (building is GreatWonder) {
            building.doublesHappiness = buildings.getOrNull(entry.doublesHappiness)
            building.gainInEveryCity = buildings.getOrNull(entry.gainInEveryCity)
            building.gainInEveryCityOnContinent = buildings.getOrNull(entry.gainInEveryCityOnContinent)
        }
    }

    return buildings
}
