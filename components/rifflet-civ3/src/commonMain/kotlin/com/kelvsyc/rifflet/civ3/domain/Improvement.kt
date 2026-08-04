package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.BldgCombatValues
import com.kelvsyc.rifflet.civ3.BldgHappiness
import okio.ByteString

/**
 * A plain Improvement — the Rules Editor's Category radio group's default option, carrying none
 * of [Wonder]'s or [SpaceshipPart]'s specialized fields.
 */
data class Improvement(
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
) : StandardBuilding
