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

/**
 * Named accessors for [RaceEntry.buildNever]'s 15 documented bits, per Apolyton's "Civilization
 * III BIC file format (2nd thread)". Prefixed with `buildNever` because [RaceEntry.buildOften]
 * documents an identical bit layout on the same receiver.
 */
val RaceEntry.buildNeverOffensiveLandUnits: Boolean get() = buildNever and (1 shl 0) != 0
val RaceEntry.buildNeverDefensiveLandUnits: Boolean get() = buildNever and (1 shl 1) != 0
val RaceEntry.buildNeverArtilleryLandUnits: Boolean get() = buildNever and (1 shl 2) != 0
val RaceEntry.buildNeverSettlers: Boolean get() = buildNever and (1 shl 3) != 0
val RaceEntry.buildNeverWorkers: Boolean get() = buildNever and (1 shl 4) != 0
val RaceEntry.buildNeverNavalUnits: Boolean get() = buildNever and (1 shl 5) != 0
val RaceEntry.buildNeverAirUnits: Boolean get() = buildNever and (1 shl 6) != 0
val RaceEntry.buildNeverGrowth: Boolean get() = buildNever and (1 shl 7) != 0
val RaceEntry.buildNeverProduction: Boolean get() = buildNever and (1 shl 8) != 0
val RaceEntry.buildNeverHappiness: Boolean get() = buildNever and (1 shl 9) != 0
val RaceEntry.buildNeverScience: Boolean get() = buildNever and (1 shl 10) != 0
val RaceEntry.buildNeverWealth: Boolean get() = buildNever and (1 shl 11) != 0
val RaceEntry.buildNeverTrade: Boolean get() = buildNever and (1 shl 12) != 0
val RaceEntry.buildNeverExplore: Boolean get() = buildNever and (1 shl 13) != 0
val RaceEntry.buildNeverCulture: Boolean get() = buildNever and (1 shl 14) != 0

/**
 * Named accessors for [RaceEntry.buildOften]'s 15 documented bits — Apolyton documents this
 * field as "same as [buildNever] above". Prefixed with `buildOften` for the same collision
 * reason as [RaceEntry.buildNeverOffensiveLandUnits] and its siblings.
 */
val RaceEntry.buildOftenOffensiveLandUnits: Boolean get() = buildOften and (1 shl 0) != 0
val RaceEntry.buildOftenDefensiveLandUnits: Boolean get() = buildOften and (1 shl 1) != 0
val RaceEntry.buildOftenArtilleryLandUnits: Boolean get() = buildOften and (1 shl 2) != 0
val RaceEntry.buildOftenSettlers: Boolean get() = buildOften and (1 shl 3) != 0
val RaceEntry.buildOftenWorkers: Boolean get() = buildOften and (1 shl 4) != 0
val RaceEntry.buildOftenNavalUnits: Boolean get() = buildOften and (1 shl 5) != 0
val RaceEntry.buildOftenAirUnits: Boolean get() = buildOften and (1 shl 6) != 0
val RaceEntry.buildOftenGrowth: Boolean get() = buildOften and (1 shl 7) != 0
val RaceEntry.buildOftenProduction: Boolean get() = buildOften and (1 shl 8) != 0
val RaceEntry.buildOftenHappiness: Boolean get() = buildOften and (1 shl 9) != 0
val RaceEntry.buildOftenScience: Boolean get() = buildOften and (1 shl 10) != 0
val RaceEntry.buildOftenWealth: Boolean get() = buildOften and (1 shl 11) != 0
val RaceEntry.buildOftenTrade: Boolean get() = buildOften and (1 shl 12) != 0
val RaceEntry.buildOftenExplore: Boolean get() = buildOften and (1 shl 13) != 0
val RaceEntry.buildOftenCulture: Boolean get() = buildOften and (1 shl 14) != 0
