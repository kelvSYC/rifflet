package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.mutableExtensionBitFlag

/**
 * Settable counterparts to [com.kelvsyc.rifflet.civ3.dominationVictoryEnabled] and its 16
 * siblings — see that file's KDoc for what each bit means.
 */
var Game.dominationVictoryEnabled: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 0)
var Game.spaceRaceVictoryEnabled: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 1)
var Game.diplomaticVictoryEnabled: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 2)
var Game.victoryByConquestEnabled: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 3)
var Game.culturalVictoryEnabled: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 4)
var Game.civSpecificAbilitiesEnabled: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 5)
var Game.culturallyLinkedStart: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 6)
var Game.restartPlayers: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 7)
var Game.preserveRandomSeed: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 8)
var Game.acceleratedProduction: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 9)
var Game.eliminationEnabled: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 10)
var Game.regicideEnabled: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 11)
var Game.massRegicideEnabled: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 12)
var Game.victoryPointScoringEnabled: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 13)
var Game.captureTheFlagEnabled: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 14)
var Game.allowCulturalConversions: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 15)
var Game.wonderVictoryEnabled: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 16)
