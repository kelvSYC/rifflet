package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3File
import com.kelvsyc.rifflet.civ3.Civ3RawSection
import com.kelvsyc.rifflet.civ3.Civ3Section
import com.kelvsyc.rifflet.internal.core.readChunkId
import okio.BufferedSource

/**
 * Parses the flat, non-nested sequence of Civ3 sections that follows the `VER#` header: each
 * section is a 4-byte marker, a little-endian 4-byte item count, then that many length-prefixed
 * items. No section type beyond `VER#` is modeled yet, so every section is wrapped as a
 * [Civ3RawSection]. The caller is expected to have already consumed the leading file magic.
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
            source.readByteString(length.toLong())
        }
        return Civ3RawSection(marker, count, items)
    }
}
