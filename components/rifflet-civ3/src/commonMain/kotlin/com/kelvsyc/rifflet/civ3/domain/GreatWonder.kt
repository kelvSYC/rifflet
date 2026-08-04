package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.BldgCombatValues
import com.kelvsyc.rifflet.civ3.BldgHappiness
import okio.ByteString

/**
 * A Great Wonder — the only [Building] variant carrying [doublesHappiness]/[gainInEveryCity]/
 * [gainInEveryCityOnContinent], confirmed `ERROR`-level absent from every other variant.
 *
 * @param doublesHappiness The building whose happiness effect this Wonder doubles, if any — e.g.
 *   the Oracle doubles the effect of Temple.
 * @param gainInEveryCity The building this Wonder grants for free in every city, if any — e.g.
 *   the Pyramids grant a free Granary.
 * @param gainInEveryCityOnContinent The building this Wonder grants for free in every city on the
 *   same continent, if any — e.g. Hoover Dam grants a free Hydro Plant.
 */
data class GreatWonder(
    override var description: String,
    override var name: String,
    override var civilopediaEntry: String,
    override var cost: Int,
    override var culture: Int,
    override var maintenanceCost: Int,
    override var pollution: Int,
    override var production: Int,
    override var requirements: BldgRequirements = BldgRequirements(),
    override var requiredResources: BldgRequiredResources = BldgRequiredResources(),
    override var improvements: Int = 0,
    override var otherCharacteristics: Int = 0,
    override var flavors: Int = 0,
    override var unknown: ByteString = ByteString.of(0, 0, 0, 0),
    override var combatValues: BldgCombatValues = BldgCombatValues(0, 0, 0, 0, 0),
    override var navalDefenseBonus: Int = 0,
    override var happiness: BldgHappiness = BldgHappiness(0, 0, 0, 0),
    override var numberOfRequiredBuildings: Int = 0,
    override var numberOfArmiesRequired: Int = 0,
    override var renderedObsoleteBy: Tech? = null,
    override var unitsProduced: BldgUnitsProduced? = null,
    override var wonders: Int = 0,
    override var smallWonders: Int = 0,
    var doublesHappiness: Building? = null,
    var gainInEveryCity: Building? = null,
    var gainInEveryCityOnContinent: Building? = null,
) : Wonder
