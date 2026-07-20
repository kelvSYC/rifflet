package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.SlocEntry
import okio.Buffer

/**
 * Parses one `SLOC` item, per existing reverse-engineering documentation of the BIX/BIQ format. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 *
 * Every field in this section is present regardless of [Civ3FormatEra].
 */
internal object SlocEntryParser {
    fun parse(item: Buffer): SlocEntry {
        val ownerType = item.readIntLe()
        val owner = item.readIntLe()
        val x = item.readIntLe()
        val y = item.readIntLe()
        return SlocEntry(ownerType, owner, x, y)
    }
}
