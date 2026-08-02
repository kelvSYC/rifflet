package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `BLDG` section: a building or wonder definition.
 *
 * @param doublesHappiness A `BLDG` section self-reference (`-1` for none) identifying the
 *   building whose happiness effect this entry doubles — e.g. the Oracle doubles the effect of
 *   Temple, and Sistine Chapel doubles the effect of Cathedral.
 * @param gainInEveryCity A `BLDG` section self-reference (`-1` for none) identifying the building
 *   this entry grants for free in every city in the civilization.
 * @param gainInEveryCityOnContinent A `BLDG` section self-reference (`-1` for none) identifying
 *   the building this entry grants for free in every city on the same continent — e.g. the
 *   Pyramids grant a free Granary, the Great Wall grants free Walls, and Hoover Dam grants a free
 *   Hydro Plant.
 * @param requirements This building's prerequisites (advance, other required building,
 *   government). See [BldgRequirements].
 * @param combatValues This building's combat modifiers. See [BldgCombatValues].
 * @param navalDefenseBonus 4 bytes with zero documented behavior; preserved raw, not validated.
 *   Despite its similar name, this is not part of [combatValues] — it doesn't correspond to any
 *   control in either the PTW or Conquests "Improvements and Wonders" tab.
 * @param happiness This building's happiness effect. See [BldgHappiness].
 * @param spaceshipPart `-1` if this building doesn't produce a spaceship part, or an index into
 *   `RuleEntry.spaceshipPartQuantities` identifying which part it produces — the Conquests base
 *   ruleset's 10 "SS ..." buildings each carry a distinct index 0-9, matching the General Settings
 *   tab's "Spaceship Parts" group's part dropdown/count.
 * @param requiredResources This building's required natural resources. See
 *   [BldgRequiredResources].
 * @param flags 16 bytes, four packed 4-byte named sub-fields per existing reverse-engineering
 *   documentation of the BIX/BIQ format: [BldgEntry.improvements] (bytes 0-3), [BldgEntry.otherCharacteristics]
 *   (bytes 4-7), [BldgEntry.smallWonders] (bytes 8-11), [BldgEntry.wonders] (bytes 12-15) — see
 *   each sub-field property and its sibling named-bit accessors in `BldgEntryFlags.kt`.
 * @param flavors Bitmask membership in the `FLAV` section's 7 flavor slots: bit *n* means this
 *   building belongs to Flavor(*n*+1). See `TechEntry.flavors`'s own KDoc for the identical
 *   scheme on advances and civilizations.
 * @param unknown 4 bytes with zero documented behavior;
 *   preserved raw, not validated.
 * @param unitsProduced The unit this building produces each turn, if any — absent (`null`) from
 *   real VANILLA/PTW files. See [BldgUnitsProduced].
 */
data class BldgEntry(
    val description: String,
    val name: String,
    val civilopediaEntry: String,
    val doublesHappiness: Int,
    val gainInEveryCity: Int,
    val gainInEveryCityOnContinent: Int,
    val requirements: BldgRequirements,
    val cost: Int,
    val culture: Int,
    val combatValues: BldgCombatValues,
    val navalDefenseBonus: Int,
    val maintenanceCost: Int,
    val happiness: BldgHappiness,
    val numberOfRequiredBuildings: Int,
    val pollution: Int,
    val production: Int,
    val spaceshipPart: Int,
    val renderedObsoleteBy: Int,
    val requiredResources: BldgRequiredResources,
    val flags: ByteString,
    val numberOfArmiesRequired: Int,
    val flavors: Int,
    val unknown: ByteString,
    val unitsProduced: BldgUnitsProduced?,
) {
    init {
        require(flags.size == 16) { "BldgEntry.flags must be exactly 16 bytes, was ${flags.size}" }
        require(unknown.size == 4) { "BldgEntry.unknown must be exactly 4 bytes, was ${unknown.size}" }
    }
}
