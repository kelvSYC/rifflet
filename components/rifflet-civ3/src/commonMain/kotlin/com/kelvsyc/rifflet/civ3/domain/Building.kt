package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.BldgCombatValues
import com.kelvsyc.rifflet.civ3.BldgHappiness
import okio.ByteString

/**
 * A building or wonder definition, mutable and cross-referenced by real object references — the
 * domain-layer counterpart to [com.kelvsyc.rifflet.civ3.BldgEntry].
 *
 * A sealed hierarchy rather than one flat type, mirroring the Conquests/PTW Rules Editor's own
 * Category radio group (Improvement / Wonder / Sm. Wonder), plus a fourth variant
 * ([SpaceshipPart]) for entries the editor locks a materially different field set for. This
 * interface declares the 14 fields every variant carries unconditionally; [StandardBuilding] and
 * [Wonder] add the fields shared by progressively narrower subsets of variants, so each field's
 * settable bit-flag accessors (`BldgFlags.kt`) are declared once against whichever interface
 * actually carries the backing field, not duplicated per leaf variant.
 *
 * A `data class` in every concrete implementation, like [com.kelvsyc.rifflet.civ3.domain.Tech]
 * and unlike the plain-class [Government]: self-referencing fields exist ([BldgRequirements.requiredBuilding]
 * on every variant; [GreatWonder]'s 3 effect fields), but `toDomain()` guarantees acyclicity by
 * construction, so there's no residual circular-reference risk to protect against.
 */
sealed interface Building {
    var description: String
    var name: String
    var civilopediaEntry: String
    var requirements: BldgRequirements
    var cost: Int
    var culture: Int
    var maintenanceCost: Int
    var pollution: Int
    var production: Int
    var requiredResources: BldgRequiredResources
    var improvements: Int
    var otherCharacteristics: Int
    var flavors: Int
    var unknown: ByteString
}

/**
 * A [Building] that isn't a [SpaceshipPart] — adds the fields the Rules Editor locks away
 * specifically for spaceship parts (combat values, happiness, unit production, etc.), confirmed
 * `ERROR`-level always-zero/absent for real spaceship parts.
 */
sealed interface StandardBuilding : Building {
    var combatValues: BldgCombatValues
    var navalDefenseBonus: Int
    var happiness: BldgHappiness
    var numberOfRequiredBuildings: Int
    var numberOfArmiesRequired: Int
    var renderedObsoleteBy: Tech?
    var unitsProduced: BldgUnitsProduced?
}

/**
 * A [StandardBuilding] with Category Wonder or Small Wonder — adds the shared "Wonders and Small
 * Wonders" checkbox grid ([wonders]/[smallWonders]), which the Rules Editor shows identically for
 * either category.
 */
sealed interface Wonder : StandardBuilding {
    var wonders: Int
    var smallWonders: Int
}
