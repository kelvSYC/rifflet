package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `EXPR` section: a combat experience level's hit-point bonus and retreat
 * chance.
 */
data class ExprEntry(
    val name: String,
    val baseHitPoints: Int,
    val retreatBonus: Int,
)
