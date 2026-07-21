package com.kelvsyc.rifflet.civ3

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.extensionBitFlag
import com.kelvsyc.kotlin.core.traits.integral.int

/**
 * Named accessors for [TechEntry.flags]'s 23 documented bits: bits 0-18 per the fuller,
 * later-era existing reverse-engineering documentation of the BIX/BIQ format, and bits 19-22
 * ([cannotBeTraded], [permitsSacrifices], [isBonusTech], [revealsMap]), matching a separate
 * reverse-engineered reference implementation's claimed count of 23. See [TechEntry.flags]'s
 * own KDoc.
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
val TechEntry.cannotBeTraded: Boolean by BitCollection.int.extensionBitFlag({ flags }, 19)
val TechEntry.permitsSacrifices: Boolean by BitCollection.int.extensionBitFlag({ flags }, 20)
val TechEntry.isBonusTech: Boolean by BitCollection.int.extensionBitFlag({ flags }, 21)
val TechEntry.revealsMap: Boolean by BitCollection.int.extensionBitFlag({ flags }, 22)

/**
 * Named accessors for [TechEntry.flavors]'s 7 documented bits (see that field's own KDoc for
 * how they were confirmed).
 */
val TechEntry.flavor1: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 0)
val TechEntry.flavor2: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 1)
val TechEntry.flavor3: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 2)
val TechEntry.flavor4: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 3)
val TechEntry.flavor5: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 4)
val TechEntry.flavor6: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 5)
val TechEntry.flavor7: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 6)
