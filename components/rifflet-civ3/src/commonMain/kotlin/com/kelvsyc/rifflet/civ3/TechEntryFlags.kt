package com.kelvsyc.rifflet.civ3

/**
 * Named accessors for [TechEntry.flags]'s 19 documented bits, per Apolyton's "Civilization III
 * BIX/BIQ file format" thread (the fuller, later-era source; confirmed to list exactly these 19
 * bits, not the 23 `QueryCiv3` claims — see [TechEntry.flags]'s own KDoc).
 */
val TechEntry.enablesDiplomats: Boolean get() = flags and (1 shl 0) != 0
val TechEntry.enablesIrrigationWithoutFreshWater: Boolean get() = flags and (1 shl 1) != 0
val TechEntry.enablesBridges: Boolean get() = flags and (1 shl 2) != 0
val TechEntry.disablesDiseasesFromFloodPlains: Boolean get() = flags and (1 shl 3) != 0
val TechEntry.enablesConscriptionOfUnits: Boolean get() = flags and (1 shl 4) != 0
val TechEntry.enablesMobilizationLevels: Boolean get() = flags and (1 shl 5) != 0
val TechEntry.enablesRecycling: Boolean get() = flags and (1 shl 6) != 0
val TechEntry.enablesPrecisionBombing: Boolean get() = flags and (1 shl 7) != 0
val TechEntry.enablesMutualProtectionPacts: Boolean get() = flags and (1 shl 8) != 0
val TechEntry.enablesRightOfPassageTreaties: Boolean get() = flags and (1 shl 9) != 0
val TechEntry.enablesMilitaryAlliances: Boolean get() = flags and (1 shl 10) != 0
val TechEntry.enablesTradeEmbargoes: Boolean get() = flags and (1 shl 11) != 0
val TechEntry.doublesEffectOfWealthImprovement: Boolean get() = flags and (1 shl 12) != 0
val TechEntry.enablesTradeOverSeaTiles: Boolean get() = flags and (1 shl 13) != 0
val TechEntry.enablesTradeOverOceanTiles: Boolean get() = flags and (1 shl 14) != 0
val TechEntry.enablesMapTrading: Boolean get() = flags and (1 shl 15) != 0
val TechEntry.enablesCommunicationTrading: Boolean get() = flags and (1 shl 16) != 0
val TechEntry.notRequiredForEraAdvancement: Boolean get() = flags and (1 shl 17) != 0
val TechEntry.doublesWorkRateOfWorkers: Boolean get() = flags and (1 shl 18) != 0
