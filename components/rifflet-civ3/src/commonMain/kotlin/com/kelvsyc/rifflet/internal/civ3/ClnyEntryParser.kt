package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.ClnyEntry
import com.kelvsyc.rifflet.civ3.ClnyImprovementType
import okio.Buffer

/**
 * Parses one `CLNY` item, per existing reverse-engineering documentation of the BIX/BIQ format,
 * which has a stray byte-length annotation of 16 immediately above this item's 5-field list;
 * cross-checked against a separate reverse-engineered reference implementation's
 * independently-maintained equivalent struct, which has no such inconsistency, confirms the
 * correct length is 20 bytes / 5 fields, not 16 — this parser and
 * [ClnyEntry] use the 20-byte, 5-field interpretation. Reads directly off [item], a
 * zero-copy-transferred [Buffer] already stripped of its own length prefix by the generic
 * section loop.
 *
 * Every field in this section is present regardless of [Civ3FormatEra].
 */
internal object ClnyEntryParser {
    fun parse(item: Buffer): ClnyEntry {
        val ownerType = item.readIntLe()
        val owner = item.readIntLe()
        val x = item.readIntLe()
        val y = item.readIntLe()
        val improvementType = decodeEnum("ClnyEntry.improvementType", item.readIntLe(), ClnyImprovementType.entries)
        return ClnyEntry(ownerType, owner, x, y, improvementType)
    }
}
