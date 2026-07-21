package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3Header
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.core.readChunkId
import okio.BufferedSource

/**
 * Parses the `VER#` section that begins every Civ3 BIC/BIX/BIQ file (immediately after the
 * leading file magic), per existing reverse-engineering documentation of the BIX/BIQ format:
 *
 * ```
 * 4 char "VER#"
 * 4 long number of headers (1)
 * 4 long length of header (720)
 * 4 long 0
 * 4 long 0
 * 4 long major version number
 * 4 long minor version number
 * 640 string description
 * 64 string title
 * ```
 *
 * All numeric fields are little-endian, matching Civ3's native x86 Windows origin (the same
 * convention this codebase already applies to the similarly PC-native RIFF format). Every
 * documented Civ3 file has exactly one header record; a header count other than 1 is treated as
 * malformed input rather than modeled as a list. Description and title are fixed-width fields
 * that terminate at the first null byte, with the remainder of the field being unspecified
 * padding.
 */
internal object Civ3HeaderParser {
    private val MARKER = ChunkId("VER#")
    private const val EXPECTED_HEADER_LENGTH = 720
    private const val DESCRIPTION_SIZE = 640L
    private const val TITLE_SIZE = 64L

    fun parse(source: BufferedSource): Civ3Header {
        val marker = source.readChunkId()
        if (marker != MARKER) throw RiffletParseException("Expected VER# section, found ${marker.name}")

        val headerCount = source.readIntLe()
        if (headerCount != 1) throw RiffletParseException("Expected exactly one VER# header, found $headerCount")

        val headerLength = source.readIntLe()
        if (headerLength != EXPECTED_HEADER_LENGTH) {
            throw RiffletParseException("Expected VER# header length $EXPECTED_HEADER_LENGTH, found $headerLength")
        }

        source.readIntLe() // reserved, always 0
        source.readIntLe() // reserved, always 0
        val major = source.readIntLe()
        val minor = source.readIntLe()
        val description = source.readByteString(DESCRIPTION_SIZE).truncateAtFirstNull()
        val title = source.readByteString(TITLE_SIZE).truncateAtFirstNull()
        return Civ3Header(major, minor, description, title)
    }
}
