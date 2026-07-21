package com.kelvsyc.rifflet.civ3

/**
 * Resolves each id in [GameEntry.playableCivIds] against [races], preserving position: the
 * result is the same length as [GameEntry.playableCivIds], with `null` at any position whose id
 * doesn't resolve.
 */
fun GameEntry.playableCivIdsRace(races: List<RaceEntry>): List<RaceEntry?> =
    playableCivIds.map { races.getOrNull(it) }
