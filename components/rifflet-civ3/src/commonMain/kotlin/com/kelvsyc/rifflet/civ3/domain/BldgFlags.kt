package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.mutableExtensionBitFlag

// --- Building.improvements (30 bits, declared once for all 4 variants) ---

var Building.centerOfEmpire: Boolean by BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 0)
var Building.veteranGroundUnits: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 1)
var Building.plus50PercentResearchOutput: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 2)
var Building.plus50PercentLuxuryOutput: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 3)
var Building.plus50PercentTaxOutput: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 4)
var Building.removesPopulationPollution: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 5)
var Building.reducesBuildingPollution: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 6)
var Building.resistantToBribery: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 7)
var Building.improvementsReducesCorruption: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 8)
var Building.doublesCityGrowthRate: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 9)
var Building.increasesLuxuryTrade: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 10)
var Building.allowsCitySizeLevel2: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 11)
var Building.allowsCitySizeLevel3: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 12)
var Building.replacesOtherBuildings: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 13)
var Building.mustBeNearWater: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 14)
var Building.mustBeNearARiver: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 15)
var Building.canExplodeOrMeltdown: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 16)
var Building.veteranSeaUnits: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 17)
var Building.veteranAirUnits: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 18)
var Building.capitalization: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 19)
var Building.allowsWaterTrade: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 20)
var Building.allowsAirTrade: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 21)
var Building.reducesWarWeariness: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 22)
var Building.increasesShieldsInWater: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 23)
var Building.increasesFoodInWater: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 24)
var Building.increasesTradeInWater: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 25)
var Building.stealthAttackBarrier: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 27)
var Building.doublesSacrifice: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 29)
var Building.producesUnits: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 30)
var Building.requiredGoodsMustBeInCityRadius: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ improvements }, { improvements = it }, 31)

// --- Building.otherCharacteristics (10 non-discriminant bits — bits 2 (wonder) and 3
// (smallWonder) get no accessor here, since which Building subtype an instance is already
// expresses that information) ---

var Building.coastalInstallation: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ otherCharacteristics }, { otherCharacteristics = it }, 0)
var Building.militaristic: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ otherCharacteristics }, { otherCharacteristics = it }, 1)
var Building.continentalMoodEffects: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ otherCharacteristics }, { otherCharacteristics = it }, 4)
var Building.scientific: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ otherCharacteristics }, { otherCharacteristics = it }, 5)
var Building.commercial: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ otherCharacteristics }, { otherCharacteristics = it }, 6)
var Building.expansionist: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ otherCharacteristics }, { otherCharacteristics = it }, 7)
var Building.religious: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ otherCharacteristics }, { otherCharacteristics = it }, 8)
var Building.industrious: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ otherCharacteristics }, { otherCharacteristics = it }, 9)
var Building.agricultural: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ otherCharacteristics }, { otherCharacteristics = it }, 10)
var Building.seafaring: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ otherCharacteristics }, { otherCharacteristics = it }, 11)

// --- Wonder.smallWonders (12 bits, declared once for SmallWonder and GreatWonder) ---

var Wonder.increasesChanceOfLeaderAppearance: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ smallWonders }, { smallWonders = it }, 0)
var Wonder.buildArmiesWithoutLeader: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ smallWonders }, { smallWonders = it }, 1)
var Wonder.buildLargerArmies: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ smallWonders }, { smallWonders = it }, 2)
var Wonder.treasuryEarns5Percent: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ smallWonders }, { smallWonders = it }, 3)
var Wonder.buildSpaceshipParts: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ smallWonders }, { smallWonders = it }, 4)
var Wonder.smallWondersReducesCorruption: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ smallWonders }, { smallWonders = it }, 5)
var Wonder.decreasesSuccessOfMissileAttacks: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ smallWonders }, { smallWonders = it }, 6)
var Wonder.allowsSpyMissions: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ smallWonders }, { smallWonders = it }, 7)
var Wonder.allowsHealingInEnemyTerritory: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ smallWonders }, { smallWonders = it }, 8)
var Wonder.ptwRequiredGoodsMustBeInCityRadius: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ smallWonders }, { smallWonders = it }, 9)
var Wonder.requiresVictoriousArmy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ smallWonders }, { smallWonders = it }, 10)
var Wonder.requiresEliteNavalUnits: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ smallWonders }, { smallWonders = it }, 11)

// --- Wonder.wonders (17 bits, declared once for SmallWonder and GreatWonder) ---

var Wonder.safeSeaTravel: Boolean by BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 0)
var Wonder.gainAnyAdvancesOwnedBy2Civs: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 1)
var Wonder.doubleCombatStrengthVsBarbarians: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 2)
var Wonder.plus1ShipMovement: Boolean by BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 3)
var Wonder.doublesResearchOutput: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 4)
var Wonder.plus1TradeInEachTradeProducingTile: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 5)
var Wonder.halvesUnitUpgradeCost: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 6)
var Wonder.paysMaintenanceForTradeInstallations: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 7)
var Wonder.allowsAllCivsToBuildNuclears: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 8)
var Wonder.cityGrowthCausesPlus2Citizens: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 9)
var Wonder.plus2FreeAdvances: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 10)
var Wonder.reducesWarWearinessInAllCities: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 11)
var Wonder.doublesCityDefenses: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 12)
var Wonder.allowsDiplomaticVictory: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 13)
var Wonder.plus2ShipMovement: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 14)
var Wonder.increasedArmyValue: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 16)
var Wonder.touristAttraction: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ wonders }, { wonders = it }, 17)
