package com.kelvsyc.rifflet.civ3

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.extensionBitFlag
import com.kelvsyc.kotlin.core.traits.integral.int

/**
 * Named accessors for [TechEntry.flags]'s 19 documented bits, per the fuller, later-era existing
 * reverse-engineering documentation of the BIX/BIQ format — as opposed to the 23 a separate
 * reverse-engineered reference implementation claims — see [TechEntry.flags]'s own KDoc.
 */
val TechEntry.enablesDiplomats: Boolean by BitCollection.int.extensionBitFlag({ flags }, 0)
val TechEntry.enablesIrrigationWithoutFreshWater: Boolean by BitCollection.int.extensionBitFlag({ flags }, 1)
val TechEntry.enablesBridges: Boolean by BitCollection.int.extensionBitFlag({ flags }, 2)
val TechEntry.disablesDiseasesFromFloodPlains: Boolean by BitCollection.int.extensionBitFlag({ flags }, 3)
val TechEntry.enablesConscriptionOfUnits: Boolean by BitCollection.int.extensionBitFlag({ flags }, 4)
val TechEntry.enablesMobilizationLevels: Boolean by BitCollection.int.extensionBitFlag({ flags }, 5)
val TechEntry.enablesRecycling: Boolean by BitCollection.int.extensionBitFlag({ flags }, 6)
val TechEntry.enablesPrecisionBombing: Boolean by BitCollection.int.extensionBitFlag({ flags }, 7)
val TechEntry.enablesMutualProtectionPacts: Boolean by BitCollection.int.extensionBitFlag({ flags }, 8)
val TechEntry.enablesRightOfPassageTreaties: Boolean by BitCollection.int.extensionBitFlag({ flags }, 9)
val TechEntry.enablesMilitaryAlliances: Boolean by BitCollection.int.extensionBitFlag({ flags }, 10)
val TechEntry.enablesTradeEmbargoes: Boolean by BitCollection.int.extensionBitFlag({ flags }, 11)
val TechEntry.doublesEffectOfWealthImprovement: Boolean by BitCollection.int.extensionBitFlag({ flags }, 12)
val TechEntry.enablesTradeOverSeaTiles: Boolean by BitCollection.int.extensionBitFlag({ flags }, 13)
val TechEntry.enablesTradeOverOceanTiles: Boolean by BitCollection.int.extensionBitFlag({ flags }, 14)
val TechEntry.enablesMapTrading: Boolean by BitCollection.int.extensionBitFlag({ flags }, 15)
val TechEntry.enablesCommunicationTrading: Boolean by BitCollection.int.extensionBitFlag({ flags }, 16)
val TechEntry.notRequiredForEraAdvancement: Boolean by BitCollection.int.extensionBitFlag({ flags }, 17)
val TechEntry.doublesWorkRateOfWorkers: Boolean by BitCollection.int.extensionBitFlag({ flags }, 18)
