package com.kelvsyc.rifflet.civ3

/**
 * A terrain type's Conquests-only landmark override — what a tile of this terrain type yields
 * and looks like once its landmark is placed.
 *
 * Corresponds to the Conquests Rules Editor's `Terrain` tab's "Landmark Information" groupbox, in
 * its entirety. Present ([TerrEntry.landmark] non-`null`) only in [Civ3FormatEra.CONQUESTS] files.
 *
 * @param landmarkEnabled Whether this terrain type has a landmark override at all — the "on/off"
 *   state backing the rest of this group's fields.
 * @param tileValues The nested "Tile Values" sub-box. See [TerrTileValues].
 * @param terraformBonuses The nested "Terraform Bonuses" sub-box. See [TerrTerraformBonuses].
 * @param landmarkMovementBonus The "Movement" field.
 * @param landmarkDefensiveBonus The "Defense Bonus" field.
 * @param landmarkName The "Name of Landmark" field.
 * @param landmarkCivilopediaEntry The landmark's own Civilopedia entry, distinct from
 *   [TerrEntry.civilopediaEntry].
 */
data class TerrLandmark(
    val landmarkEnabled: Byte,
    val tileValues: TerrTileValues,
    val terraformBonuses: TerrTerraformBonuses,
    val landmarkMovementBonus: Int,
    val landmarkDefensiveBonus: Int,
    val landmarkName: String,
    val landmarkCivilopediaEntry: String,
)
