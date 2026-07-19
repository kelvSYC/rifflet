package com.kelvsyc.rifflet.civ3

import okio.ByteString

private fun ByteString.toIntLe(offset: Int): Int =
    (this[offset].toInt() and 0xFF) or
        ((this[offset + 1].toInt() and 0xFF) shl 8) or
        ((this[offset + 2].toInt() and 0xFF) shl 16) or
        ((this[offset + 3].toInt() and 0xFF) shl 24)

/**
 * The first named 4-byte sub-field of [BldgEntry.flags], per Apolyton's "Civilization III
 * BIX/BIQ file format" thread's "improvements (binary)" field. See [BldgEntry.centerOfEmpire]
 * and its sibling accessors for the 25 named bits.
 */
val BldgEntry.improvements: Int get() = flags.toIntLe(0)

/**
 * Named accessors for [BldgEntry.improvements]'s 25 documented bits.
 */
val BldgEntry.centerOfEmpire: Boolean get() = improvements and (1 shl 0) != 0
val BldgEntry.veteranGroundUnits: Boolean get() = improvements and (1 shl 1) != 0
val BldgEntry.plus50PercentResearchOutput: Boolean get() = improvements and (1 shl 2) != 0
val BldgEntry.plus50PercentLuxuryOutput: Boolean get() = improvements and (1 shl 3) != 0
val BldgEntry.plus50PercentTaxOutput: Boolean get() = improvements and (1 shl 4) != 0
val BldgEntry.removesPopulationPollution: Boolean get() = improvements and (1 shl 5) != 0
val BldgEntry.reducesBuildingPollution: Boolean get() = improvements and (1 shl 6) != 0
val BldgEntry.resistantToBribery: Boolean get() = improvements and (1 shl 7) != 0
val BldgEntry.improvementsReducesCorruption: Boolean get() = improvements and (1 shl 8) != 0
val BldgEntry.doublesCityGrowthRate: Boolean get() = improvements and (1 shl 9) != 0
val BldgEntry.increasesLuxuryTrade: Boolean get() = improvements and (1 shl 10) != 0
val BldgEntry.allowsCitySizeLevel2: Boolean get() = improvements and (1 shl 11) != 0
val BldgEntry.allowsCitySizeLevel3: Boolean get() = improvements and (1 shl 12) != 0
val BldgEntry.replacesOtherBuildings: Boolean get() = improvements and (1 shl 13) != 0
val BldgEntry.mustBeNearWater: Boolean get() = improvements and (1 shl 14) != 0
val BldgEntry.mustBeNearARiver: Boolean get() = improvements and (1 shl 15) != 0
val BldgEntry.canExplodeOrMeltdown: Boolean get() = improvements and (1 shl 16) != 0
val BldgEntry.veteranSeaUnits: Boolean get() = improvements and (1 shl 17) != 0
val BldgEntry.veteranAirUnits: Boolean get() = improvements and (1 shl 18) != 0
val BldgEntry.capitalization: Boolean get() = improvements and (1 shl 19) != 0
val BldgEntry.allowsWaterTrade: Boolean get() = improvements and (1 shl 20) != 0
val BldgEntry.allowsAirTrade: Boolean get() = improvements and (1 shl 21) != 0
val BldgEntry.reducesWarWeariness: Boolean get() = improvements and (1 shl 22) != 0
val BldgEntry.increasesShieldsInWater: Boolean get() = improvements and (1 shl 23) != 0
val BldgEntry.increasesFoodInWater: Boolean get() = improvements and (1 shl 24) != 0
