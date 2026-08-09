package com.kelvsyc.rifflet.civ3.domain

/**
 * A placed unit instance, mutable — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.UnitEntry]. Named `PlacedUnit` rather than `Unit`, since `Unit` is
 * Kotlin's own built-in type.
 *
 * @param x This unit's map X coordinate.
 * @param y This unit's map Y coordinate.
 * @param legacyName This unit's [com.kelvsyc.rifflet.civ3.Civ3FormatEra.VANILLA]-era name field.
 *   Kept alongside [ptwName] rather than collapsed into one resolved field, pending real Civ3
 *   install/editor confirmation of how the two interact — see [name] for a read-only resolved
 *   view.
 * @param ptwName This unit's [com.kelvsyc.rifflet.civ3.Civ3FormatEra.PTW]/
 *   [com.kelvsyc.rifflet.civ3.Civ3FormatEra.CONQUESTS]-era name field.
 * @param owner This unit's owner. See [Owner]. Unlike [City]/[StartingLocation], [Owner.Barbarian]
 *   is a legitimate value here.
 * @param unitType This unit's prototype/type.
 * @param experienceLevel This unit's combat experience level.
 * @param aiStrategy This unit's currently-selected AI Strategy, or `null` for the real
 *   Rules/Scenario editor's "Random" option (only offered when [unitType] has 2+ `aiStrategies`
 *   bits set).
 * @param useCivilizationKing Whether this unit uses its owning civilization's specific King
 *   representation rather than a generic one — relevant only when [unitType] is configured as a
 *   civilization's King unit. Never observed set in any real file checked; behavior when a
 *   civilization has zero or multiple King-unit prototypes is unconfirmed.
 */
data class PlacedUnit(
    var x: Int,
    var y: Int,
    var legacyName: String = "",
    var ptwName: String = "",
    var owner: Owner = Owner.None,
    var unitType: Prto? = null,
    var experienceLevel: ExperienceLevel? = null,
    var aiStrategy: AiStrategy? = null,
    var useCivilizationKing: Boolean = false,
) {
    /** Resolved display name: prefers [ptwName] when non-blank, falling back to [legacyName].
     * Mirrors [com.kelvsyc.rifflet.civ3.UnitEntry.name]'s own resolution logic. */
    val name: String get() = ptwName.ifBlank { legacyName }
}
