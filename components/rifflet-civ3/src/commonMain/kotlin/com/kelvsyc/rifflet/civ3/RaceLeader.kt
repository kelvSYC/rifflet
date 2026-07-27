package com.kelvsyc.rifflet.civ3

/**
 * A civilization's leader identity.
 *
 * Corresponds to the Conquests Rules Editor's `Civilizations` tab's "Leader" groupbox, in its
 * entirety.
 *
 * @param name The leader's own name (e.g. "Caesar Augustus") — distinct from [RaceEntry.name],
 *   the civilization's own name (e.g. "Rome").
 * @param title The leader's title (e.g. "Emperor").
 * @param gender The "Gender" radio group (Male/Female) — distinct from
 *   [RaceEntry.civilizationGender], the civilization's own grammatical gender.
 */
data class RaceLeader(
    val name: String,
    val title: String,
    val gender: Int,
)
