package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.ErasEntry
import okio.Buffer

/**
 * Parses one `ERAS` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 */
internal object ErasEntryParser {
    fun parse(item: Buffer): ErasEntry {
        val name = item.readByteString(64L).truncateAtFirstNull()
        val civilopediaEntry = item.readByteString(32L).truncateAtFirstNull()
        val researcher1 = item.readByteString(32L).truncateAtFirstNull()
        val researcher2 = item.readByteString(32L).truncateAtFirstNull()
        val researcher3 = item.readByteString(32L).truncateAtFirstNull()
        val researcher4 = item.readByteString(32L).truncateAtFirstNull()
        val researcher5 = item.readByteString(32L).truncateAtFirstNull()
        val numberOfUsedResearcherNames = item.readIntLe()
        val unknown = item.readByteString(4L)
        return ErasEntry(
            name,
            civilopediaEntry,
            researcher1,
            researcher2,
            researcher3,
            researcher4,
            researcher5,
            numberOfUsedResearcherNames,
            unknown,
        )
    }
}
