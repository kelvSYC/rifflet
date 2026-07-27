package com.kelvsyc.rifflet.civ3

/**
 * The scenario's terrain and city defense bonuses.
 *
 * Corresponds to the Conquests Rules Editor's `General Settings` tab's "Defensive Bonuses"
 * groupbox, in its entirety.
 */
data class RuleDefensiveBonuses(
    val fortress: Int,
    val building: Int,
    val citizen: Int,
    val river: Int,
    val town: Int,
    val city: Int,
    val metropolis: Int,
    val fortifications: Int,
)
