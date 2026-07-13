package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.RaceEraFilenames
import okio.Buffer

/**
 * Parses one `RACE_ERAS`-style filename pair (per the Apolyton BIX/BIQ format documentation),
 * 520 bytes: `forwardFilename` (260 bytes), `reverseFilename` (260 bytes). Reads directly off
 * [item], continuing whatever cursor position the caller (`RaceEntryParser`) has already reached
 * on the shared `RACE` record `Buffer`.
 */
internal object RaceEraFilenamesParser {
    fun parse(item: Buffer): RaceEraFilenames {
        val forwardFilename = item.readByteString(260L).truncateAtFirstNull()
        val reverseFilename = item.readByteString(260L).truncateAtFirstNull()
        return RaceEraFilenames(forwardFilename, reverseFilename)
    }
}
