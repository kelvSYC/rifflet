package com.kelvsyc.rifflet.civ3

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.extensionBitFlag
import com.kelvsyc.kotlin.core.traits.integral.int

/**
 * Named accessors for [RaceEntry.bonuses]'s 8 documented bits (civilization traits) — the first
 * 6 confirmed by the original vanilla-era Apolyton documentation, [agricultural] and [seaFaring]
 * added in a Conquests-era correction to the same thread.
 */
val RaceEntry.militaristic: Boolean by BitCollection.int.extensionBitFlag({ bonuses }, 0)
val RaceEntry.commercial: Boolean by BitCollection.int.extensionBitFlag({ bonuses }, 1)
val RaceEntry.expansionist: Boolean by BitCollection.int.extensionBitFlag({ bonuses }, 2)
val RaceEntry.scientific: Boolean by BitCollection.int.extensionBitFlag({ bonuses }, 3)
val RaceEntry.religious: Boolean by BitCollection.int.extensionBitFlag({ bonuses }, 4)
val RaceEntry.industrious: Boolean by BitCollection.int.extensionBitFlag({ bonuses }, 5)
val RaceEntry.agricultural: Boolean by BitCollection.int.extensionBitFlag({ bonuses }, 6)
val RaceEntry.seaFaring: Boolean by BitCollection.int.extensionBitFlag({ bonuses }, 7)

/**
 * Named accessors for [RaceEntry.governorSettings]'s 7 documented bits, per Apolyton's
 * "Civilization III BIC file format (2nd thread)".
 */
val RaceEntry.manageCitizens: Boolean by BitCollection.int.extensionBitFlag({ governorSettings }, 0)
val RaceEntry.emphasizeFood: Boolean by BitCollection.int.extensionBitFlag({ governorSettings }, 1)
val RaceEntry.emphasizeShields: Boolean by BitCollection.int.extensionBitFlag({ governorSettings }, 2)
val RaceEntry.emphasizeTrade: Boolean by BitCollection.int.extensionBitFlag({ governorSettings }, 3)
val RaceEntry.manageProduction: Boolean by BitCollection.int.extensionBitFlag({ governorSettings }, 4)
val RaceEntry.noWonders: Boolean by BitCollection.int.extensionBitFlag({ governorSettings }, 5)
val RaceEntry.noSmallWonders: Boolean by BitCollection.int.extensionBitFlag({ governorSettings }, 6)

/**
 * Named accessors for [RaceEntry.buildNever]'s 15 documented bits, per Apolyton's "Civilization
 * III BIC file format (2nd thread)". Prefixed with `buildNever` because [RaceEntry.buildOften]
 * documents an identical bit layout on the same receiver.
 */
val RaceEntry.buildNeverOffensiveLandUnits: Boolean by BitCollection.int.extensionBitFlag({ buildNever }, 0)
val RaceEntry.buildNeverDefensiveLandUnits: Boolean by BitCollection.int.extensionBitFlag({ buildNever }, 1)
val RaceEntry.buildNeverArtilleryLandUnits: Boolean by BitCollection.int.extensionBitFlag({ buildNever }, 2)
val RaceEntry.buildNeverSettlers: Boolean by BitCollection.int.extensionBitFlag({ buildNever }, 3)
val RaceEntry.buildNeverWorkers: Boolean by BitCollection.int.extensionBitFlag({ buildNever }, 4)
val RaceEntry.buildNeverNavalUnits: Boolean by BitCollection.int.extensionBitFlag({ buildNever }, 5)
val RaceEntry.buildNeverAirUnits: Boolean by BitCollection.int.extensionBitFlag({ buildNever }, 6)
val RaceEntry.buildNeverGrowth: Boolean by BitCollection.int.extensionBitFlag({ buildNever }, 7)
val RaceEntry.buildNeverProduction: Boolean by BitCollection.int.extensionBitFlag({ buildNever }, 8)
val RaceEntry.buildNeverHappiness: Boolean by BitCollection.int.extensionBitFlag({ buildNever }, 9)
val RaceEntry.buildNeverScience: Boolean by BitCollection.int.extensionBitFlag({ buildNever }, 10)
val RaceEntry.buildNeverWealth: Boolean by BitCollection.int.extensionBitFlag({ buildNever }, 11)
val RaceEntry.buildNeverTrade: Boolean by BitCollection.int.extensionBitFlag({ buildNever }, 12)
val RaceEntry.buildNeverExplore: Boolean by BitCollection.int.extensionBitFlag({ buildNever }, 13)
val RaceEntry.buildNeverCulture: Boolean by BitCollection.int.extensionBitFlag({ buildNever }, 14)

/**
 * Named accessors for [RaceEntry.buildOften]'s 15 documented bits — Apolyton documents this
 * field as "same as [buildNever] above". Prefixed with `buildOften` for the same collision
 * reason as [RaceEntry.buildNeverOffensiveLandUnits] and its siblings.
 */
val RaceEntry.buildOftenOffensiveLandUnits: Boolean by BitCollection.int.extensionBitFlag({ buildOften }, 0)
val RaceEntry.buildOftenDefensiveLandUnits: Boolean by BitCollection.int.extensionBitFlag({ buildOften }, 1)
val RaceEntry.buildOftenArtilleryLandUnits: Boolean by BitCollection.int.extensionBitFlag({ buildOften }, 2)
val RaceEntry.buildOftenSettlers: Boolean by BitCollection.int.extensionBitFlag({ buildOften }, 3)
val RaceEntry.buildOftenWorkers: Boolean by BitCollection.int.extensionBitFlag({ buildOften }, 4)
val RaceEntry.buildOftenNavalUnits: Boolean by BitCollection.int.extensionBitFlag({ buildOften }, 5)
val RaceEntry.buildOftenAirUnits: Boolean by BitCollection.int.extensionBitFlag({ buildOften }, 6)
val RaceEntry.buildOftenGrowth: Boolean by BitCollection.int.extensionBitFlag({ buildOften }, 7)
val RaceEntry.buildOftenProduction: Boolean by BitCollection.int.extensionBitFlag({ buildOften }, 8)
val RaceEntry.buildOftenHappiness: Boolean by BitCollection.int.extensionBitFlag({ buildOften }, 9)
val RaceEntry.buildOftenScience: Boolean by BitCollection.int.extensionBitFlag({ buildOften }, 10)
val RaceEntry.buildOftenWealth: Boolean by BitCollection.int.extensionBitFlag({ buildOften }, 11)
val RaceEntry.buildOftenTrade: Boolean by BitCollection.int.extensionBitFlag({ buildOften }, 12)
val RaceEntry.buildOftenExplore: Boolean by BitCollection.int.extensionBitFlag({ buildOften }, 13)
val RaceEntry.buildOftenCulture: Boolean by BitCollection.int.extensionBitFlag({ buildOften }, 14)
