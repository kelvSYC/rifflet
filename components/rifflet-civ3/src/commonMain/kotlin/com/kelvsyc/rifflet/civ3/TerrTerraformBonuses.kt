package com.kelvsyc.rifflet.civ3

/**
 * A terrain type's worker-job terraform bonuses.
 *
 * Corresponds to the Conquests Rules Editor's `Terrain` tab's "Terraform Bonuses" groupbox, in
 * its entirety. Also used, unchanged, for [TerrLandmark]'s own nested "Terraform Bonuses" sub-box.
 *
 * @param irrigationBonus The "Irrigation" field.
 * @param miningBonus The "Mining" field (labeled "Mines(Shields)" in [TerrLandmark]'s own
 *   sub-box).
 * @param roadBonus The "Road" field.
 */
data class TerrTerraformBonuses(
    val irrigationBonus: Int,
    val miningBonus: Int,
    val roadBonus: Int,
)
