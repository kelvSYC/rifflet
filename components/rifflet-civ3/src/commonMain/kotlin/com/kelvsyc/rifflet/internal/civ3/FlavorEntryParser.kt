package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.FlavorEntry
import okio.BufferedSource

/**
 * Parses one flavor within a `FLAV` group, per the Apolyton BIX/BIQ format documentation.
 * Unlike every other `EntryParser` in this codebase, this reads directly off a live
 * [BufferedSource] rather than a pre-sliced [okio.Buffer] — `FLAV` items have no length prefix
 * of their own in the file format, so [com.kelvsyc.rifflet.internal.civ3.Civ3RootParserImpl]
 * cannot pre-slice them the way it does for every other section; see that file's `parseSection`
 * for the special-cased dispatch. Called repeatedly by [FlavGroupEntryParser], which reads the
 * flavor count that sizes each call.
 *
 * `numberOfRelations` is validated via [requireSaneCount] before sizing [FlavorEntry.relations]
 * — see that function's KDoc for why.
 */
internal object FlavorEntryParser {
    fun parse(source: BufferedSource): FlavorEntry {
        val unknown = source.readByteString(4L)
        val name = source.readByteString(256L).truncateAtFirstNull()
        val numberOfRelations = source.requireSaneCount(source.readIntLe(), 4L, "FlavorEntry.relations")
        val relations = List(numberOfRelations) { source.readIntLe() }
        return FlavorEntry(unknown, name, relations)
    }
}
