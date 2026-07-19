package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.CtznEntry
import okio.Buffer

/**
 * Parses one `CTZN` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 *
 * The trailing two fields (`corruption`, `construction`) are read defensively: real
 * [Civ3FormatEra.VANILLA]/[Civ3FormatEra.PTW] files omit them entirely (confirmed by decoding
 * real `CTZN` items from a genuine [Civ3FormatEra.PTW] `.bix` scenario file — the
 * [Civ3FormatEra.VANILLA]/[Civ3FormatEra.PTW] 116-byte record is an exact prefix of the
 * [Civ3FormatEra.CONQUESTS] 124-byte record), so each read checks `item.size` first and defaults
 * when absent, matching `BldgEntryParser`/`TechEntryParser`/`UnitEntryParser`/`RuleEntryParser`'s
 * established length-aware defensive parsing pattern. Only a single [Civ3FormatEra.PTW] sample
 * file was used to confirm this shape; which minor it was is not recorded, so PTW minor
 * sensitivity here is unconfirmed.
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
        val corruption = if (item.size >= 4L) item.readIntLe() else 0
        val construction = if (item.size >= 4L) item.readIntLe() else 0
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
