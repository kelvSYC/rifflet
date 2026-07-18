package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.ErasEntry
import okio.Buffer
import okio.ByteString

/**
 * Parses one `ERAS` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 *
 * The trailing field (`unknown`) is read defensively: real vanilla (major=4) and PTW (major=11)
 * files omit it entirely — the 260-byte vanilla/PTW record is an exact prefix of the 264-byte
 * Conquests record — so the read checks `item.size` first and defaults when absent, matching
 * `BldgEntryParser`/`CtznEntryParser`/`TileEntryParser`'s established length-aware defensive
 * parsing pattern.
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
        val unknown = if (item.size >= 4L) item.readByteString(4L) else ByteString.of(0, 0, 0, 0)
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
