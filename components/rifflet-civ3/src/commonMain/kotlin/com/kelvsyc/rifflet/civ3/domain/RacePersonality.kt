package com.kelvsyc.rifflet.civ3.domain

/**
 * A civilization's default diplomatic and AI-behavior personality — the domain-layer counterpart
 * to [com.kelvsyc.rifflet.civ3.RacePersonality].
 *
 * @param favoriteGovernment This civilization's favorite government, per the "Favorite" dropdown.
 * @param shunnedGovernment This civilization's shunned government, per the "Shunned Government"
 *   dropdown.
 * @param aggressionLevel The "Aggression Level" slider (Less..More).
 */
data class RacePersonality(
    var favoriteGovernment: Government? = null,
    var shunnedGovernment: Government? = null,
    var aggressionLevel: Int = 0,
)
