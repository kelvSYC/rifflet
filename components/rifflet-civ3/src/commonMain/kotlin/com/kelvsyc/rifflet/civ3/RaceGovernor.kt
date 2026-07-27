package com.kelvsyc.rifflet.civ3

/**
 * A civilization's default Governor automation settings.
 *
 * Corresponds to the Conquests Rules Editor's `Civilizations` tab's "Governor" groupbox, in its
 * entirety. Each member is a bit-packed flag field, not decomposed at the type level — see
 * `RaceEntryFlags.kt` for the individual named accessors.
 *
 * @param settings The "Settings" sub-box's 7 checkboxes (Manage Citizens, Emphasize Food, ...).
 * @param buildNever The "Build Never" sub-box's 15 checkboxes.
 * @param buildOften The "Build Often" sub-box's 15 checkboxes, documenting an identical layout to
 *   [buildNever].
 */
data class RaceGovernor(
    val settings: Int,
    val buildNever: Int,
    val buildOften: Int,
)
