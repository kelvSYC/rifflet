package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.TfrmEntry
import okio.Buffer

/**
 * Parses one `TFRM` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 */
internal object TfrmEntryParser {
    fun parse(item: Buffer): TfrmEntry {
        val name = item.readByteString(32L).truncateAtFirstNull()
        val civilopediaEntry = item.readByteString(32L).truncateAtFirstNull()
        val turnsToComplete = item.readIntLe()
        val required = item.readIntLe()
        val requiredResource1 = item.readIntLe()
        val requiredResource2 = item.readIntLe()
        val order = item.readByteString(32L).truncateAtFirstNull()
        return TfrmEntry(
            name,
            civilopediaEntry,
            turnsToComplete,
            required,
            requiredResource1,
            requiredResource2,
            order,
        )
    }
}
