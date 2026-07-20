package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.TechEntry
import okio.Buffer
import okio.ByteString

/**
 * Parses one `TECH` item, per existing reverse-engineering documentation of the BIX/BIQ format. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 *
 * Like `UnitEntryParser`, this checks [item]'s remaining size before reading its trailing
 * `unknown` field: a separate reverse-engineered reference implementation's struct includes this
 * field but existing reverse-engineering documentation omits it entirely. Because the generic
 * section loop in `Civ3RootParserImpl` already slices [item] to
 * the file's own declared length, `item.size` reliably reflects how many bytes actually remain
 * for this specific file.
 *
 * [TechEntry.flavors] and [TechEntry.unknown] form a single combined cutoff:
 * [Civ3FormatEra.VANILLA] and [Civ3FormatEra.PTW] items end right after [TechEntry.flags]
 * (neither field present — only `minor=18` [Civ3FormatEra.PTW] files are confirmed to include a
 * `TECH` section, so other PTW minors' shape here is unconfirmed); [Civ3FormatEra.CONQUESTS]
 * items include both (`flavors` may support Conquests' per-civilization tech trees). Each field
 * is guarded independently.
 */
internal object TechEntryParser {
    fun parse(item: Buffer): TechEntry {
        val name = item.readByteString(32L).truncateAtFirstNull()
        val civilopediaEntry = item.readByteString(32L).truncateAtFirstNull()
        val cost = item.readIntLe()
        val era = item.readIntLe()
        val advanceIcon = item.readIntLe()
        val x = item.readIntLe()
        val y = item.readIntLe()
        val prerequisite1 = item.readIntLe()
        val prerequisite2 = item.readIntLe()
        val prerequisite3 = item.readIntLe()
        val prerequisite4 = item.readIntLe()
        val flags = item.readIntLe()
        val flavors = if (item.size >= 4L) item.readIntLe() else 0
        val unknown = if (item.size >= 4L) item.readByteString(4L) else ByteString.of(0, 0, 0, 0)
        return TechEntry(
            name,
            civilopediaEntry,
            cost,
            era,
            advanceIcon,
            x,
            y,
            prerequisite1,
            prerequisite2,
            prerequisite3,
            prerequisite4,
            flags,
            flavors,
            unknown,
        )
    }
}
