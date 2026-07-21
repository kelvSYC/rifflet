package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.FlavGroupEntry
import okio.BufferedSource

/**
 * Parses one `FLAV` flavor group, per existing reverse-engineering documentation of the BIX/BIQ format: a 4-byte
 * `numberOfFlavors` count immediately followed by that many flavors, each parsed by
 * [FlavorEntryParser]. This is the nesting level `Civ3RootParserImpl`'s FLAV special case
 * invokes once per section-level item — typically once per file, since the outer "number of
 * flavorgroups" count is almost always 1.
 *
 * `numberOfFlavors` is validated via [requireSaneCount] before sizing [FlavGroupEntry.flavors] —
 * see that function's KDoc for why. `264L` is [FlavorEntry]'s own minimum possible byte width
 * (4-byte `unknown` + 256-byte `name` + a 4-byte relations count, with zero relations).
 *
 * Every field in this section is present regardless of [Civ3FormatEra].
 */
internal object FlavGroupEntryParser {
    fun parse(source: BufferedSource): FlavGroupEntry {
        val numberOfFlavors = source.requireSaneCount(source.readIntLe(), 264L, "FlavGroupEntry.flavors")
        val flavors = List(numberOfFlavors) { FlavorEntryParser.parse(source) }
        return FlavGroupEntry(flavors)
    }
}
