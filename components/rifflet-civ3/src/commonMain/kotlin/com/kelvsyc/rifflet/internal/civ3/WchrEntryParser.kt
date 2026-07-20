package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.WchrEntry
import okio.Buffer

/**
 * Parses one `WCHR` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 *
 * Every field in this section is present regardless of [Civ3FormatEra].
 */
internal object WchrEntryParser {
    fun parse(item: Buffer): WchrEntry {
        val selectedClimate = item.readIntLe()
        val actualClimate = item.readIntLe()
        val selectedBarbarianActivity = item.readIntLe()
        val actualBarbarianActivity = item.readIntLe()
        val selectedLandform = item.readIntLe()
        val actualLandform = item.readIntLe()
        val selectedOceanCoverage = item.readIntLe()
        val actualOceanCoverage = item.readIntLe()
        val selectedTemperature = item.readIntLe()
        val actualTemperature = item.readIntLe()
        val selectedAge = item.readIntLe()
        val actualAge = item.readIntLe()
        val worldSize = item.readIntLe()
        return WchrEntry(
            selectedClimate,
            actualClimate,
            selectedBarbarianActivity,
            actualBarbarianActivity,
            selectedLandform,
            actualLandform,
            selectedOceanCoverage,
            actualOceanCoverage,
            selectedTemperature,
            actualTemperature,
            selectedAge,
            actualAge,
            worldSize,
        )
    }
}
