package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.TerrAllowances
import com.kelvsyc.rifflet.civ3.TerrLandmark
import com.kelvsyc.rifflet.civ3.TerrTerraformBonuses
import com.kelvsyc.rifflet.civ3.TerrTileValues
import com.kelvsyc.rifflet.civ3.TfrmEntry
import okio.ByteString

/**
 * A terrain type, mutable — the domain-layer counterpart to [com.kelvsyc.rifflet.civ3.TerrEntry].
 * Stored in a `Map<TerrainSlot, Terrain>` rather than a flat list: see `TerrainSlot`'s own KDoc for
 * why `TERR`'s fixed, named-identity shape gets this treatment instead of every other section's
 * `List<T>`.
 *
 * @param name This terrain type's name.
 * @param civilopediaEntry This terrain type's Civilopedia entry key.
 * @param possibleResources The resources that can appear on this terrain type.
 * @param terraformBonuses This terrain type's worker-job terraform bonuses. See
 *   [TerrTerraformBonuses].
 * @param defenseBonus This terrain type's defense bonus.
 * @param movementCost This terrain type's movement cost.
 * @param tileValues This terrain type's base Food/Shields/Commerce yield. See [TerrTileValues].
 * @param workerJobAllowed The worker job that transforms this terrain type, if any. References the
 *   wire `TfrmEntry` — `TFRM` doesn't have its own domain type yet.
 * @param pollutionEffect What this terrain type becomes when polluted. See [TerrPollutionEffect].
 * @param allowances This terrain type's city/improvement/movement allowances. See [TerrAllowances].
 * @param landmark This terrain type's Conquests-only landmark override, `null` pre-Conquests. See
 *   [TerrLandmark].
 * @param terrainFlags Mostly opaque, preserved raw — see
 *   [com.kelvsyc.rifflet.civ3.TerrEntry.terrainFlags]'s own KDoc for what's confirmed and what
 *   isn't. [causesDisease]/[curedBySanitation] are settable named views into 2 of its bits, not
 *   separate stored state — see `TerrainFlags.kt`.
 * @param diseaseStrength This terrain type's disease strength.
 * @param unknown 4 bytes with zero documented behavior; preserved raw, not validated. Same
 *   treatment as `Race.unknown`/`Tech.unknown`/`Government.unknown`.
 * @param unknown2 4 bytes with zero documented behavior; preserved raw, not validated.
 */
data class Terrain(
    var name: String,
    var civilopediaEntry: String = "",
    var possibleResources: MutableSet<Resource> = mutableSetOf(),
    var terraformBonuses: TerrTerraformBonuses = TerrTerraformBonuses(0, 0, 0),
    var defenseBonus: Int = 0,
    var movementCost: Int = 0,
    var tileValues: TerrTileValues = TerrTileValues(0, 0, 0),
    var workerJobAllowed: TfrmEntry? = null,
    var pollutionEffect: TerrPollutionEffect = TerrPollutionEffect.None,
    var allowances: TerrAllowances = TerrAllowances(0, 0, null, null, null, null, null, null),
    var landmark: TerrLandmark? = null,
    var terrainFlags: Int = 0,
    var diseaseStrength: Int = 0,
    var unknown: ByteString = ByteString.of(0, 0, 0, 0),
    var unknown2: ByteString = ByteString.of(0, 0, 0, 0),
)

/**
 * The resolved meaning of [Terrain.pollutionEffect] — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.TerrPollutionEffect].
 */
sealed interface TerrPollutionEffect {
    /** This terrain type has no pollution effect. */
    data object None : TerrPollutionEffect

    /**
     * This terrain type reverts to its own base terrain when polluted, per the Terrain editor
     * tab's "Base Terrain Type" option — used by overlay terrain types like Forest/Jungle rather
     * than a fixed other terrain type.
     */
    data object BaseTerrainType : TerrPollutionEffect

    /**
     * A specific terrain type this terrain type becomes when polluted. [terrain] is `null` when
     * the wire entry's `pollutionEffect` doesn't resolve against the supplied `TERR` entries.
     */
    data class SpecificTerrain(val terrain: Terrain?) : TerrPollutionEffect
}
