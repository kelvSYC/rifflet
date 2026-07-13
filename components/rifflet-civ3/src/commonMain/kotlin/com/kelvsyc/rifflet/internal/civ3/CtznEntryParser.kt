package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.CtznEntry
import okio.Buffer

/**
 * Parses one `CTZN` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 */
internal object CtznEntryParser {
    fun parse(item: Buffer): CtznEntry {
        val defaultCitizen = item.readIntLe()
        val singularName = item.readByteString(32L).truncateAtFirstNull()
        val civilopediaEntry = item.readByteString(32L).truncateAtFirstNull()
        val pluralName = item.readByteString(32L).truncateAtFirstNull()
        val prerequisite = item.readIntLe()
        val luxuries = item.readIntLe()
        val research = item.readIntLe()
        val taxes = item.readIntLe()
        val corruption = item.readIntLe()
        val construction = item.readIntLe()
        return CtznEntry(
            defaultCitizen,
            singularName,
            civilopediaEntry,
            pluralName,
            prerequisite,
            luxuries,
            research,
            taxes,
            corruption,
            construction,
        )
    }
}
