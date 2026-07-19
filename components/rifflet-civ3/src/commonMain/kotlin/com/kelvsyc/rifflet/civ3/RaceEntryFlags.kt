package com.kelvsyc.rifflet.civ3

/**
 * Named accessors for [RaceEntry.bonuses]'s 8 documented bits (civilization traits) — the first
 * 6 confirmed by the original vanilla-era Apolyton documentation, [agricultural] and [seaFaring]
 * added in a Conquests-era correction to the same thread.
 */
val RaceEntry.militaristic: Boolean get() = bonuses and (1 shl 0) != 0
val RaceEntry.commercial: Boolean get() = bonuses and (1 shl 1) != 0
val RaceEntry.expansionist: Boolean get() = bonuses and (1 shl 2) != 0
val RaceEntry.scientific: Boolean get() = bonuses and (1 shl 3) != 0
val RaceEntry.religious: Boolean get() = bonuses and (1 shl 4) != 0
val RaceEntry.industrious: Boolean get() = bonuses and (1 shl 5) != 0
val RaceEntry.agricultural: Boolean get() = bonuses and (1 shl 6) != 0
val RaceEntry.seaFaring: Boolean get() = bonuses and (1 shl 7) != 0

/**
 * Named accessors for [RaceEntry.governorSettings]'s 7 documented bits, per Apolyton's
 * "Civilization III BIC file format (2nd thread)".
 */
val RaceEntry.manageCitizens: Boolean get() = governorSettings and (1 shl 0) != 0
val RaceEntry.emphasizeFood: Boolean get() = governorSettings and (1 shl 1) != 0
val RaceEntry.emphasizeShields: Boolean get() = governorSettings and (1 shl 2) != 0
val RaceEntry.emphasizeTrade: Boolean get() = governorSettings and (1 shl 3) != 0
val RaceEntry.manageProduction: Boolean get() = governorSettings and (1 shl 4) != 0
val RaceEntry.noWonders: Boolean get() = governorSettings and (1 shl 5) != 0
val RaceEntry.noSmallWonders: Boolean get() = governorSettings and (1 shl 6) != 0
