package com.kelvsyc.rifflet.civ3

/**
 * One entry of `GOVT`'s embedded government-relationship array: how one government type
 * relates to another (bribery and resistance modifiers).
 *
 * @param canBribe Int-shaped boolean: 0 = no, 1 = yes.
 */
data class GovtRelationship(
    val canBribe: Int,
    val briberyModifier: Int,
    val resistanceModifier: Int,
)
