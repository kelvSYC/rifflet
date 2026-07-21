package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.ErasEntry
import okio.Buffer
import okio.ByteString

/**
 * Parses one `ERAS` item, per existing reverse-engineering documentation of the BIX/BIQ format. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 *
 * The trailing field (`unknown`) is read defensively: real [Civ3FormatEra.VANILLA] (`major=4`)
 * and [Civ3FormatEra.PTW] (`major=11`) files omit it entirely — the 260-byte
 * [Civ3FormatEra.VANILLA]/[Civ3FormatEra.PTW] record is an exact prefix of the 264-byte
 * [Civ3FormatEra.CONQUESTS] record — so the read checks `item.size` first and defaults when
 * absent, matching `BldgEntryParser`/`CtznEntryParser`/`TileEntryParser`'s established
 * length-aware defensive parsing pattern. [Civ3FormatEra.PTW] minor sensitivity was not
 * separately tracked during the original investigation of this section.
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
