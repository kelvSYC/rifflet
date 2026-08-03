package com.kelvsyc.rifflet.civ3.domain

/**
 * A civilization's default Governor automation settings — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.RaceGovernor]. Each member is a bit-packed flag field, not decomposed
 * at the type level — see `RaceFlags.kt` for the individual named, settable accessors.
 *
 * @param settings The "Settings" sub-box's 7 checkboxes (Manage Citizens, Emphasize Food, ...).
 * @param buildNever The "Build Never" sub-box's 15 checkboxes.
 * @param buildOften The "Build Often" sub-box's 15 checkboxes, documenting an identical layout to
 *   [buildNever].
 */
data class RaceGovernor(
    var settings: Int = 0,
    var buildNever: Int = 0,
    var buildOften: Int = 0,
)
