package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.FlavGroupEntry
import okio.BufferedSource

/**
 * Parses one `FLAV` flavor group, per the Apolyton BIX/BIQ format documentation: a 4-byte
 * `numberOfFlavors` count immediately followed by that many flavors, each parsed by
 * [FlavorEntryParser]. This is the nesting level `Civ3RootParserImpl`'s FLAV special case
 * invokes once per section-level item (typically once per file, since the outer "number of
 * flavorgroups" count is almost always 1) — confirmed against a real Conquests scenario file,
 * where `numberOfFlavors=7` decodes cleanly into 7 named flavors before landing exactly on the
 * next section's marker with zero byte drift.
 */
internal object FlavGroupEntryParser {
    fun parse(source: BufferedSource): FlavGroupEntry {
        val numberOfFlavors = source.readIntLe()
        val flavors = List(numberOfFlavors) { FlavorEntryParser.parse(source) }
        return FlavGroupEntry(flavors)
    }
}
