package com.kelvsyc.rifflet.civ3

/**
 * The resolved meaning of [TerrEntry.pollutionEffect].
 */
sealed interface TerrPollutionEffect {
    /** `pollutionEffect == -1`: this terrain type has no pollution effect. */
    data object None : TerrPollutionEffect

    /**
     * `pollutionEffect` equals the `TERR` section's own entry count: this terrain type reverts
     * to its own base terrain when polluted, per the Terrain editor tab's "Base Terrain Type"
     * option — used by overlay terrain types like Forest/Jungle rather than a fixed other
     * terrain type.
     */
    data object BaseTerrainType : TerrPollutionEffect

    /**
     * A specific `TERR` section index this terrain type becomes when polluted. [terrain] is
     * `null` when [TerrEntry.pollutionEffect] doesn't resolve against the supplied `TERR`
     * entries.
     */
    data class Terrain(val terrain: TerrEntry?) : TerrPollutionEffect
}

/**
 * Resolves [TerrEntry.pollutionEffect] against [terrains] (the same `TERR` section this entry
 * came from) — see [TerrPollutionEffect] for what each case means.
 */
fun TerrEntry.pollutionEffectResolved(terrains: List<TerrEntry>): TerrPollutionEffect = when (pollutionEffect) {
    -1 -> TerrPollutionEffect.None
    terrains.size -> TerrPollutionEffect.BaseTerrainType
    else -> TerrPollutionEffect.Terrain(terrains.getOrNull(pollutionEffect))
}

/**
 * Resolves [TerrEntry.workerJobAllowed] against [jobs] (the same `TFRM` section this entry
 * references).
 */
fun TerrEntry.workerJobAllowedTfrm(jobs: List<TfrmEntry>): TfrmEntry? = jobs.getOrNull(workerJobAllowed)
