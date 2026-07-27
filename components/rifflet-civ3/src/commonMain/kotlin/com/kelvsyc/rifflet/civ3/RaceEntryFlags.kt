package com.kelvsyc.rifflet.civ3

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.extensionBitFlag
import com.kelvsyc.kotlin.core.traits.integral.int

/**
 * Named accessors for [RaceEntry.bonuses]'s 8 documented bits (civilization traits) — the first
 * 6 documented by the original vanilla-era reverse-engineering documentation, [agricultural] and
 * [seaFaring] added in a Conquests-era correction to that same documentation.
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
 * Named accessors for [RaceEntry.flavors]'s 7 documented bits (see that field's own KDoc for
 * how they were confirmed) — not to be confused with [RaceEntry.bonuses]'s similarly-labeled
 * "Flavor1".."Flavor7" checkboxes in the Conquests Rules Editor, which turned out to be a
 * red herring (see [bonuses]'s own KDoc).
 */
val RaceEntry.flavor1: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 0)
val RaceEntry.flavor2: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 1)
val RaceEntry.flavor3: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 2)
val RaceEntry.flavor4: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 3)
val RaceEntry.flavor5: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 4)
val RaceEntry.flavor6: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 5)
val RaceEntry.flavor7: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 6)

/**
 * Named accessors for [RaceGovernor.settings]'s 7 documented bits, per earlier
 * reverse-engineering documentation of the BIC format.
 */
val RaceEntry.manageCitizens: Boolean by BitCollection.int.extensionBitFlag({ governor.settings }, 0)
val RaceEntry.emphasizeFood: Boolean by BitCollection.int.extensionBitFlag({ governor.settings }, 1)
val RaceEntry.emphasizeShields: Boolean by BitCollection.int.extensionBitFlag({ governor.settings }, 2)
val RaceEntry.emphasizeTrade: Boolean by BitCollection.int.extensionBitFlag({ governor.settings }, 3)
val RaceEntry.manageProduction: Boolean by BitCollection.int.extensionBitFlag({ governor.settings }, 4)
val RaceEntry.noWonders: Boolean by BitCollection.int.extensionBitFlag({ governor.settings }, 5)
val RaceEntry.noSmallWonders: Boolean by BitCollection.int.extensionBitFlag({ governor.settings }, 6)

/**
 * Named accessors for [RaceGovernor.buildNever]'s 15 documented bits, per earlier
 * reverse-engineering documentation of the BIC format. Prefixed with `buildNever` because
 * [RaceGovernor.buildOften] documents an identical bit layout on the same receiver.
 */
val RaceEntry.buildNeverOffensiveLandUnits: Boolean by BitCollection.int.extensionBitFlag({ governor.buildNever }, 0)
val RaceEntry.buildNeverDefensiveLandUnits: Boolean by BitCollection.int.extensionBitFlag({ governor.buildNever }, 1)
val RaceEntry.buildNeverArtilleryLandUnits: Boolean by BitCollection.int.extensionBitFlag({ governor.buildNever }, 2)
val RaceEntry.buildNeverSettlers: Boolean by BitCollection.int.extensionBitFlag({ governor.buildNever }, 3)
val RaceEntry.buildNeverWorkers: Boolean by BitCollection.int.extensionBitFlag({ governor.buildNever }, 4)
val RaceEntry.buildNeverNavalUnits: Boolean by BitCollection.int.extensionBitFlag({ governor.buildNever }, 5)
val RaceEntry.buildNeverAirUnits: Boolean by BitCollection.int.extensionBitFlag({ governor.buildNever }, 6)
val RaceEntry.buildNeverGrowth: Boolean by BitCollection.int.extensionBitFlag({ governor.buildNever }, 7)
val RaceEntry.buildNeverProduction: Boolean by BitCollection.int.extensionBitFlag({ governor.buildNever }, 8)
val RaceEntry.buildNeverHappiness: Boolean by BitCollection.int.extensionBitFlag({ governor.buildNever }, 9)
val RaceEntry.buildNeverScience: Boolean by BitCollection.int.extensionBitFlag({ governor.buildNever }, 10)
val RaceEntry.buildNeverWealth: Boolean by BitCollection.int.extensionBitFlag({ governor.buildNever }, 11)
val RaceEntry.buildNeverTrade: Boolean by BitCollection.int.extensionBitFlag({ governor.buildNever }, 12)
val RaceEntry.buildNeverExplore: Boolean by BitCollection.int.extensionBitFlag({ governor.buildNever }, 13)
val RaceEntry.buildNeverCulture: Boolean by BitCollection.int.extensionBitFlag({ governor.buildNever }, 14)

/**
 * Named accessors for [RaceGovernor.buildOften]'s 15 documented bits — existing reverse-engineering
 * documentation describes this field as "same as [RaceGovernor.buildNever] above". Prefixed with
 * `buildOften` for the same collision reason as [RaceEntry.buildNeverOffensiveLandUnits] and its
 * siblings.
 */
val RaceEntry.buildOftenOffensiveLandUnits: Boolean by BitCollection.int.extensionBitFlag({ governor.buildOften }, 0)
val RaceEntry.buildOftenDefensiveLandUnits: Boolean by BitCollection.int.extensionBitFlag({ governor.buildOften }, 1)
val RaceEntry.buildOftenArtilleryLandUnits: Boolean by BitCollection.int.extensionBitFlag({ governor.buildOften }, 2)
val RaceEntry.buildOftenSettlers: Boolean by BitCollection.int.extensionBitFlag({ governor.buildOften }, 3)
val RaceEntry.buildOftenWorkers: Boolean by BitCollection.int.extensionBitFlag({ governor.buildOften }, 4)
val RaceEntry.buildOftenNavalUnits: Boolean by BitCollection.int.extensionBitFlag({ governor.buildOften }, 5)
val RaceEntry.buildOftenAirUnits: Boolean by BitCollection.int.extensionBitFlag({ governor.buildOften }, 6)
val RaceEntry.buildOftenGrowth: Boolean by BitCollection.int.extensionBitFlag({ governor.buildOften }, 7)
val RaceEntry.buildOftenProduction: Boolean by BitCollection.int.extensionBitFlag({ governor.buildOften }, 8)
val RaceEntry.buildOftenHappiness: Boolean by BitCollection.int.extensionBitFlag({ governor.buildOften }, 9)
val RaceEntry.buildOftenScience: Boolean by BitCollection.int.extensionBitFlag({ governor.buildOften }, 10)
val RaceEntry.buildOftenWealth: Boolean by BitCollection.int.extensionBitFlag({ governor.buildOften }, 11)
val RaceEntry.buildOftenTrade: Boolean by BitCollection.int.extensionBitFlag({ governor.buildOften }, 12)
val RaceEntry.buildOftenExplore: Boolean by BitCollection.int.extensionBitFlag({ governor.buildOften }, 13)
val RaceEntry.buildOftenCulture: Boolean by BitCollection.int.extensionBitFlag({ governor.buildOften }, 14)
