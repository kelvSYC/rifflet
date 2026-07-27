package com.kelvsyc.rifflet.civ3

/**
 * The scenario's terrain and city defense bonuses.
 *
 * Corresponds to the Conquests Rules Editor's `General Settings` tab's "Defensive Bonuses"
 * groupbox, in its entirety.
 */
data class RuleDefensiveBonuses(
    val fortressDefensiveBonus: Int,
    val buildingDefensiveBonus: Int,
    val citizenDefensiveBonus: Int,
    val riverDefensiveBonus: Int,
    val townDefenseBonus: Int,
    val cityDefenseBonus: Int,
    val metropolisDefenseBonus: Int,
    val fortificationsDefensiveBonus: Int,
)
