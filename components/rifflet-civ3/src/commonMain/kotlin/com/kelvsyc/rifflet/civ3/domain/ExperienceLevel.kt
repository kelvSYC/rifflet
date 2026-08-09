package com.kelvsyc.rifflet.civ3.domain

/**
 * A combat experience level, mutable — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.ExprEntry].
 *
 * @param name This experience level's name, per the Rules Editor's Experience Levels tab.
 * @param baseHitPoints This experience level's hit-point bonus.
 * @param retreatBonus This experience level's retreat chance.
 */
data class ExperienceLevel(
    var name: String,
    var baseHitPoints: Int = 0,
    var retreatBonus: Int = 0,
)
