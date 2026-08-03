package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.mutableExtensionBitFlag

/**
 * Settable counterparts to [com.kelvsyc.rifflet.civ3.militaristic] and its 7 sibling trait
 * accessors — see that file's KDoc for what each bit means.
 */
var Race.militaristic: Boolean by BitCollection.int.mutableExtensionBitFlag({ bonuses }, { bonuses = it }, 0)
var Race.commercial: Boolean by BitCollection.int.mutableExtensionBitFlag({ bonuses }, { bonuses = it }, 1)
var Race.expansionist: Boolean by BitCollection.int.mutableExtensionBitFlag({ bonuses }, { bonuses = it }, 2)
var Race.scientific: Boolean by BitCollection.int.mutableExtensionBitFlag({ bonuses }, { bonuses = it }, 3)
var Race.religious: Boolean by BitCollection.int.mutableExtensionBitFlag({ bonuses }, { bonuses = it }, 4)
var Race.industrious: Boolean by BitCollection.int.mutableExtensionBitFlag({ bonuses }, { bonuses = it }, 5)
var Race.agricultural: Boolean by BitCollection.int.mutableExtensionBitFlag({ bonuses }, { bonuses = it }, 6)
var Race.seaFaring: Boolean by BitCollection.int.mutableExtensionBitFlag({ bonuses }, { bonuses = it }, 7)

/**
 * Settable counterparts to [com.kelvsyc.rifflet.civ3.flavor1] and its 6 siblings.
 */
var Race.flavor1: Boolean by BitCollection.int.mutableExtensionBitFlag({ flavors }, { flavors = it }, 0)
var Race.flavor2: Boolean by BitCollection.int.mutableExtensionBitFlag({ flavors }, { flavors = it }, 1)
var Race.flavor3: Boolean by BitCollection.int.mutableExtensionBitFlag({ flavors }, { flavors = it }, 2)
var Race.flavor4: Boolean by BitCollection.int.mutableExtensionBitFlag({ flavors }, { flavors = it }, 3)
var Race.flavor5: Boolean by BitCollection.int.mutableExtensionBitFlag({ flavors }, { flavors = it }, 4)
var Race.flavor6: Boolean by BitCollection.int.mutableExtensionBitFlag({ flavors }, { flavors = it }, 5)
var Race.flavor7: Boolean by BitCollection.int.mutableExtensionBitFlag({ flavors }, { flavors = it }, 6)

/**
 * Settable counterparts to [com.kelvsyc.rifflet.civ3.manageCitizens] and its 6 siblings.
 */
var Race.manageCitizens: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.settings }, { governor.settings = it }, 0)
var Race.emphasizeFood: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.settings }, { governor.settings = it }, 1)
var Race.emphasizeShields: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.settings }, { governor.settings = it }, 2)
var Race.emphasizeTrade: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.settings }, { governor.settings = it }, 3)
var Race.manageProduction: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.settings }, { governor.settings = it }, 4)
var Race.noWonders: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.settings }, { governor.settings = it }, 5)
var Race.noSmallWonders: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.settings }, { governor.settings = it }, 6)

/**
 * Settable counterparts to [com.kelvsyc.rifflet.civ3.buildNeverOffensiveLandUnits] and its 14
 * siblings. Prefixed with `buildNever` for the same collision reason as the wire-layer accessors.
 */
var Race.buildNeverOffensiveLandUnits: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildNever }, { governor.buildNever = it }, 0)
var Race.buildNeverDefensiveLandUnits: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildNever }, { governor.buildNever = it }, 1)
var Race.buildNeverArtilleryLandUnits: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildNever }, { governor.buildNever = it }, 2)
var Race.buildNeverSettlers: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildNever }, { governor.buildNever = it }, 3)
var Race.buildNeverWorkers: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildNever }, { governor.buildNever = it }, 4)
var Race.buildNeverNavalUnits: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildNever }, { governor.buildNever = it }, 5)
var Race.buildNeverAirUnits: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildNever }, { governor.buildNever = it }, 6)
var Race.buildNeverGrowth: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildNever }, { governor.buildNever = it }, 7)
var Race.buildNeverProduction: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildNever }, { governor.buildNever = it }, 8)
var Race.buildNeverHappiness: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildNever }, { governor.buildNever = it }, 9)
var Race.buildNeverScience: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildNever }, { governor.buildNever = it }, 10)
var Race.buildNeverWealth: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildNever }, { governor.buildNever = it }, 11)
var Race.buildNeverTrade: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildNever }, { governor.buildNever = it }, 12)
var Race.buildNeverExplore: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildNever }, { governor.buildNever = it }, 13)
var Race.buildNeverCulture: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildNever }, { governor.buildNever = it }, 14)

/**
 * Settable counterparts to [com.kelvsyc.rifflet.civ3.buildOftenOffensiveLandUnits] and its 14
 * siblings. Prefixed with `buildOften` for the same collision reason as the wire-layer accessors.
 */
var Race.buildOftenOffensiveLandUnits: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildOften }, { governor.buildOften = it }, 0)
var Race.buildOftenDefensiveLandUnits: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildOften }, { governor.buildOften = it }, 1)
var Race.buildOftenArtilleryLandUnits: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildOften }, { governor.buildOften = it }, 2)
var Race.buildOftenSettlers: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildOften }, { governor.buildOften = it }, 3)
var Race.buildOftenWorkers: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildOften }, { governor.buildOften = it }, 4)
var Race.buildOftenNavalUnits: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildOften }, { governor.buildOften = it }, 5)
var Race.buildOftenAirUnits: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildOften }, { governor.buildOften = it }, 6)
var Race.buildOftenGrowth: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildOften }, { governor.buildOften = it }, 7)
var Race.buildOftenProduction: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildOften }, { governor.buildOften = it }, 8)
var Race.buildOftenHappiness: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildOften }, { governor.buildOften = it }, 9)
var Race.buildOftenScience: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildOften }, { governor.buildOften = it }, 10)
var Race.buildOftenWealth: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildOften }, { governor.buildOften = it }, 11)
var Race.buildOftenTrade: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildOften }, { governor.buildOften = it }, 12)
var Race.buildOftenExplore: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildOften }, { governor.buildOften = it }, 13)
var Race.buildOftenCulture: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ governor.buildOften }, { governor.buildOften = it }, 14)
