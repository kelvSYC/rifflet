package com.kelvsyc.rifflet.civ3

/**
 * Resolves each id in [GameEntry.playableCivIds] against [races], preserving position: the
 * result is the same length as [GameEntry.playableCivIds], with `null` at any position whose id
 * doesn't resolve.
 */
fun GameEntry.playableCivIdsRace(races: List<RaceEntry>): List<RaceEntry?> =
    playableCivIds.map { races.getOrNull(it) }

/**
 * The 3 values of [GameTimeOptions.baseTimeUnit], per the Conquests Rules Editor's `Scenario
 * Properties` → `Scenario` tab → "Time Options" groupbox's "Base unit of time" dropdown.
 *
 * Ordinal position matches the raw file value directly (raw `0` is [YEARS], raw `1` is [MONTHS],
 * raw `2` is [WEEKS]) — do not reorder these constants.
 */
enum class GameBaseTimeUnit { YEARS, MONTHS, WEEKS }
