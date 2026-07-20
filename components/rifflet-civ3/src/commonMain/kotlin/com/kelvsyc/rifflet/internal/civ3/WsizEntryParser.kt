package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.WsizEntry
import okio.Buffer

/**
 * Parses one `WSIZ` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop (`Civ3RootParserImpl.parseSection`) — no [okio.ByteString] materialization
 * needed for a modeled section.
 *
 * Every field in this section is present regardless of [Civ3FormatEra].
 */
internal object WsizEntryParser {
    fun parse(item: Buffer): WsizEntry {
        val optimalNumberOfCities = item.readIntLe()
        val techRate = item.readIntLe()
        val reserved = item.readByteString(24L)
        val name = item.readByteString(32L).truncateAtFirstNull()
        val height = item.readIntLe()
        val distanceBetweenCivs = item.readIntLe()
        val numberOfCivs = item.readIntLe()
        val width = item.readIntLe()
        return WsizEntry(optimalNumberOfCities, techRate, reserved, name, height, distanceBetweenCivs, numberOfCivs, width)
    }
}
