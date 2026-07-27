package com.kelvsyc.rifflet.civ3

/**
 * A civilization's default diplomatic and AI-behavior personality.
 *
 * Corresponds to the Conquests Rules Editor's `Civilizations` tab's "Personality" groupbox, in
 * its entirety.
 *
 * @param favoriteGovernment A `GOVT` section index, per the "Favorite" dropdown.
 * @param shunnedGovernment A `GOVT` section index, per the "Shunned Government" dropdown.
 * @param aggressionLevel The "Aggression Level" slider (Less..More).
 */
data class RacePersonality(
    val favoriteGovernment: Int,
    val shunnedGovernment: Int,
    val aggressionLevel: Int,
)
