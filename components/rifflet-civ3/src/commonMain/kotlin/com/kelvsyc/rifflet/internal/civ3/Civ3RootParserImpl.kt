package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3File
import com.kelvsyc.rifflet.civ3.Civ3RawSection
import com.kelvsyc.rifflet.civ3.Civ3Section
import com.kelvsyc.rifflet.civ3.Civ3SectionIds
import com.kelvsyc.rifflet.civ3.DiffSection
import com.kelvsyc.rifflet.civ3.ErasSection
import com.kelvsyc.rifflet.civ3.GovtSection
import com.kelvsyc.rifflet.civ3.WsizSection
import com.kelvsyc.rifflet.internal.core.readChunkId
import okio.Buffer
import okio.BufferedSource

/**
 * Parses the flat, non-nested sequence of Civ3 sections that follows the `VER#` header: each
 * section is a 4-byte marker, a little-endian 4-byte item count, then that many length-prefixed
 * items. Items are read as zero-copy-transferred [Buffer]s (`source.readFully`), matching this
 * codebase's existing T3 block-parsing discipline — [okio.ByteString] is materialized only for
 * markers with no dedicated type, where [Civ3RawSection]'s immutable domain value genuinely
 * requires one. The caller is expected to have already consumed the leading file magic.
 */
internal object Civ3RootParserImpl {
    fun parse(source: BufferedSource): Civ3File {
        val header = Civ3HeaderParser.parse(source)
        val sections = mutableListOf<Civ3Section>()
        while (!source.exhausted()) {
            sections += parseSection(source)
        }
        return Civ3File(header, sections)
    }

    private fun parseSection(source: BufferedSource): Civ3Section {
        val marker = source.readChunkId()
        val count = source.readIntLe()
        val items = List(count) {
            val length = source.readIntLe()
            val data = Buffer()
            source.readFully(data, length.toLong())
            data
        }
        return when (marker) {
            Civ3SectionIds.WSIZ -> WsizSection(items.map { WsizEntryParser.parse(it) })
            Civ3SectionIds.DIFF -> DiffSection(items.map { DiffEntryParser.parse(it) })
            Civ3SectionIds.ERAS -> ErasSection(items.map { ErasEntryParser.parse(it) })
            Civ3SectionIds.GOVT -> GovtSection(items.map { GovtEntryParser.parse(it) })
            else -> Civ3RawSection(marker, count, items.map { it.readByteString() })
        }
    }
}
