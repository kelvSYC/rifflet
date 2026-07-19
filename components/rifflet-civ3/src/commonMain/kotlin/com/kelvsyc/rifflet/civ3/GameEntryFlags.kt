package com.kelvsyc.rifflet.civ3

import okio.ByteString

private fun ByteString.toIntLe(): Int =
    (this[0].toInt() and 0xFF) or
        ((this[1].toInt() and 0xFF) shl 8) or
        ((this[2].toInt() and 0xFF) shl 16) or
        ((this[3].toInt() and 0xFF) shl 24)

/**
 * Named accessors for [GameEntry.flags]'s 16 documented bits, per Apolyton's "Civilization III
 * BIX/BIQ file format" thread.
 */
val GameEntry.dominationVictoryEnabled: Boolean get() = flags.toIntLe() and (1 shl 0) != 0
val GameEntry.spaceRaceVictoryEnabled: Boolean get() = flags.toIntLe() and (1 shl 1) != 0
val GameEntry.diplomaticVictoryEnabled: Boolean get() = flags.toIntLe() and (1 shl 2) != 0
val GameEntry.victoryByConquestEnabled: Boolean get() = flags.toIntLe() and (1 shl 3) != 0
val GameEntry.culturalVictoryEnabled: Boolean get() = flags.toIntLe() and (1 shl 4) != 0
val GameEntry.civSpecificAbilitiesEnabled: Boolean get() = flags.toIntLe() and (1 shl 5) != 0
val GameEntry.culturallyLinkedStart: Boolean get() = flags.toIntLe() and (1 shl 6) != 0
val GameEntry.restartPlayers: Boolean get() = flags.toIntLe() and (1 shl 7) != 0
val GameEntry.preserveRandomSeed: Boolean get() = flags.toIntLe() and (1 shl 8) != 0
val GameEntry.acceleratedProduction: Boolean get() = flags.toIntLe() and (1 shl 9) != 0
val GameEntry.eliminationEnabled: Boolean get() = flags.toIntLe() and (1 shl 10) != 0
val GameEntry.regicideEnabled: Boolean get() = flags.toIntLe() and (1 shl 11) != 0
val GameEntry.massRegicideEnabled: Boolean get() = flags.toIntLe() and (1 shl 12) != 0
val GameEntry.victoryLocationsEnabled: Boolean get() = flags.toIntLe() and (1 shl 13) != 0
val GameEntry.captureTheFlagEnabled: Boolean get() = flags.toIntLe() and (1 shl 14) != 0
val GameEntry.allowCulturalConversions: Boolean get() = flags.toIntLe() and (1 shl 15) != 0
