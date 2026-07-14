package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.ContEntry
import okio.Buffer

/**
 * Parses one `CONT` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 */
internal object ContEntryParser {
    fun parse(item: Buffer): ContEntry {
        val type = item.readIntLe()
        val numberOfTiles = item.readIntLe()
        return ContEntry(type, numberOfTiles)
    }
}
