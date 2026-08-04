package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.mutableExtensionBitFlag

/**
 * Settable counterparts to [com.kelvsyc.rifflet.civ3.enablesDiplomats] and its 22 siblings — see
 * that file's KDoc for what each bit means.
 */
var Tech.enablesDiplomats: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 0)
var Tech.enablesIrrigationWithoutFreshWater: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 1)
var Tech.enablesBridges: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 2)
var Tech.disablesDiseasesFromFloodPlains: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 3)
var Tech.enablesConscriptionOfUnits: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 4)
var Tech.enablesMobilizationLevels: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 5)
var Tech.enablesRecycling: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 6)
var Tech.enablesPrecisionBombing: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 7)
var Tech.enablesMutualProtectionPacts: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 8)
var Tech.enablesRightOfPassageTreaties: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 9)
var Tech.enablesMilitaryAlliances: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 10)
var Tech.enablesTradeEmbargoes: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 11)
var Tech.doublesEffectOfWealthImprovement: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 12)
var Tech.enablesTradeOverSeaTiles: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 13)
var Tech.enablesTradeOverOceanTiles: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 14)
var Tech.enablesMapTrading: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 15)
var Tech.enablesCommunicationTrading: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 16)
var Tech.notRequiredForEraAdvancement: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 17)
var Tech.doublesWorkRateOfWorkers: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 18)
var Tech.cannotBeTraded: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 19)
var Tech.permitsSacrifices: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 20)
var Tech.isBonusTech: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 21)
var Tech.revealsMap: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 22)

/**
 * Settable counterparts to [com.kelvsyc.rifflet.civ3.flavor1] and its 6 siblings.
 */
var Tech.flavor1: Boolean by BitCollection.int.mutableExtensionBitFlag({ flavors }, { flavors = it }, 0)
var Tech.flavor2: Boolean by BitCollection.int.mutableExtensionBitFlag({ flavors }, { flavors = it }, 1)
var Tech.flavor3: Boolean by BitCollection.int.mutableExtensionBitFlag({ flavors }, { flavors = it }, 2)
var Tech.flavor4: Boolean by BitCollection.int.mutableExtensionBitFlag({ flavors }, { flavors = it }, 3)
var Tech.flavor5: Boolean by BitCollection.int.mutableExtensionBitFlag({ flavors }, { flavors = it }, 4)
var Tech.flavor6: Boolean by BitCollection.int.mutableExtensionBitFlag({ flavors }, { flavors = it }, 5)
var Tech.flavor7: Boolean by BitCollection.int.mutableExtensionBitFlag({ flavors }, { flavors = it }, 6)
