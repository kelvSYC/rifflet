package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.EspnEntry
import okio.Buffer

/**
 * Parses one `ESPN` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 */
internal object EspnEntryParser {
    fun parse(item: Buffer): EspnEntry {
        val description = item.readByteString(128L).truncateAtFirstNull()
        val name = item.readByteString(64L).truncateAtFirstNull()
        val civilopediaEntry = item.readByteString(32L).truncateAtFirstNull()
        val missionFlags = item.readIntLe()
        val baseCost = item.readIntLe()
        return EspnEntry(description, name, civilopediaEntry, missionFlags, baseCost)
    }
}
