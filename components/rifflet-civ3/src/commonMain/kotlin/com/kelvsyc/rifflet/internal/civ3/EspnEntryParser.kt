package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.EspnEntry
import okio.Buffer

/**
 * Parses one `ESPN` item, per existing reverse-engineering documentation of the BIX/BIQ format. Reads directly off
 * `item`, a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 *
 * Every field in this section is present regardless of [Civ3FormatEra].
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
