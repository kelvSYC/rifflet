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
 * The first named 4-byte sub-field of [BldgEntry.flags], per existing reverse-engineering
 * documentation of the BIX/BIQ format's "improvements (binary)" field. See
 * [BldgEntry.centerOfEmpire] and its sibling accessors for the 25 named bits.
 */
val BldgEntry.improvements: Int get() = flags.toIntLe(0)

/**
 * Named accessors for [BldgEntry.improvements]'s 30 documented bits. Bit 7 ([resistantToBribery])
 * is labeled "Resistant to Propaganda" in the Conquests Rules Editor, not "Resistant to
 * Bribery" — a naming mismatch, not a missing bit. [requiredGoodsMustBeInCityRadius] (bit 31) is
 * the [Civ3FormatEra.CONQUESTS]-tier location of this flag; [Civ3FormatEra.PTW] and
 * [Civ3FormatEra.VANILLA] instead use [ptwRequiredGoodsMustBeInCityRadius] (`smallWonders` bit
 * 9) — see `BldgEntryReferences.kt`'s era-aware `requiredGoodsMustBeInCityRadius(era)` resolver.
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
val BldgEntry.increasesTradeInWater: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 25)
val BldgEntry.stealthAttackBarrier: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 27)
val BldgEntry.doublesSacrifice: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 29)
val BldgEntry.producesUnits: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 30)
val BldgEntry.requiredGoodsMustBeInCityRadius: Boolean by BitCollection.int.extensionBitFlag({ improvements }, 31)

/**
 * The second named 4-byte sub-field of [BldgEntry.flags]. Existing reverse-engineering
 * documentation lists 10 bits here in the vanilla/PTW era ("coastal installation" through
 * "construction installation"), later renamed to trait names ([militaristic], [scientific],
 * [commercial], [expansionist], [religious], [industrious]) and extended with 2 new
 * Conquests-only bits ([agricultural], [seafaring]) in a subsequent correction — this codebase
 * exposes the final, corrected 12-bit layout.
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
val BldgEntry.seafaring: Boolean by BitCollection.int.extensionBitFlag({ otherCharacteristics }, 11)

/**
 * The third named 4-byte sub-field of [BldgEntry.flags], per existing reverse-engineering
 * documentation's "small wonders (binary)" field. See [BldgEntry.increasesChanceOfLeaderAppearance]
 * and its sibling accessors for the 12 named bits. [requiresEliteNavalUnits] (bit 11) lives in
 * this sub-field's byte range even though the Rules Editor presents it (and every bit here) in
 * the same combined "Wonders and Small Wonders" grid regardless of whether the building's own
 * category is Wonder or Small Wonder. Bit 9, [ptwRequiredGoodsMustBeInCityRadius], is the
 * [Civ3FormatEra.PTW]-tier location of [BldgEntry.requiredGoodsMustBeInCityRadius].
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
val BldgEntry.ptwRequiredGoodsMustBeInCityRadius: Boolean by BitCollection.int.extensionBitFlag({ smallWonders }, 9)
val BldgEntry.requiresVictoriousArmy: Boolean by BitCollection.int.extensionBitFlag({ smallWonders }, 10)
val BldgEntry.requiresEliteNavalUnits: Boolean by BitCollection.int.extensionBitFlag({ smallWonders }, 11)

/**
 * The fourth named 4-byte sub-field of [BldgEntry.flags], per existing reverse-engineering
 * documentation's "wonders (binary)" field. See [BldgEntry.safeSeaTravel] and its sibling
 * accessors for the 17 named bits. [plus2ShipMovement] (bit 14) is distinct from the
 * already-named [plus1ShipMovement].
 *
 * Bit 15 has no corresponding checkbox in the Rules Editor UI — distinct from
 * [BldgEntry.requiresEliteNavalUnits] — the same way `GovtRelationship.canBribe` isn't exposed
 * by the Governments tab; no accessor is added for it.
 */
val BldgEntry.wonders: Int get() = flags.toIntLe(12)

val BldgEntry.safeSeaTravel: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 0)
val BldgEntry.gainAnyAdvancesOwnedBy2Civs: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 1)
val BldgEntry.doubleCombatStrengthVsBarbarians: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 2)
val BldgEntry.plus1ShipMovement: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 3)
val BldgEntry.plus2ShipMovement: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 14)
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
val BldgEntry.increasedArmyValue: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 16)
val BldgEntry.touristAttraction: Boolean by BitCollection.int.extensionBitFlag({ wonders }, 17)

/**
 * Named accessors for [BldgEntry.flavors]'s 7 documented bits (see that field's own KDoc for
 * how they were confirmed).
 */
val BldgEntry.flavor1: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 0)
val BldgEntry.flavor2: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 1)
val BldgEntry.flavor3: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 2)
val BldgEntry.flavor4: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 3)
val BldgEntry.flavor5: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 4)
val BldgEntry.flavor6: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 5)
val BldgEntry.flavor7: Boolean by BitCollection.int.extensionBitFlag({ flavors }, 6)
