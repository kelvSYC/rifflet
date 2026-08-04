package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.*
import com.kelvsyc.rifflet.civ3.BldgCombatValues
import com.kelvsyc.rifflet.civ3.BldgHappiness
import com.kelvsyc.rifflet.civ3.BldgRequiredResources as BldgRequiredResourcesWire
import com.kelvsyc.rifflet.civ3.BldgRequirements as BldgRequirementsWire
import com.kelvsyc.rifflet.civ3.BldgUnitsProduced as BldgUnitsProducedWire

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

/**
 * Converts a `BLDG` section's domain-layer form back to wire entries, resolving each [Building]'s
 * object references back into indices against [governments]/[techs]/[goods]/[units] and this
 * list's own roster.
 *
 * Throws [IllegalArgumentException] if any cross-reference resolves to an object not present in
 * the corresponding list argument — a dangling reference at encode time is a real bug, not
 * something to default silently. Whatever fields a [Building] variant doesn't carry (per its own
 * type — see `Building.kt`'s KDoc) are written back as the `ERROR`-confirmed constant every real
 * file uses for that variant (`-1` for absent self-references, `0`/`1` for the corresponding
 * scalar defaults).
 *
 * Since every [Building] variant is a `data class`, the self-referencing `indexOf` lookups below
 * are structural-equality matches, not true reference identity — the same narrow, accepted
 * limitation already documented on GOVT's/TECH's `toWire()`.
 */
fun List<Building>.toWire(
    governments: List<Government>,
    techs: List<Tech>,
    goods: List<GoodEntry>,
    units: List<PrtoEntry>,
): List<BldgEntry> {
    val roster = this

    fun resolveBuilding(field: String, target: Building?): Int = target?.let {
        val index = roster.indexOf(it)
        require(index >= 0) { "Building.$field references a Building not present in this list" }
        index
    } ?: -1

    fun resolveGovernment(target: Government?): Int = target?.let {
        val index = governments.indexOf(it)
        require(index >= 0) { "BldgRequirements.requiredGovernment references a Government not present in governments" }
        index
    } ?: -1

    fun resolveTech(field: String, target: Tech?): Int = target?.let {
        val index = techs.indexOf(it)
        require(index >= 0) { "Building.$field references a Tech not present in techs" }
        index
    } ?: -1

    fun resolveGood(field: String, target: GoodEntry?): Int = target?.let {
        val index = goods.indexOf(it)
        require(index >= 0) { "BldgRequiredResources.$field references a GoodEntry not present in goods" }
        index
    } ?: -1

    fun resolveUnit(target: PrtoEntry?): Int = target?.let {
        val index = units.indexOf(it)
        require(index >= 0) { "BldgUnitsProduced.unitProduced references a PrtoEntry not present in units" }
        index
    } ?: -1

    return map { building ->
        val doublesHappiness = if (building is GreatWonder) resolveBuilding("doublesHappiness", building.doublesHappiness) else -1
        val gainInEveryCity = if (building is GreatWonder) resolveBuilding("gainInEveryCity", building.gainInEveryCity) else -1
        val gainInEveryCityOnContinent = if (building is GreatWonder) {
            resolveBuilding("gainInEveryCityOnContinent", building.gainInEveryCityOnContinent)
        } else {
            -1
        }
        val wonders = if (building is Wonder) building.wonders else 0
        val smallWonders = if (building is Wonder) building.smallWonders else 0
        val combatValues = if (building is StandardBuilding) building.combatValues else BldgCombatValuesZero
        val navalDefenseBonus = if (building is StandardBuilding) building.navalDefenseBonus else 0
        val happiness = if (building is StandardBuilding) building.happiness else BldgHappinessZero
        val numberOfRequiredBuildings = if (building is StandardBuilding) building.numberOfRequiredBuildings else 0
        val numberOfArmiesRequired = if (building is StandardBuilding) building.numberOfArmiesRequired else 0
        val renderedObsoleteBy = if (building is StandardBuilding) resolveTech("renderedObsoleteBy", building.renderedObsoleteBy) else -1
        val unitsProduced = if (building is StandardBuilding) {
            building.unitsProduced?.let {
                BldgUnitsProducedWire(unitProduced = resolveUnit(it.unitProduced), unitFrequency = it.unitFrequency)
            }
        } else {
            BldgUnitsProducedWire(unitProduced = -1, unitFrequency = 0)
        }
        val partIndex = if (building is SpaceshipPart) building.partIndex else -1

        BldgEntry(
            description = building.description,
            name = building.name,
            civilopediaEntry = building.civilopediaEntry,
            doublesHappiness = doublesHappiness,
            gainInEveryCity = gainInEveryCity,
            gainInEveryCityOnContinent = gainInEveryCityOnContinent,
            requirements = BldgRequirementsWire(
                requiredBuilding = resolveBuilding("requirements.requiredBuilding", building.requirements.requiredBuilding),
                requiredGovernment = resolveGovernment(building.requirements.requiredGovernment),
                requiredAdvance = resolveTech("requirements.requiredAdvance", building.requirements.requiredAdvance),
            ),
            cost = building.cost,
            culture = building.culture,
            combatValues = combatValues,
            navalDefenseBonus = navalDefenseBonus,
            maintenanceCost = building.maintenanceCost,
            happiness = happiness,
            numberOfRequiredBuildings = numberOfRequiredBuildings,
            pollution = building.pollution,
            production = building.production,
            spaceshipPart = partIndex,
            renderedObsoleteBy = renderedObsoleteBy,
            requiredResources = BldgRequiredResourcesWire(
                requiredResource1 = resolveGood("requiredResource1", building.requiredResources.requiredResource1),
                requiredResource2 = resolveGood("requiredResource2", building.requiredResources.requiredResource2),
            ),
            flags = flagsToByteString(
                building.improvements,
                building.otherCharacteristics or
                    (if (building is Wonder && building !is GreatWonder) 1 shl 3 else 0) or
                    (if (building is GreatWonder) 1 shl 2 else 0),
                smallWonders,
                wonders,
            ),
            numberOfArmiesRequired = numberOfArmiesRequired,
            flavors = building.flavors,
            unknown = building.unknown,
            unitsProduced = unitsProduced,
        )
    }
}

private val BldgCombatValuesZero = BldgCombatValues(0, 0, 0, 0, 0)
private val BldgHappinessZero = BldgHappiness(0, 0, 0, 0)

/**
 * `building.otherCharacteristics` never carries the `wonder`/`smallWonder` discriminant bits
 * (bits 2/3) — `BldgFlags.kt` declares no settable accessor for them, since which [Building]
 * subtype an instance is already expresses that information. This function ORs those 2 bits back
 * in from the caller's own type check before packing, so the wire round-trip is exact: `SmallWonder`
 * sets bit 3, `GreatWonder` sets bit 2, `Improvement`/`SpaceshipPart` set neither. `Wonder` is
 * `SmallWonder`'s and `GreatWonder`'s shared supertype, so `building is Wonder && building !is
 * GreatWonder` isolates exactly the `SmallWonder` case.
 */
private fun flagsToByteString(improvements: Int, otherCharacteristics: Int, smallWonders: Int, wonders: Int): okio.ByteString {
    val buffer = okio.Buffer()
    buffer.writeIntLe(improvements)
    buffer.writeIntLe(otherCharacteristics)
    buffer.writeIntLe(smallWonders)
    buffer.writeIntLe(wonders)
    return buffer.readByteString()
}
