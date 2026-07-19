package com.kelvsyc.rifflet.civ3

/**
 * Named accessors for [EspnEntry.missionFlags]'s 2 documented bits, per Apolyton's "Civilization
 * III BIC file format (2nd thread)".
 */
val EspnEntry.diplomat: Boolean get() = missionFlags and (1 shl 0) != 0
val EspnEntry.spy: Boolean get() = missionFlags and (1 shl 1) != 0
