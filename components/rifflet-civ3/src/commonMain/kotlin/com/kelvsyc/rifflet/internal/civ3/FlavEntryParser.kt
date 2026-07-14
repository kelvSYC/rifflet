package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.FlavEntry
import okio.BufferedSource

/**
 * Parses one `FLAV` item, per the Apolyton BIX/BIQ format documentation. Unlike every other
 * `EntryParser` in this codebase, this reads directly off a live [BufferedSource] rather than a
 * pre-sliced [okio.Buffer] — `FLAV` items have no length prefix of their own in the file format,
 * so [com.kelvsyc.rifflet.internal.civ3.Civ3RootParserImpl] cannot pre-slice them the way it
 * does for every other section; see that file's `parseSection` for the special-cased dispatch.
 */
internal object FlavEntryParser {
    fun parse(source: BufferedSource): FlavEntry {
        val unknown = source.readByteString(4L)
        val name = source.readByteString(256L).truncateAtFirstNull()
        val numberOfFlavors = source.readIntLe()
        val flavorRelationships = List(numberOfFlavors) { source.readIntLe() }
        return FlavEntry(unknown, name, flavorRelationships)
    }
}
