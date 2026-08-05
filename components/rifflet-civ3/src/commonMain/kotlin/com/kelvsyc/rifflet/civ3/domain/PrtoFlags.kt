package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.mutableExtensionBitFlag

// --- Prto.abilities (31 bits) ---

var Prto.wheeledAbility: Boolean by BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 0)
var Prto.footUnitAbility: Boolean by BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 1)
var Prto.blitzAbility: Boolean by BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 2)
var Prto.cruiseMissileAbility: Boolean by BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 3)
var Prto.allTerrainAsRoadsAbility: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 4)
var Prto.radarAbility: Boolean by BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 5)
var Prto.amphibiousAbility: Boolean by BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 6)
var Prto.invisibleAbility: Boolean by BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 7)
var Prto.transportsOnlyAircraftAbility: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 8)
var Prto.draftAbility: Boolean by BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 9)
var Prto.immobileAbility: Boolean by BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 10)
var Prto.sinksInSeaAbility: Boolean by BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 11)
var Prto.sinksInOceanAbility: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 12)
var Prto.flagUnitAbility: Boolean by BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 13)
var Prto.transportsOnlyFootUnitsAbility: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 14)
var Prto.startsGoldenAgeAbility: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 15)
var Prto.nuclearWeaponAbility: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 16)
var Prto.hiddenNationalityAbility: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 17)
var Prto.armyAbility: Boolean by BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 18)
var Prto.leaderAbility: Boolean by BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 19)
var Prto.infiniteBombardRangeAbility: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 20)
var Prto.stealthAbility: Boolean by BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 21)
var Prto.detectInvisibleAbility: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 22)
var Prto.tacticalMissileAbility: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 23)
var Prto.transportsOnlyTacticalMissilesAbility: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 24)
var Prto.rangedAttackAnimationAbility: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 25)
var Prto.rotateBeforeAttackAbility: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 26)
var Prto.lethalLandBombardmentAbility: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 27)
var Prto.lethalSeaBombardmentAbility: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 28)
var Prto.kingAbility: Boolean by BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 29)
var Prto.requiresEscortAbility: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ abilities }, { abilities = it }, 30)

// --- Prto.aiStrategies (20 bits) ---

var Prto.offenseStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 0)
var Prto.defenseStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 1)
var Prto.artilleryStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 2)
var Prto.exploreStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 3)
var Prto.armyStrategy: Boolean by BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 4)
var Prto.cruiseMissileStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 5)
var Prto.airBombardStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 6)
var Prto.airDefenseStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 7)
var Prto.navalPowerStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 8)
var Prto.airTransportStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 9)
var Prto.navalTransportStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 10)
var Prto.navalCarrierStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 11)
var Prto.terraformStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 12)
var Prto.settleStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 13)
var Prto.leaderStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 14)
var Prto.tacticalNukeStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 15)
var Prto.icbmStrategy: Boolean by BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 16)
var Prto.navalMissileTransportStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 17)
var Prto.flagUnitStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 18)
var Prto.kingStrategy: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ aiStrategies }, { aiStrategies = it }, 19)
