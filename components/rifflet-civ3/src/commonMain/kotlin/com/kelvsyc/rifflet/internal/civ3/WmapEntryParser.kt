package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.WmapEntry
import okio.Buffer

/**
 * Parses one `WMAP` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop — `WMAP` retains a normal length prefix, unlike `FLAV`.
 *
 * `numberOfResources` is validated via [requireSaneCount] before sizing
 * [WmapEntry.resourceIds] — see that function's KDoc for why.
 *
 * Every field in this section is present regardless of [Civ3FormatEra].
 */
internal object WmapEntryParser {
    fun parse(item: Buffer): WmapEntry {
        val numberOfResources = item.requireSaneCount(item.readIntLe(), 4L, "WmapEntry.resourceIds")
        val resourceIds = List(numberOfResources) { item.readIntLe() }
        val numberOfContinents = item.readIntLe()
        val height = item.readIntLe()
        val distanceBetweenCivs = item.readIntLe()
        val numberOfCivs = item.readIntLe()
        val unknown1 = item.readByteString(8L)
        val width = item.readIntLe()
        val unknown2 = item.readByteString(128L)
        val mapSeed = item.readIntLe()
        val flags = item.readIntLe()
        return WmapEntry(
            resourceIds,
            numberOfContinents,
            height,
            distanceBetweenCivs,
            numberOfCivs,
            unknown1,
            width,
            unknown2,
            mapSeed,
            flags,
        )
    }
}
