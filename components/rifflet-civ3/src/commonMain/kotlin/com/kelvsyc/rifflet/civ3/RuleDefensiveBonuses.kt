package com.kelvsyc.rifflet.civ3

/**
 * The scenario's terrain and city defense bonuses.
 *
 * Corresponds to the Conquests Rules Editor's `General Settings` tab's "Defensive Bonuses"
 * groupbox, in its entirety.
 *
 * @param fortressDefensiveBonus The "Fortress" field.
 * @param buildingDefensiveBonus The "Building" field.
 * @param citizenDefensiveBonus The "Citizen" field.
 * @param riverDefensiveBonus The "River" field.
 * @param townDefenseBonus The "Town" field.
 * @param cityDefenseBonus The "City" field.
 * @param metropolisDefenseBonus The "Metropolis" field.
 * @param fortificationsDefensiveBonus The "Fortifications" field.
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
