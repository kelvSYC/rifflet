package com.kelvsyc.rifflet.civ3

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.extensionBitFlag
import com.kelvsyc.kotlin.core.traits.integral.int
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
val BldgEntry.centerOfEmpire: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 0)
val BldgEntry.veteranGroundUnits: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 1)
val BldgEntry.plus50PercentResearchOutput: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 2)
val BldgEntry.plus50PercentLuxuryOutput: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 3)
val BldgEntry.plus50PercentTaxOutput: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 4)
val BldgEntry.removesPopulationPollution: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 5)
val BldgEntry.reducesBuildingPollution: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 6)
val BldgEntry.resistantToBribery: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 7)
val BldgEntry.improvementsReducesCorruption: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 8)
val BldgEntry.doublesCityGrowthRate: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 9)
val BldgEntry.increasesLuxuryTrade: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 10)
val BldgEntry.allowsCitySizeLevel2: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 11)
val BldgEntry.allowsCitySizeLevel3: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 12)
val BldgEntry.replacesOtherBuildings: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 13)
val BldgEntry.mustBeNearWater: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 14)
val BldgEntry.mustBeNearARiver: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 15)
val BldgEntry.canExplodeOrMeltdown: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 16)
val BldgEntry.veteranSeaUnits: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 17)
val BldgEntry.veteranAirUnits: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 18)
val BldgEntry.capitalization: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 19)
val BldgEntry.allowsWaterTrade: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 20)
val BldgEntry.allowsAirTrade: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 21)
val BldgEntry.reducesWarWeariness: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 22)
val BldgEntry.increasesShieldsInWater: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 23)
val BldgEntry.increasesFoodInWater: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 24)

/**
 * The second named 4-byte sub-field of [BldgEntry.flags]. Apolyton documents 10 bits here in
 * the vanilla/PTW era ("coastal installation" through "construction installation"), later
 * renamed to trait names ([militaristic], [scientific], [commercial], [expansionist],
 * [religious], [industrious]) and extended with 2 new Conquests-only bits ([agricultural],
 * [seaFaring]) in a 2004 forum correction — this codebase exposes the final, corrected 12-bit
 * layout.
 */
val BldgEntry.otherCharacteristics: Int get() = flags.toIntLe(4)

val BldgEntry.coastalInstallation: Boolean by BitCollection.int.extensionBitFlag({ otherCharacteristics }, 0)
val BldgEntry.militaristic: Boolean by BitCollection.int.extensionBitFlag({ otherCharacteristics }, 1)
val BldgEntry.wonder: Boolean by BitCollection.int.extensionBitFlag({ otherCharacteristics }, 2)
val BldgEntry.smallWonder: Boolean by BitCollection.int.extensionBitFlag({ otherCharacteristics }, 3)
val BldgEntry.continentalMoodEffects: Boolean by BitCollection.int.extensionBitFlag({ otherCharacteristics }, 4)
val BldgEntry.scientific: Boolean by BitCollection.int.extensionBitFlag({ otherCharacteristics }, 5)
val BldgEntry.commercial: Boolean by BitCollection.int.extensionBitFlag({ otherCharacteristics }, 6)
val BldgEntry.expansionist: Boolean by BitCollection.int.extensionBitFlag({ otherCharacteristics }, 7)
val BldgEntry.religious: Boolean by BitCollection.int.extensionBitFlag({ otherCharacteristics }, 8)
val BldgEntry.industrious: Boolean by BitCollection.int.extensionBitFlag({ otherCharacteristics }, 9)
val BldgEntry.agricultural: Boolean by BitCollection.int.extensionBitFlag({ otherCharacteristics }, 10)
val BldgEntry.seaFaring: Boolean by BitCollection.int.extensionBitFlag({ otherCharacteristics }, 11)

/**
 * The third named 4-byte sub-field of [BldgEntry.flags], per Apolyton's "small wonders (binary)"
 * field. See [BldgEntry.increasesChanceOfLeaderAppearance] and its sibling accessors for the 11
 * named bits.
 */
val BldgEntry.smallWonders: Int get() = flags.toIntLe(8)

val BldgEntry.increasesChanceOfLeaderAppearance: Boolean by BitCollection.int.extensionBitFlag({ smallWonders }, 0)
val BldgEntry.buildArmiesWithoutLeader: Boolean by BitCollection.int.extensionBitFlag({ smallWonders }, 1)
val BldgEntry.buildLargerArmies: Boolean by BitCollection.int.extensionBitFlag({ smallWonders }, 2)
val BldgEntry.treasuryEarns5Percent: Boolean by BitCollection.int.extensionBitFlag({ smallWonders }, 3)
val BldgEntry.buildSpaceshipParts: Boolean by BitCollection.int.extensionBitFlag({ smallWonders }, 4)
val BldgEntry.smallWondersReducesCorruption: Boolean by BitCollection.int.extensionBitFlag({ smallWonders }, 5)
val BldgEntry.decreasesSuccessOfMissileAttacks: Boolean by BitCollection.int.extensionBitFlag({ smallWonders }, 6)
val BldgEntry.allowsSpyMissions: Boolean by BitCollection.int.extensionBitFlag({ smallWonders }, 7)
val BldgEntry.allowsHealingInEnemyTerritory: Boolean by BitCollection.int.extensionBitFlag({ smallWonders }, 8)
val BldgEntry.requiredGoodsMustBeInCityRadius: Boolean by BitCollection.int.extensionBitFlag({ smallWonders }, 9)
val BldgEntry.requiresVictoriousArmy: Boolean by BitCollection.int.extensionBitFlag({ smallWonders }, 10)

/**
 * The fourth named 4-byte sub-field of [BldgEntry.flags], per Apolyton's "wonders (binary)"
 * field. See [BldgEntry.safeSeaTravel] and its sibling accessors for the 14 named bits.
 */
val BldgEntry.wonders: Int get() = flags.toIntLe(12)

val BldgEntry.safeSeaTravel: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 0)
val BldgEntry.gainAnyAdvancesOwnedBy2Civs: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 1)
val BldgEntry.doubleCombatStrengthVsBarbarians: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 2)
val BldgEntry.plus1ShipMovement: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 3)
val BldgEntry.doublesResearchOutput: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 4)
val BldgEntry.plus1TradeInEachTradeProducingTile: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 5)
val BldgEntry.halvesUnitUpgradeCost: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 6)
val BldgEntry.paysMaintenanceForTradeInstallations: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 7)
val BldgEntry.allowsAllCivsToBuildNuclears: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 8)
val BldgEntry.cityGrowthCausesPlus2Citizens: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 9)
val BldgEntry.plus2FreeAdvances: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 10)
val BldgEntry.reducesWarWearinessInAllCities: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 11)
val BldgEntry.doublesCityDefenses: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 12)
val BldgEntry.allowsDiplomaticVictory: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 13)
